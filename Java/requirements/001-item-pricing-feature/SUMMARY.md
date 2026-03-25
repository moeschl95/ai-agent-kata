# 001 — Item Pricing Feature — Implementation Summary

**Date:** 2026-03-25
**Model:** Claude Sonnet 4.5

---

## What Was Implemented

A pricing system was added to the Gilded Rose shop via two new classes. The `PricingService` interface
exposes a single `priceFor(Item item)` method, and `DefaultPricingService` implements it: it returns
a configurable fixed legendary price for Sulfuras, a discounted price (base × (1 − discountRate)) for
expired items (sellIn < 0), and the plain base price for all other in-date items. Base prices, the
discount rate, and the legendary price are all injected via the constructor — no magic numbers.

---

## Problems Addressed During Development

- `Item.java` is immutable by kata rules, so base prices cannot be stored on the item itself; a
  `Map<String, Integer>` keyed by item name is passed to `DefaultPricingService` at construction time.
- The boundary condition `sellIn == 0` was explicitly tested to confirm it is treated as in-date
  (discount only applies when `sellIn < 0`), matching the requirement wording.
- `ItemUpdaterFactory.SULFURAS` is reused as the legendary-item name constant, avoiding duplication
  between the quality-update and pricing concerns.

---

## Files Changed

- `src/main/java/com/gildedrose/PricingService.java` — new interface with `int priceFor(Item item)`
- `src/main/java/com/gildedrose/DefaultPricingService.java` — new implementation of `PricingService`
- `src/test/java/com/gildedrose/PricingServiceTest.java` — 6 unit tests covering all acceptance criteria
