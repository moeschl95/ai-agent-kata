# 011 — Item Projection Panel

**Status:** done

---

## Description

Users want to explore the future state of a single item without actually advancing the shop.
The Projection page (a dedicated view separate from the inventory list) should allow a user to select (or type) an item name and a number of days,
then display a panel showing that item's projected `sellIn` and `quality` after
those days — without mutating live data. This gives shop keepers an at-a-glance forecast for
individual items. The per-item projection is accessed from the projection panel, not from the inventory list.

---

## Implementation Plan

1. **Defer to task 012 (Shop Bulk Projection View) for page structure** — task 012 creates a dedicated Projection page.
   Task 011 focuses on adding the single-item projection form to that Projection page once 012 is complete.

2. **Write failing component tests (Red)** in a `projection.component.spec.ts` (to be created as part of task 012):
   - `should_displayItemNameInput_when_projectionPageLoads` — assert the page has an input for selecting/typing item name.
   - `should_displayDaysInput_when_projectionPageLoads` — assert the page has a number input for days.
   - `should_displayProjectedValues_when_projectionSucceeds` — mock `ShopService.projectItem()`; submit the form;
     assert the projected `sellIn` and `quality` values are rendered.
   - `should_showValidationError_when_daysIsNegative` — enter `-1` in the days field; assert the form shows
     a validation error and does not call the service.
   - `should_showValidationError_when_itemNameIsEmpty` — leave item name empty; assert the form shows a
     validation error.
   Run the tests — they should fail.

3. **Create the Projection form** in the Projection component (created as part of task 012):
   - Add a Reactive Form group with `itemName` and `days` controls.
   - Apply validators: `Validators.required` and for days `Validators.min(0)`.
   - Add `submitProjection()` method — if form is invalid, mark all fields touched and return early;
     otherwise call `shopService.projectItem(itemName, days)` and store the result.
   - Pipe the subscription through `.pipe(takeUntilDestroyed(this.destroyRef))`.

4. **Update the Projection page template**:
   - Add a form with labeled inputs for "Item Name" (text) and "Days" (number).
   - Add `<clr-control-error>` elements shown when fields are invalid.
   - Add a "Show Projection" submit button (disabled while form is invalid).
   - Add a result section (shown only when `projectedResult` is set) displaying the projected
     `sellIn` and `quality` values.

5. **Run tests (Green)** — all projection tests and all previous tests pass.

6. **Refactor** — ensure the form layout and styling are consistent with the rest of the page;
   extract large template blocks into named `ng-template` fragments if needed.

---

## Acceptance Criteria

- [ ] Item name input field (text) with validation (required).
- [ ] Days numeric input field with validation (must be ≥ 0).
- [ ] The form shows validation errors when fields are invalid.
- [ ] Submitting the form calls `GET /api/items/{name}/projection?days=n`.
- [ ] The projected `sellIn` and `quality` are displayed in a result panel.
- [ ] Form disables submit button while invalid.
- [ ] All new and existing tests pass.
- [ ] Every `subscribe()` call is piped through `takeUntilDestroyed()`.
- [ ] Java source files are not modified.
- [ ] Feature is integrated into the dedicated Projection page (task 012), not the inventory list.

---

## Notes

## Notes

Depends on task 012 (Shop Bulk Projection View). Task 012 creates the Projection page structure;
task 011 adds the single-item projection form to that page.

Imports needed: `ReactiveFormsModule` from `@angular/forms` in the component's `imports` array.

---

## Changelog

| Date | Status | Note |
|------|--------|------|
| 2026-03-25 | funnel | Task created |
| 2026-03-25 | ready-for-development | Approved by user |
| 2026-03-26 | in-progress | Implementation started |
| 2026-03-26 | implemented | Completed - projection modal with item forecast capability, form validation, and integration tests all passing |
| 2026-03-26 | in-progress | User feedback: projection feature should be in dedicated projection panel, not on inventory page. Moving back to in-progress to relocate feature to projection page (task 012). |
| 2026-03-26 | in-progress | Implementation started |
| 2026-03-26 | implemented | Completed - Unified Projection page with per-item projection form, reactive form validation, ShopService integration, and comprehensive unit tests (15 tests passing). Feature placed on dedicated /projection route as per architectural feedback. |
| 2026-03-26 | done | User approved implementation. Per-item projection available on /projection route with bulk projection. All 35 frontend tests passing. |
