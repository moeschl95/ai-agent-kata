# 004 — REST API for Shop Operations

**Status:** done

---

## Description

The Gilded Rose shop needs a REST API so external clients can inspect inventory, retrieve item
prices, and advance the shop by one day. The Spring Boot foundation from task 003 is already in
place; this task adds a thin HTTP layer on top of the existing `GildedRose` and `PricingService`
beans without touching any domain classes.

---

## Implementation Plan

1. **Write failing `@WebMvcTest` tests (Red)** in a new `ShopControllerTest.java`:
   - `GET /items` returns HTTP 200 with a JSON array of all items (name, sellIn, quality).
   - `POST /items/advance-day` calls `updateQuality()` and returns HTTP 200 with the updated item list.
   - `GET /items/{name}/price` returns HTTP 200 with the price as a JSON number for a known item.
   - `GET /items/{name}/price` returns HTTP 404 for an unknown item name.

2. **Create `ItemDto.java`** — a Java record (`record ItemDto(String name, int sellIn, int quality)`)
   that acts as the HTTP response model, keeping `Item` out of the HTTP layer.

3. **Create `ShopController.java`** (`@RestController`, `@RequestMapping("/items")`):
   - Inject `GildedRose` and `PricingService` via constructor injection.
   - `GET /items` — map `GildedRose.items` to `List<ItemDto>` and return.
   - `POST /items/advance-day` — call `GildedRose.updateQuality()`, then return updated `List<ItemDto>`.
   - `GET /items/{name}/price` — delegate to `PricingService.priceFor(...)` for the first item
     matching `{name}`; return 404 via `ResponseEntity` if no match is found.

4. **Add `spring-boot-starter-web` dependency** to both `pom.xml` and `build.gradle.kts`
   (currently only `spring-boot-starter` is present).

5. **Run tests (Green)** — all new controller tests pass and all existing tests continue to pass.

6. **Refactor** if needed — ensure `ShopController` is thin (no business logic), and the DTO
   mapping is extracted to a private helper method.

---

## Acceptance Criteria

- [ ] `GET /items` returns HTTP 200 with a JSON array; each element has `name`, `sellIn`, and `quality` fields.
- [ ] `POST /items/advance-day` advances the shop by one day and returns the updated inventory as JSON.
- [ ] `GET /items/{name}/price` returns HTTP 200 and the correct integer price for a known item name.
- [ ] `GET /items/{name}/price` returns HTTP 404 for an item name not present in the inventory.
- [ ] All new behaviour is covered by `@WebMvcTest` slice tests written before production code (TDD).
- [ ] All existing unit and integration tests continue to pass.
- [ ] `Item.java` is not modified.
- [ ] No business logic leaks into `ShopController` — it only orchestrates and maps.

---

## Notes

No conflicts with tasks 001–003 (all `done`).

**Conflict noted with 005 (`005-item-state-projection`, status: `funnel`)**: task 005 adds a
projection endpoint to the `ShopController` introduced here. Implement task 004 first to avoid
merge conflicts in `ShopController` and its `@WebMvcTest` test class.

The `PricingService.priceFor(Item)` signature requires an `Item` instance, so the price endpoint
will look up the item by name in `GildedRose.items` before calling the service. If the shop holds
multiple items with the same name, the first match is used.

**Conflict noted with 006 (`006-in-memory-item-persistence`, status: `funnel`)**: task 006
introduces a Spring Data JPA repository that `GildedRose` uses to load and persist items.
`@WebMvcTest` slice tests for this task mock `GildedRose` and are unaffected. Full integration
tests, however, will require the repository to be seeded. Task 006 should be implemented
**before** task 004 to avoid rework at the integration-test level.

---

## Changelog

| Date | Status | Note |
|------|--------|------|
| 2026-03-25 | funnel | Task created |
| 2026-03-25 | ready-for-development | Approved by user |
| 2026-03-25 | ready-for-development | Conflict noted with 005-item-state-projection: both touch ShopController |
| 2026-03-25 | ready-for-development | Conflict noted with 006-in-memory-item-persistence: integration tests depend on persistence layer |
| 2026-03-25 | in-progress | Implementation started |
| 2026-03-25 | implemented | ShopController, ItemDto, and web tests implemented; 49 tests pass |
| 2026-03-25 | done | Accepted by user |
