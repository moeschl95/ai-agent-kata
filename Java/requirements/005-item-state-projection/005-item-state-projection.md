# 005 — Item State Projection

**Status:** ready-for-development

---

## Description

Users need to simulate what state an item will be in after a given number of days — without
mutating the real inventory. Given an item name and a number of days `n`, the system should return
the projected `sellIn` and `quality` values as they would be after `n` daily `updateQuality` ticks.
This is a read-only, non-destructive operation: the live shop state must never be modified.

---

## Implementation Plan

1. **Write failing tests (Red)**:
   - Unit tests for a new `ProjectionService.project(Item item, int days)` method:
     - Projects a normal item forward by `n` days.
     - Projects Aged Brie, Backstage Passes, Sulfuras, and Conjured items correctly.
     - Returns the original state when `days = 0`.
     - Handles `days` larger than `sellIn` (item expires mid-projection).
   - `@WebMvcTest` slice tests for the new REST endpoint (depends on task 004 being in place):
     - `GET /items/{name}/projection?days=n` returns HTTP 200 with projected `name`, `sellIn`, `quality`.
     - Returns HTTP 404 when the item name is not found in the inventory.
     - Returns HTTP 400 when `days` is negative.

2. **Create `ProjectionService.java`** (a Spring `@Service`):
   - Accepts an `Item` and an `int days` parameter.
   - Deep-copies the item to avoid mutating the original, then runs `updateQuality` logic
     `n` times on the copy using the existing `ItemUpdaterFactory`.
   - Returns an `ItemDto` (or a dedicated `ProjectionDto`) with the projected state.

3. **Extend `ShopController`** (introduced in task 004) with the new endpoint:
   - `GET /items/{name}/projection?days=n`
   - Validates that `days >= 0`; returns `400 Bad Request` otherwise.
   - Looks up the first item matching `{name}` in `GildedRose.items`; returns `404` if not found.
   - Delegates projection to `ProjectionService` and returns the result as JSON.

4. **Run tests (Green)** — all new and existing tests pass.

5. **Refactor** — ensure `ProjectionService` contains all projection logic and the controller
   remains orchestration-only. Consider a dedicated `ProjectionDto` if the response shape
   diverges from `ItemDto` in the future.

---

## Acceptance Criteria

- [ ] `GET /items/{name}/projection?days=5` returns HTTP 200 with the item's projected `name`, `sellIn`, and `quality` after 5 days.
- [ ] The live shop inventory is not mutated by a projection request.
- [ ] `days=0` returns the item's current state unchanged.
- [ ] Returns HTTP 404 when no item with the given name exists in the inventory.
- [ ] Returns HTTP 400 when `days` is negative.
- [ ] Projection is correct for all item types: Normal, Aged Brie, Backstage Passes, Sulfuras, Conjured.
- [ ] All new behaviour is covered by tests written before production code (TDD).
- [ ] All existing tests continue to pass.
- [ ] `Item.java` is not modified.

---

## Notes

### Conflicts

**Conflict with task 004 (`004-rest-api-shop-operations`, status: `ready-for-development`)**:
Both tasks touch `ShopController`. Task 004 introduces the controller; task 005 adds a new
endpoint to it. To avoid conflicts, task 005 must be implemented **after** task 004 is done.
If implemented concurrently, the `ShopController` and `@WebMvcTest` test class will require
careful merging.

### Implementation note
`ProjectionService` should work by creating a temporary `GildedRose` instance (or directly using
`ItemUpdaterFactory`) with a defensive copy of the target item, running the updater `n` times,
and returning the result — keeping the original `GildedRose` bean's state untouched.

---

## Changelog

| Date | Status | Note |
|------|--------|------|
| 2026-03-25 | funnel | Task created |
| 2026-03-25 | ready-for-development | Approved by user |
