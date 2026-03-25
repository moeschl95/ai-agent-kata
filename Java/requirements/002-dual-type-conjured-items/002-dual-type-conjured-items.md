# 002 — Dual-Type Conjured Items

**Status:** done

---

## Description

Some items in the shop are both Conjured and a recognised special type — for example "Conjured Aged
Brie" or "Conjured Backstage passes to a TAFKAL80ETC concert". These items should follow the base
type's quality rules (Brie increases, Backstage Pass uses tiered increases, etc.) while the Conjured
modifier doubles the magnitude of every quality change. This gives the game meaningful dual-type
mechanics without breaking any existing item behaviour.

---

## Implementation Plan

1. **Write failing tests (Red)** for all dual-type scenarios in `GildedRoseTest.java`:
   - `Conjured Aged Brie` — quality increases by **+2** (doubled from +1) before sell-by, **+4** after, capped at 50.
   - `Conjured Backstage passes to a TAFKAL80ETC concert` — quality increases at doubled tiers:
     **+2** (>10 days), **+4** (≤10 days), **+6** (≤5 days), drops to **0** after the concert.
   - Both items: `sellIn` decrements each day.
   - Both items: quality never exceeds 50 and never drops below 0.

2. **Create `ConjuredDecorator.java`** (new class, `src/main/java/com/gildedrose/`):
   - Implements `ItemUpdater`.
   - Wraps another `ItemUpdater` (the base-type updater).
   - Delegates to the wrapped updater, records the quality delta, then applies the same delta a
     second time (clamped to `[0, 50]`).
   - Zero-delta items (e.g. Sulfuras) remain unaffected.

3. **Update `ItemUpdaterFactory.forItem()`** to detect dual-type Conjured items:
   - If the name starts with the `CONJURED_PREFIX + " "` and the remaining base name matches Aged
     Brie or Backstage Pass, return `new ConjuredDecorator(BRIE)` or `new ConjuredDecorator(PASS)`.
   - All other `Conjured*` names fall through to the existing `ConjuredItemUpdater` (unchanged
     behaviour for plain Conjured items).
   - Extract a private helper `forBaseName(String baseName)` to keep `forItem()` readable.

4. **Run tests (Green)** — confirm all new tests pass; confirm all existing 22 tests still pass.

5. **Refactor** if needed — e.g. rename constants, adjust visibility, reduce duplication.

6. **Run full test suite** (`gradlew test`) before marking task implemented.

---

## Acceptance Criteria

- [ ] `"Conjured Aged Brie"` with a positive `sellIn` gains **+2** quality per day.
- [ ] `"Conjured Aged Brie"` with a negative `sellIn` (past sell-by) gains **+4** quality per day.
- [ ] `"Conjured Aged Brie"` quality never exceeds **50**.
- [ ] `"Conjured Backstage passes to a TAFKAL80ETC concert"` gains **+2** quality when >10 days remain.
- [ ] `"Conjured Backstage passes to a TAFKAL80ETC concert"` gains **+4** quality when ≤10 days remain.
- [ ] `"Conjured Backstage passes to a TAFKAL80ETC concert"` gains **+6** quality when ≤5 days remain.
- [ ] `"Conjured Backstage passes to a TAFKAL80ETC concert"` quality drops to **0** after the concert (sellIn < 0).
- [ ] `"Conjured Backstage passes to a TAFKAL80ETC concert"` quality never exceeds **50**.
- [ ] `sellIn` decrements by 1 each day for all dual-type Conjured items.
- [ ] Plain Conjured items (e.g. `"Conjured Mana Cake"`) continue to behave exactly as before (-2/-4).
- [ ] All 22 existing tests continue to pass.
- [ ] `Item.java` is **not modified**.
- [ ] New tests follow the `should_<behavior>_when_<condition>` naming convention.

---

## Notes

### Doubling semantics

The Conjured modifier **doubles the quality delta** produced by the base-type updater, consistent
with how plain Conjured items already work (normal items degrade at −1, plain Conjured at −2).

For Aged Brie: base delta = +1 (before sell-by), doubled → +2.  
For Backstage Pass at ≤5 days: base delta = +3, doubled → +6.

> **Please confirm this interpretation when approving.** An alternative reading is that the Conjured
> modifier *halves* the increase (e.g. Aged Brie: +1 → +0 or +1 stays). The acceptance criteria
> above assume "doubles". Reply with your preferred semantics if different.

### Design: Decorator pattern

`ConjuredDecorator` wraps any `ItemUpdater`. Because the base updater already clamps quality to
`[0, 50]`, the decorator must re-clamp after applying the second delta. This correctly handles all
boundary cases (quality at 49, quality at 1 after sell-by, etc.) without introducing rounding.

### Conflicts

| Task | Overlap |
|------|---------|
| 001-item-pricing-feature | Both tasks touch `ItemUpdaterFactory.java`. Task 001 reads the `SULFURAS` constant; task 002 adds logic to `forItem()`. No merge conflict, but coordinate if both are worked on simultaneously. |
| 003-spring-boot-migration | Task 003 creates a Spring `@Configuration` that wires `GildedRose` (which internally uses `ItemUpdaterFactory`) as a Spring bean. Task 002's changes to `forItem()` must be present in the codebase before 003 is built. Sequencing dependency only — no code conflict expected. |

---

## Changelog

| Date | Status | Note |
|------|--------|------|
| 2026-03-25 | funnel | Task created |
| 2026-03-25 | ready-for-development | Approved by user |
| 2026-03-25 | in-progress | Implementation started |
| 2026-03-25 | implemented | ConjuredDecorator pattern implemented; all 34 tests pass |
| 2026-03-25 | done | Approved by user; 97.5% line coverage, 92.5% branch coverage |
| 2026-03-25 | implemented | Conflict noted with 003-spring-boot-migration: Spring wiring depends on ItemUpdaterFactory changes from this task |
