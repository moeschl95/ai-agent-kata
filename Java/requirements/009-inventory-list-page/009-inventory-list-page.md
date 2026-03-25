# 009 — Inventory List Page

**Status:** done

---

## Description

With the Angular shell in place (task 008), the shop needs an inventory page that shows all
items in the live shop. Users should see each item's name, days remaining to sell (`sellIn`),
and current quality in a clear, scannable table. The page must load inventory from the backend
when it opens and display a loading indicator while the network request is in flight.

---

## Implementation Plan

1. **Write a failing component test (Red)** in `inventory.component.spec.ts`:
   - `should_displayItems_when_itemsAreLoaded` — mock `ShopService.getItems()` to return two items;
     assert that the rendered HTML contains both item names.
   - `should_showLoadingSpinner_when_requestIsInFlight` — mock `getItems()` to return a never-resolving
     observable; assert `<clr-spinner>` is visible.
   - `should_showErrorAlert_when_getItemsFails` — mock `getItems()` to throw an error; assert a
     `<clr-alert>` with type `"danger"` is in the DOM.
   Run the tests — they fail because the component is currently a placeholder.

2. **Implement `InventoryComponent`** (`src/app/features/inventory/`):
   - Standalone component that injects `ShopService` and `DestroyRef`.
   - On `ngOnInit`, calls `shopService.getItems()`, stores the result in `items: ShopItem[]`.
   - Pipe every `.subscribe()` through `.pipe(takeUntilDestroyed(this.destroyRef))` to prevent
     memory leaks (mandatory convention — see `FRONTEND_CODE_CONVENTIONS.md` rule 4).
   - Tracks `loading: boolean` and `error: string | null` state.

3. **Build the template** (`inventory.component.html`):
   - Show `<clr-spinner>` while `loading` is true.
   - Show `<clr-alert clrAlertType="danger">` if `error` is set.
   - Show a `<clr-datagrid>` with columns: **Name**, **Sell In**, **Quality**.
   - Each row maps one `ShopItem`; cells render `item.name`, `item.sellIn`, `item.quality`.
   - Apply a CSS class to highlight items where `sellIn <= 0` (expired) — e.g. `text-danger`.

4. **Register the route** — replace the placeholder `InventoryComponent` in `app.routes.ts` with
   the real implementation (the lazy-load path remains unchanged).

5. **Run tests (Green)** — all three component tests pass; all existing service tests continue to pass.

6. **Refactor** — extract a private `handleError()` method if the error-handling logic is repeated.
   Ensure the template uses `*ngFor` (or `@for`) to iterate over items rather than hard-coded rows.
   No business logic should live in the component — only state and delegation to the service.

---

## Acceptance Criteria

- [ ] Navigating to `/inventory` shows a Clarity datagrid with columns: Name, Sell In, Quality.
- [ ] The datagrid is populated with items returned by `GET /api/items`.
- [ ] A loading spinner is visible while the HTTP request is pending.
- [ ] An error alert is shown if the request fails.
- [ ] Expired items (`sellIn <= 0`) are visually distinguished (e.g. different text colour).
- [ ] All three component tests pass.
- [ ] Every `subscribe()` call is piped through `takeUntilDestroyed(this.destroyRef)`.
- [ ] `ShopService` is not modified.
- [ ] No direct `HttpClient` calls exist in `InventoryComponent`.
- [ ] Java source files are not modified.

---

## Notes

Depends on task 008 (Angular bootstrap + `ShopService`) being completed first.

The price column is intentionally omitted here — it is added in task 011 to keep this task
focused and achievable in a single session.

---

## Changelog

| Date | Status | Note |
|------|--------|------|
| 2026-03-25 | funnel | Task created |
| 2026-03-25 | ready-for-development | Approved by user |
| 2026-03-25 | in-progress | Implementation started |
| 2026-03-25 | implemented | Component, template, and tests implemented. All 3 acceptance criteria tests pass (8 total tests passing) |
| 2026-03-25 | done | Accepted by user |
