# 010 — Advance Day Action

**Status:** ready-for-development

---

## Description

Shop keepers need to advance the shop by one day directly from the inventory page. Each click
on an "Advance Day" button should call the backend, update all item qualities, and refresh the
displayed inventory — all without reloading the whole page. The button must be disabled while
the server call is in progress to prevent double-clicks.

---

## Implementation Plan

1. **Write failing component tests (Red)** in `inventory.component.spec.ts`
   (extending the tests from task 009):
   - `should_renderAdvanceDayButton_when_inventoryPageLoads` — assert a button labelled
     "Advance Day" is present in the template.
   - `should_callAdvanceDay_when_buttonIsClicked` — spy on `ShopService.advanceDay()`,
     simulate a click on the button, and assert the spy was called once.
   - `should_refreshInventory_when_advanceDaySucceeds` — mock `advanceDay()` to return a
     new list of items; assert the datagrid re-renders with the returned data.
   - `should_disableButton_when_requestIsInProgress` — mock `advanceDay()` with a never-resolving
     observable; assert the button has the `disabled` attribute while the call is pending.
   - `should_showErrorAlert_when_advanceDayFails` — mock `advanceDay()` to throw; assert the
     error alert appears.
   Run the tests — they fail because the functionality does not exist yet.

2. **Update `InventoryComponent`**:
   - Add `advancing: boolean` state flag.
   - Add `advanceDay()` method that calls `shopService.advanceDay()` and updates `items` on success,
     sets `error` on failure, and resets `advancing` when the call completes (success or error).
   - Pipe the `advanceDay()` subscription through `.pipe(takeUntilDestroyed(this.destroyRef))`
     (a `DestroyRef` injection already exists from task 009 — reuse it).

3. **Update `inventory.component.html`**:
   - Add a Clarity primary button `<button clrButton (click)="advanceDay()">Advance Day</button>`
     above the datagrid.
   - Bind `[disabled]="advancing"` to prevent multiple concurrent requests.
   - Reuse the existing `<clr-alert>` block — it already covers both load and advance errors.

4. **Run tests (Green)** — all five new tests and all previous tests pass.

5. **Refactor** — if the error-handling and loading-state reset logic is duplicated between
   `load()` and `advanceDay()`, extract a shared private helper.

---

## Acceptance Criteria

- [ ] An "Advance Day" button is visible on the Inventory page.
- [ ] Clicking the button calls `POST /api/items/advance-day` exactly once.
- [ ] The datagrid updates with the new item values returned by the server.
- [ ] The button is disabled while the HTTP request is in flight.
- [ ] An error alert is displayed if the request fails.
- [ ] All five new tests pass; all previous tests continue to pass.
- [ ] Java source files are not modified.

---

## Notes

Depends on task 009 (Inventory List Page) being completed first.

---

## Changelog

| Date | Status | Note |
|------|--------|------|
| 2026-03-25 | funnel | Task created |
| 2026-03-25 | ready-for-development | Approved by user |
