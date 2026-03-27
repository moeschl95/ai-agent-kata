# 017 — Inventory Price Column

**Status:** done

---

## Description

The inventory list page (task 009) displays item details but does not show the price for each item. Users must click on an item or navigate to a separate screen to see pricing. This task adds a price column to the inventory datagrid so users can see the cost of each item at a glance. The price column is read-only and not sortable, to keep the UI simple and focus sorting on the properties that change daily (name, sell-in, quality).

---

## Implementation Plan

1. **Update ItemDto** in the backend to include a `price` field:
   - Modify `ItemDto.java` to add `private BigDecimal price;` with getter.
   - Update the mapping logic in the controller or service to populate the price field from the `PricingService` when retrieving items.

2. **Update REST API** (`GET /api/items`):
   - Ensure the response payload includes the `price` field for each item in the list.
   - The price should be calculated correctly for each item type (base, Sulfuras premium, expired discount) at the point of serialization.

3. **Add price column to the Clarity datagrid** in the `InventoryListComponent` (frontend):
   - Add a new `<clr-dg-column>` for price in the template.
   - Bind to the `item.price` property.
   - Set `[sortable]="false"` on the column to prevent sorting on price.
   - Format the price display as currency (e.g., `{{ item.price | currency }}`).

4. **Update the `Item` shared model** in the frontend (if needed):
   - If `ItemDto` in backend is changed, regenerate or manually update the `Item` interface in `frontend/src/app/shared/models/item.model.ts` to include the `price` field.

5. **Write tests**:
   - Backend: Add unit test to `ShopControllerTest` verifying that `GET /api/items` includes price in the response payload for a variety of item types.
   - Frontend: Add unit test to `InventoryListComponent.spec.ts` verifying that the price column is present in the DOM and displays the price value correctly for each item.
   - Verify the price column header shows "Price" or similar.

6. **Verify in UI**:
   - Run the Angular dev server and the Spring Boot app.
   - Open the inventory page and confirm the price column appears and displays correct prices for all item types.

---

## Acceptance Criteria

- [ ] `ItemDto` includes a `price` field of type `BigDecimal`.
- [ ] `GET /api/items` response includes the `price` field for each item, with correct values for all item types.
- [ ] Frontend `Item` interface includes the `price` field.
- [ ] Inventory datagrid displays a "Price" column with values formatted as currency.
- [ ] The price column is not sortable (has `[sortable]="false"` or equivalent).
- [ ] Unit test in `ShopControllerTest` verifies prices appear in the API response.
- [ ] Unit test in `InventoryListComponent.spec.ts` verifies the price column renders and displays correct values.
- [ ] All existing tests continue to pass.
- [ ] No breaking changes to the REST API or domain logic.

---

## Notes

- The price should reflect the current state at the time of the request (i.e., same logic as the `GET /items/{name}/price` endpoint from task 004).
- If prices change when `POST /items/advance-day` is called, the next `GET /api/items` should reflect the updated prices.
- The column should be read-only; prices are not edited from the inventory view.
- Currency formatting (if used) should be locale-aware or consistent with the app's preferred currency.

---

## Changelog

| Date | Status | Note |
|------|--------|------|
| 2026-03-26 | funnel | Task created |
| 2026-03-26 | ready-for-development | Approved by user |
| 2026-03-26 | in-progress | Backend: Updated ItemDto, ShopService, ProjectionService to include price; all backend tests passing |
| 2026-03-26 | implemented | Complete: Backend returns price in API response; Frontend displays price column with currency format; all backend tests passing |
| 2026-03-27 | done | User accepted implementation |
