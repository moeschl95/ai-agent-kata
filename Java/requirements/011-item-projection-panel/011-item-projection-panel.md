# 011 — Item Projection Panel

**Status:** ready-for-development

---

## Description

Users want to explore the future state of a single item without actually advancing the shop.
The Inventory page should allow a user to select (or type) an item name and a number of days,
then display a side panel or modal showing that item's projected `sellIn` and `quality` after
those days — without mutating live data. This gives shop keepers an at-a-glance forecast for
individual items.

---

## Implementation Plan

1. **Write failing component tests (Red)** in `inventory.component.spec.ts`:
   - `should_showProjectButton_when_itemRowIsRendered` — assert each row in the datagrid has a
     "Project" button (or link).
   - `should_openProjectionModal_when_projectButtonIsClicked` — click the "Project" button on
     the first row; assert a `<clr-modal>` with `[clrModalOpen]="true"` is now in the DOM.
   - `should_displayProjectedValues_when_projectionSucceeds` — mock `ShopService.projectItem()`
     to return a `ProjectedItem`; submit the form in the modal; assert the projected `sellIn`
     and `quality` values are rendered.
   - `should_closeModal_when_closeButtonIsClicked` — open the modal, click "Close", assert the
     modal is no longer open.
   - `should_showValidationError_when_daysIsNegative` — enter `-1` in the days field; assert the
     form shows a validation error and does not call the service.
   Run the tests — they should fail.

2. **Create the Projection form** inside `InventoryComponent`:
   - Add `projectionModalOpen = false` and `selectedItem: ShopItem | null = null` state.
   - Add `projectedResult: ProjectedItem | null = null` state.
   - Add `openProjection(item: ShopItem)` method — sets `selectedItem` and `projectionModalOpen = true`.
   - Add a Reactive Form group with a single `days` control; apply `Validators.min(0)` and
     `Validators.required`.
   - Add `submitProjection()` method — if form is invalid, mark all fields touched and return early;
     otherwise call `shopService.projectItem(selectedItem.name, days)` and store the result.
   - Pipe the subscription through `.pipe(takeUntilDestroyed(this.destroyRef))` (reuse the
     `DestroyRef` injected in earlier tasks).

3. **Update the template**:
   - In each datagrid row, add a small Clarity outline button `<button clrButton type="outline"
     (click)="openProjection(item)">Project</button>`.
   - Add a `<clr-modal [(clrModalOpen)]="projectionModalOpen">` containing:
     - The selected item's name as the modal title.
     - A `<form>` with a Clarity labeled input for "Days" (bound to the reactive form control).
     - `<clr-control-error>` shown when the `days` field is invalid.
     - A "Show Projection" submit button (disabled while form is invalid).
     - A result section (shown only when `projectedResult` is set) displaying the projected
       `sellIn` and `quality` values using Clarity badges or simple text.
     - A "Close" button that sets `projectionModalOpen = false`.

4. **Run tests (Green)** — all five new tests and all previous tests pass.

5. **Refactor** — move the reactive form creation to `ngOnInit` rather than a property initializer
   so it is easier to reset. Extract large template blocks into named `ng-template` fragments if
   the template grows beyond one screen.

---

## Acceptance Criteria

- [ ] Each inventory row has a "Project" button.
- [ ] Clicking "Project" on a row opens a Clarity modal pre-filled with that item's name.
- [ ] The modal contains a "Days" numeric input with validation (must be ≥ 0).
- [ ] Submitting the form calls `GET /api/items/{name}/projection?days=n`.
- [ ] The projected `sellIn` and `quality` are displayed inside the modal.
- [ ] The form shows a validation error when `days` is negative; the service is not called.
- [ ] Clicking "Close" dismisses the modal.
- [ ] All new and existing tests pass.
- [ ] Every `subscribe()` call is piped through `takeUntilDestroyed(this.destroyRef)`.
- [ ] Java source files are not modified.

---

## Notes

Depends on task 009 (Inventory List Page). Task 010 (Advance Day) may be done first but is not
a hard dependency.

Imports needed: `ReactiveFormsModule` from `@angular/forms` in the component's `imports` array.

---

## Changelog

| Date | Status | Note |
|------|--------|------|
| 2026-03-25 | funnel | Task created |
| 2026-03-25 | ready-for-development | Approved by user |
