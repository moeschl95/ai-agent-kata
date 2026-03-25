# Task 013 — Spring Event Bus

**Status:** done
**Created:** 2026-03-25

---

## Description

Introduce a Spring-based domain event system so that meaningful shop domain events can be
published as the inventory advances through each day. No listeners are required by this task —
the infrastructure and the first set of meaningful events are the scope. Downstream features
will subscribe to these events.

### Events to publish

| Event | Trigger |
|-------|---------|
| `DayAdvancedEvent` | Fired once after all items have been updated by `updateQuality()` |
| `ItemExpiredEvent` | Fired for each item whose quality reaches 0 during a day advance |
| `ItemQualityChangedEvent` | Fired for each item whose quality changes to a non-zero value |

---

## Implementation Plan

1. **Create event records** in `com.gildedrose` (or a sub-package):
   - `DayAdvancedEvent(List<Item> updatedItems)` — day-level event carrying the final inventory snapshot
   - `ItemExpiredEvent(String itemName)` — item-level event for quality reaching 0
   - `ItemQualityChangedEvent(String itemName, int previousQuality, int newQuality)` — item-level event for non-zero quality changes

2. **Extend `GildedRose`** with an `ApplicationEventPublisher` field:
   - The existing `GildedRose(List<Item> items)` constructor keeps `publisher = null` (event-free, used by unit tests and `ProjectionService`).
   - Add a new constructor `GildedRose(ItemRepository repository, ApplicationEventPublisher publisher)`.
   - In `updateQuality()`: capture each item's quality before the updater runs; compare after; publish the appropriate event(s); finally publish `DayAdvancedEvent`.
   - Guard every `publisher.publishEvent(...)` call with a null check (keeps non-Spring paths clean).

3. **Update `GildedRoseConfiguration`** to inject `ApplicationEventPublisher` into the `GildedRose` bean.

4. **TDD cycle** — write tests first:
   - Use a mocked `ApplicationEventPublisher` to verify events are published.
   - Cover: quality reaches 0 → `ItemExpiredEvent`; quality changes but not to 0 → `ItemQualityChangedEvent`; no change (Sulfuras) → no item event; `DayAdvancedEvent` always fired once.

---

## Acceptance Criteria

- [ ] `DayAdvancedEvent` is published exactly once per call to `updateQuality()` when a publisher is present.
- [ ] `ItemExpiredEvent` is published for each item whose quality transitions to 0.
- [ ] `ItemQualityChangedEvent` is published for each item whose quality changes to a non-zero value.
- [ ] No events are published when no publisher is wired (in-memory / test constructor path).
- [ ] Sulfuras (quality never changes) produces no item-level events.
- [ ] All existing tests continue to pass.

---

## Changelog

| Date | Status | Note |
|------|--------|------|
| 2026-03-25 | in-progress | Implementation started |
| 2026-03-25 | implemented | 3 event records + GildedRose publisher wiring; 10 new tests, 86 total, all green |
| 2026-03-25 | done | Accepted by user |
