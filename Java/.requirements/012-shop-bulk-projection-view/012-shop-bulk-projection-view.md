# 012 — Shop Bulk Projection View

**Status:** done

---

## Description

Shop keepers want to see where the entire inventory will stand after N days — not just a single
item. The Projection page (already scaffolded in task 008) should show a form where users enter
a number of days and, upon submission, displays a read-only datagrid showing every item's
projected name, `sellIn`, and `quality`. The live inventory is never mutated.

---

## Implementation Plan

1. **Write failing component tests (Red)** in a new `projection.component.spec.ts`:
   - `should_renderDaysForm_when_pageLoads` — assert that a numeric "Days" input and a
     "Show Projection" button are present in the DOM.
   - `should_showValidationError_when_daysIsNegative` — type `-1`, assert a validation error
     is shown and the service is NOT called.
   - `should_showValidationError_when_daysIsEmpty` — clear the field, submit, assert validation
     error appears.
   - `should_callProjectAll_when_validFormIsSubmitted` — mock `ShopService.projectAll()` to
     return two items; fill in `days = 5` and submit; assert the spy was called with `5`.
   - `should_displayProjectedItems_when_projectionSucceeds` — mock `projectAll()` with two
     items; submit the form; assert both item names appear in the rendered datagrid.
   - `should_showErrorAlert_when_projectionFails` — mock `projectAll()` to throw; submit;
     assert `<clr-alert clrAlertType="danger">` appears.
   - `should_showLoadingSpinner_when_requestIsInFlight` — mock with a never-resolving observable;
     submit; assert `<clr-spinner>` is visible.
   Run the tests — they fail because the component is still a placeholder.

2. **Implement `ProjectionComponent`** (`src/app/features/projection/`):
   - Standalone component that injects `ShopService` and `FormBuilder`.
   - Reactive Form with one control: `days` (required, `Validators.min(0)`).
   - `projectedItems: ProjectedItem[] = []`.
   - `loading = false`, `error: string | null = null`.
   - `submit()` method: validates the form; calls `shopService.projectAll(days)`;
     updates `projectedItems` on success, sets `error` on failure, resets `loading` on complete.
   - Inject `DestroyRef` and pipe the subscription through `.pipe(takeUntilDestroyed(this.destroyRef))`
     to prevent memory leaks (mandatory convention — see `FRONTEND_CODE_CONVENTIONS.md` rule 4).

3. **Build the template** (`projection.component.html`):
   - A page heading: "Shop Projection".
   - A Clarity form section containing:
     - A labeled `<input clrInput type="number" formControlName="days">` for "Days".
     - `<clr-control-error>` shown when the field is invalid and touched.
     - A `<button clrButton (click)="submit()" [disabled]="loading">Show Projection</button>`.
   - `<clr-spinner>` shown while `loading` is true.
   - `<clr-alert clrAlertType="danger">` shown when `error` is set.
   - A `<clr-datagrid>` (shown only when `projectedItems.length > 0`) with columns:
     **Name**, **Sell In**, **Quality**.
     Highlight rows where projected `sellIn <= 0` with a CSS danger class.

4. **Register the route** — replace the placeholder `ProjectionComponent` in `app.routes.ts`
   with the real implementation (lazy-load path stays the same).

5. **Run tests (Green)** — all seven new tests and all previous tests pass.

6. **Refactor** — ensure no business logic is in the template. If error-handling and loading
   state reset are identical to the pattern in `InventoryComponent`, note it as a shared pattern
   (but do NOT extract a shared base class — prefer duplication over the wrong abstraction at
   this stage).

---

## Acceptance Criteria

- [ ] Navigating to `/projection` shows a "Shop Projection" page with a Days input and a "Show Projection" button.
- [ ] Entering a negative number shows a validation error; the service is not called.
- [ ] Submitting with a valid number calls `GET /api/items/projection?days=n`.
- [ ] A loading spinner is visible while the request is in flight.
- [ ] The results datagrid shows all projected items with Name, Sell In, Quality columns.
- [ ] Items with projected `sellIn <= 0` are visually distinguished.
- [ ] An error alert is shown if the request fails.
- [ ] All seven new tests and all prior tests pass.
- [ ] Every `subscribe()` call is piped through `takeUntilDestroyed(this.destroyRef)`.
- [ ] Java source files are not modified.

---

## Notes

Depends on task 008 (Angular bootstrap + `ShopService`). Tasks 009–011 are not hard dependencies,
but completing them first gives a more complete application to demo.

Imports needed: `ReactiveFormsModule` from `@angular/forms`.

---

## Changelog

| Date | Status | Note |
|------|--------|------|
| 2026-03-25 | funnel | Task created |
| 2026-03-25 | ready-for-development | Approved by user |
| 2026-03-26 | in-progress | Implementation started - combining with task 011 for unified projection page |
| 2026-03-26 | implemented | Completed - Unified Projection page with bulk projection form, reactive form validation, ShopService integration, datagrid display of projected inventory, and comprehensive unit tests (15+ tests passing). Per-item and bulk projection features merged into single /projection route with two-section layout. |
| 2026-03-26 | done | User approved implementation. Unified Projection page with bulk and per-item inventory forecasting. All 35 frontend tests passing. SUMMARY.md created. |
