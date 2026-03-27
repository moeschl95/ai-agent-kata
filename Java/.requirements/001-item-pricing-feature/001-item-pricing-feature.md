# 001 — Item Pricing Feature

**Status:** done

---

## Description

The shop needs a pricing system for its inventory. Every item has a base price set by the shopkeeper. Legendary items (Sulfuras) are always sold at a fixed premium price regardless of their base price. Items that are past their sell-by date receive an automatic discount applied to their base price.

---

## Implementation Plan

1. **Write failing tests** for a new `PricingService` class (TDD red step):
   - A normal item in-date returns its base price.
   - A normal item past its sell-by date (sellIn < 0) returns base price × (1 − discountRate).
   - Sulfuras always returns the fixed premium price (independent of any base price).

2. **Create `PricingService` interface** (`src/main/java/com/gildedrose/PricingService.java`):
   - Single method: `int priceFor(Item item)`.

3. **Create `DefaultPricingService`** that implements `PricingService`:
   - Constructor accepts a `Map<String, Integer> basePrices`, a `double expiredDiscountRate` (e.g. 0.5 for 50 % off), and a `int legendaryPrice` (the fixed Sulfuras premium).
   - Uses `ItemUpdaterFactory.SULFURAS` constant to identify legendary items.
   - Returns `legendaryPrice` for Sulfuras.
   - Returns `Math.round(basePrice * (1 - expiredDiscountRate))` for expired items (sellIn < 0).
   - Returns `basePrice` for all other items.

4. **Make all new tests pass** (TDD green step).

5. **Refactor** if needed — ensure no duplication with existing updater logic; keep pricing concern separate from quality-update concern (single responsibility).

6. **Run full test suite** (`gradlew test`) to confirm no regressions.

---

## Acceptance Criteria

- [ ] A normal item with a positive `sellIn` value is priced at its exact base price.
- [ ] A normal item with a negative `sellIn` value (expired) is priced at a discount below its base price.
- [ ] Sulfuras is always priced at the fixed legendary premium, regardless of any base price supplied.
- [ ] Pricing logic is fully unit-tested following the `should_<behavior>_when_<condition>` naming convention.
- [ ] `Item.java` is **not modified**.
- [ ] All existing tests continue to pass.

---

## Notes

- `Item.java` is immutable by the kata rules; base prices must be stored outside the `Item` class (e.g. a `Map<String, Integer>` keyed by item name).
- The discount rate and legendary price should be configurable via the `DefaultPricingService` constructor — no magic numbers in business logic.
- Re-use `ItemUpdaterFactory.SULFURAS` for the legendary item name constant to avoid duplication.

---

## Changelog

| Date | Status | Note |
|------|--------|------|
| 2026-03-25 | funnel | Task created |
| 2026-03-25 | ready-for-development | Approved by user |
| 2026-03-25 | ready-for-development | Conflict noted with 002-dual-type-conjured-items: both tasks touch ItemUpdaterFactory.java (002 adds forItem() logic; 001 reads SULFURAS constant only — no direct conflict, coordinate if worked simultaneously) |
| 2026-03-25 | in-progress | Implementation started |
| 2026-03-25 | implemented | PricingService interface and DefaultPricingService created; 6 tests passing, full suite green |
| 2026-03-25 | done | Accepted by user |
