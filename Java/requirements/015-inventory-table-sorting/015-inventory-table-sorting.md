# 015 — Inventory Table Sorting

**Status:** done

---

## Description

Users of the Gilded Rose shop UI need to be able to sort the inventory table by any column (Name, Sell In, Quality) so they can quickly locate and compare items. Currently rows appear in insertion order with no way to reorder them. Sorting is driven server-side: the frontend sends `sortBy` and `sortDir` query parameters to the REST API, which returns items already ordered. Clicking a column header toggles through ascending, descending, and unsorted (default order) states.

---

## Implementation Plan

### Backend (Java / Spring Boot)

1. **Update `ShopController.getItems()`** to accept `@RequestParam(required = false) String sortBy` and `@RequestParam(required = false) String sortDir` and pass them through to the service.
2. **Extend `ShopService.getAllItems()`** to accept the optional sort params. Build a `org.springframework.data.domain.Sort` instance from the validated params and call `itemRepository.findAll(Sort sort)` — `ItemRepository` already extends `JpaRepository` so this method is available with no additional code. When no params are provided, call `itemRepository.findAll()` for the default order. Validate `sortBy` against an allowlist (`name`, `sellIn`, `quality`); ignore unrecognised values.
3. **Write unit tests** in `ShopControllerTest` and `ShopServiceTest` covering:
   - `GET /api/items?sortBy=name&sortDir=asc` returns items sorted A→Z by name.
   - `GET /api/items?sortBy=sellIn&sortDir=desc` returns items sorted by sellIn descending.
   - `GET /api/items?sortBy=quality&sortDir=asc` returns items sorted by quality ascending.
   - `GET /api/items` (no params) returns items in default order.
   - An invalid `sortBy` value falls back to default order (no error, no 400).

### Frontend (Angular)

4. **Update `ShopService.getItems()`** in `shop.service.ts` to accept an optional `{ sortBy, sortDir }` argument and append it as query parameters to the `GET /api/items` request.
5. **Wire Clarity datagrid sort events** in `inventory.component.ts` — handle `(clrDgSortedChange)` or `(clrDgRefresh)` to detect the active sort state and re-call `load()` with the current sort params.
6. **Add `[clrDgSortBy]`** bindings on each `<clr-dg-column>` in `inventory.component.html` so Clarity renders sort chevrons and dispatches sort events.
7. **Write or update unit tests** in `inventory.component.spec.ts` to assert that when a sort event fires the service is called with the correct `sortBy`/`sortDir` parameters.

### Validation

8. Run `.\gradlew test` and `npm test -- --watch=false --browsers=ChromeHeadless` to confirm all tests pass.

---

## Acceptance Criteria

- [ ] `GET /api/items?sortBy=name&sortDir=asc` returns items sorted alphabetically by name.
- [ ] `GET /api/items?sortBy=name&sortDir=desc` returns items sorted reverse alphabetically.
- [ ] `GET /api/items?sortBy=sellIn&sortDir=asc` and `sortDir=desc` sort numerically.
- [ ] `GET /api/items?sortBy=quality&sortDir=asc` and `sortDir=desc` sort numerically.
- [ ] `GET /api/items` (no params) returns items in default (unsorted) order — no regression.
- [ ] Clicking a **Name**, **Sell In**, or **Quality** column header in the UI triggers a new API call with the correct sort parameters.
- [ ] Sort chevrons in the datagrid correctly reflect the active sort direction.
- [ ] A third click on a sorted column resets to unsorted (no sort params sent).
- [ ] All existing backend and frontend tests continue to pass.
- [ ] New unit tests cover the sort logic in the service, controller, and component.

---

## Notes

- Sorting is intentionally server-side to keep the frontend stateless about ordering and to support future pagination consistently.
- Sorting is performed at the **database level** via `JpaRepository.findAll(Sort sort)` — no in-memory `Comparator` or post-fetch sorting needed.
- The `sortBy` field names map to `ItemEntity` column names (`name`, `sellIn`, `quality`), which Spring Data passes directly to the SQL `ORDER BY` clause.
- An allowlist check in `ShopService` guards against `sortBy` values that could cause unexpected query behaviour.
- Tasks 011 and 012 also touch the inventory datagrid HTML and `ShopService` — minor merge consideration but no functional conflict.

---

## Changelog

| Date | Status | Note |
|------|--------|------|
| 2026-03-25 | funnel | Task created |
| 2026-03-25 | funnel | Revised to include server-side sorting — backend API changes added to plan |
| 2026-03-25 | funnel | Revised to use database-level sorting via JPA Sort instead of in-memory Comparator |
| 2026-03-25 | ready-for-development | Approved by user |
| 2026-03-25 | in-progress | Implementation started |
| 2026-03-25 | implemented | Backend and frontend sorting implemented and tested |
| 2026-03-25 | done | User approved; work complete |
