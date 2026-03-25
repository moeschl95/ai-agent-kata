# 013 — Spring Event Bus — Implementation Summary

**Date:** 2026-03-25
**Model:** Claude Sonnet 4.6

---

## What Was Implemented

Three immutable record-based domain events — `DayAdvancedEvent`, `ItemExpiredEvent`, and `ItemQualityChangedEvent` — were introduced in `com.gildedrose`. `GildedRose.updateQuality()` was extended to capture each item's quality before and after the updater runs, then publish the correct event per item; a single `DayAdvancedEvent` carrying the full post-update inventory is always fired last. `GildedRoseConfiguration` now injects Spring's `ApplicationEventPublisher` into the `GildedRose` bean so the full event pipeline is active in production.

---

## Problems Addressed During Development

- **Backward-compatible constructors** — the existing `GildedRose(ItemRepository)` constructor (used by integration tests) was removed by the first cut of the implementation. A delegating no-publisher overload was added to restore compatibility without duplicating logic.
- **`ItemQualityChangedEvent` vs `ItemExpiredEvent` boundary** — items expiring (quality → 0) and items merely changing quality needed to be mutually exclusive. The rule "publish `ItemExpiredEvent` when new quality is 0, `ItemQualityChangedEvent` otherwise" was applied, so listeners can react to each case independently without overlap.
- **No-publisher guard** — the in-memory `GildedRose(List<Item>)` constructor used by unit tests and `ProjectionService` carries a `null` publisher. Every publish call is null-checked so the domain class stays usable outside a Spring context.
- **`DayAdvancedEvent` timing** — the event must reference the final post-update item state; it is therefore fired after the full loop completes and (if present) after the repository save, ensuring consumers always see the persisted state.

---

## Files Changed

- `src/main/java/com/gildedrose/DayAdvancedEvent.java` — new record; fired once per `updateQuality()` call with the final inventory snapshot
- `src/main/java/com/gildedrose/ItemExpiredEvent.java` — new record; fired when an item's quality reaches 0
- `src/main/java/com/gildedrose/ItemQualityChangedEvent.java` — new record; fired when an item's quality changes to a non-zero value
- `src/main/java/com/gildedrose/GildedRose.java` — added `ApplicationEventPublisher` field; two new constructors; `updateQuality()` now captures pre/post quality and publishes events; private helper `publishItemEventIfChanged`
- `src/main/java/com/gildedrose/GildedRoseConfiguration.java` — `gildedRose` bean now accepts and passes `ApplicationEventPublisher`
- `src/test/java/com/gildedrose/GildedRoseEventTest.java` — new test class; 10 tests covering all event types, Sulfuras no-op, quality-at-zero edge case, multi-item expiry, and the null-publisher path
