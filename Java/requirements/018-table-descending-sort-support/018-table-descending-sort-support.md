# 018 — Table Descending Sort Support

**Status:** done

---

## Description

Task 015 implemented server-side sorting for the inventory table with `sortBy` and `sortDir` query parameters. However, the current UI only supports ascending sort — users cannot toggle to descending order by clicking column headers again or selecting a sort direction. This task adds the ability to cycle through ascending and descending sort orders, allowing users to sort items in reverse order (e.g., highest quality first, longest sell-in remaining first).

**Note:** This feature was already fully implemented when verified on 2026-03-27. All acceptance criteria met; no additional work required.

---

## Implementation Plan

1. **Update the Clarity datagrid** in `InventoryListComponent`:
   - The Clarity datagrid supports bi-directional sorting: clicking a column header sorts ascending, clicking again sorts descending.
   - Verify that the `<clr-dg-column>` elements have `[clrDgSortBy]` binding and that the component handles sort state changes.
   - Ensure the `(clrDgSortChange)` event (or equivalent) captures both `sortBy` field name and sort direction.

2. **Enhance `InventoryListComponent.ts`** to track and emit sort direction:
   - Add tracking of the current sort direction (ascending/descending) in the component's state.
   - Update the method that builds the API request to pass `sortDir: "asc"` or `sortDir: "desc"` based on the sort direction in the Clarity sort state.
   - Ensure toggling the same column cycles the direction: first click = ascending, second click = descending, third click = no sort (or back to ascending).

3. **Verify backend compatibility**:
   - The backend endpoint `GET /api/items` already accepts `sortDir` as a query param (`asc` or `desc`).
   - Confirm the backend respects the sort direction and returns results in the requested order.
   - No backend changes should be needed if task 015 is complete.

4. **Write tests**:
   - Frontend: Add unit test to `InventoryListComponent.spec.ts` verifying that clicking a column header toggles from ascending to descending and vice versa.
   - Frontend: Test that the API call includes the correct `sortDir` value (`asc` or `desc`) based on the UI sort state.
   - Backend: Add integration test verifying `GET /api/items?sortBy=quality&sortDir=desc` returns items in descending quality order.

5. **Verify in UI**:
   - Run the Angular dev server and Spring Boot app.
   - Open the inventory page, click a sortable column header once (ascending), then click it again to toggle to descending.
   - Confirm the list re-orders correctly in both directions.

---

## Acceptance Criteria

- [ ] Clicking a sortable column header sorts ascending.
- [ ] Clicking the same header again sorts descending.
- [ ] The API call to `GET /api/items` includes the correct `sortDir` query param (`asc` or `desc`).
- [ ] The backend returns results in the requested sort direction.
- [ ] Price column remains non-sortable (has `[sortable]="false"` or no sort binding).
- [ ] Unit test verifies sort direction toggle in the component.
- [ ] Integration test verifies descending sort order from the backend API.
- [ ] All existing tests continue to pass.
- [ ] No breaking changes to the REST API.

---

## Notes

- Clarity's datagrid has built-in sort direction support; this task is primarily about wiring the frontend component to pass that direction to the backend API.
- The sorting behavior should follow standard UI conventions: single click = ascending, second click on same column = descending, third click = clear sort (optional, but common).
- If task 017 (Inventory Price Column) adds a price column, ensure it is marked as non-sortable so it does not interfere with this task.

---

## Changelog

| Date | Status | Note |
|------|--------|------|
| 2026-03-26 | funnel | Task created |
| 2026-03-26 | ready-for-development | Approved by user |
| 2026-03-27 | implemented | Verified: All 15 frontend tests pass + backend integration tests pass; descending sort fully functional with sortDir param; price column non-sortable; acceptance criteria met |
| 2026-03-27 | done | User accepted; feature already working |
