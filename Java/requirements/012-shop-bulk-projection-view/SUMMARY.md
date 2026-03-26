# Task 012 Summary — Shop Bulk Projection View

**Date:** 2026-03-26  
**Model:** Claude Haiku 4.5

---

## What Was Implemented

Task 012 (combined with task 011) delivered a unified Projection page that allows shop keepers to forecast the entire inventory after N days. The page includes both bulk inventory projection (all items) and per-item projection (single item lookup), providing comprehensive inventory forecasting without mutating live data.

## Key Deliverables

### 1. Bulk Projection Form
- Reactive form with `days` control (required, min 0)
- Real-time form validation with user-friendly error messages
- Submit button disabled while form is invalid
- Clean form layout with Clarity components

### 2. Bulk Projection Results
- Clarity datagrid displaying all projected items (name, sellIn, quality)
- Rows highlighted in red/danger styling when `sellIn <= 0`
- Read-only results (no mutation of live inventory)
- Responsive datagrid with proper column alignment

### 3. Component Implementation
- `ProjectionComponent` with dual projection methods:
  - `submitBulkProjection()` — calls `ShopService.projectAll(days)`
  - `submitItemProjection()` — calls `ShopService.projectItem(itemName, days)`
- Loading spinners display during API calls
- Error alerts with user-friendly error messages
- Proper subscription cleanup in `ngOnDestroy()`

### 4. Template & UI Design
- Dedicated `/projection` route with two distinct sections
- Bulk Projection Section: Form + datagrid
- Per-Item Projection Section: Form + result card
- Consistent styling with inventory page
- Clear visual separation between sections

### 5. Unit Test Coverage
- 15+ comprehensive tests covering:
  - Form rendering and validation
  - API integration and mock responses
  - Loading and error states
  - Result display for both bulk and per-item projection
  - Edge cases (negative values, empty fields)
- All tests passing in Angular 15 environment

## Files Created

**New Files:**
- [frontend/src/app/features/projection/projection.component.ts](frontend/src/app/features/projection/projection.component.ts) — Component with bulk and per-item projection logic (90+ lines)
- [frontend/src/app/features/projection/projection.component.html](frontend/src/app/features/projection/projection.component.html) — Template with two projection sections (196+ lines)
- [frontend/src/app/features/projection/projection.component.spec.ts](frontend/src/app/features/projection/projection.component.spec.ts) — Comprehensive test suite (15+ tests)

## Test Results

✅ **35 frontend tests passing** (15+ projection tests + 20 other)

```
Chrome Headless 146.0.0.0 (Windows 10): Executed 35 of 35 SUCCESS (0.415 secs)
TOTAL: 35 SUCCESS
```

## Architecture & Design Decisions

1. **Unified Projection Page** — Combined tasks 011 & 012 into single component/template for cohesive UX
2. **Reactive Forms** — Two separate form groups (`bulkForm` and `itemForm`) for independent management
3. **State Management** — Component-level state with loading flags, errors, and results
4. **Error Handling** — Service error handling with user-friendly alert messages
5. **Memory Cleanup** — Proper subscription management in `ngOnDestroy()` for Angular 15 compatibility

## Key Features

### Bulk Projection
- Input: Number of days
- Output: Datagrid showing all items' projected values
- Danger highlighting: Rows with `sellIn <= 0` shown in red
- Use case: Plan inventory management across the full catalog

### Per-Item Projection
- Input: Item name + number of days
- Output: Card showing projected values for single item
- Use case: Deep-dive forecast for specific interesting items

## Verification

To verify the bulk projection feature:
1. Navigate to `/projection` in the Angular app
2. Fill in the Bulk Projection form (Days field)
3. Click "Show Projection"
4. Verify datagrid displays all items with projected values
5. Verify rows with `sellIn <= 0` are highlighted in red

## Acceptance Criteria Met

✅ Dedicated `/projection` route with bulk projection form  
✅ Form validates days input (required, min 0)  
✅ Validation errors display on invalid input  
✅ API call made to `ShopService.projectAll(days)`  
✅ All items displayed in Clarity datagrid with projected values  
✅ Rows with expired items (sellIn <= 0) highlighted in danger red  
✅ Loading spinner visible during API calls  
✅ Error messages shown if projection fails  
✅ Live inventory never mutated  
✅ Unified design with per-item projection (task 011)  
✅ All 35+ frontend tests passing (15+ projection tests)  
✅ Subscription cleanup via proper lifecycle management  
✅ Angular 15 compatible (no DestroyRef or takeUntilDestroyed)  

---

## technical Implementation Details

**Component:**
- Class: `ProjectionComponent`
- Lifecycle: OnInit, OnDestroy
- Injection: ShopService, FormBuilder
- Forms: Two reactive form groups with proper validators

**Template:**
- 196+ lines of HTML with Clarity components
- `<clr-input-container>` for form inputs
- `<clr-control-error>` for validation messages
- `<clr-datagrid>` for bulk results display
- `<clr-alert>` for error messages
- `<clr-spinner>` for loading states

**Tests:**
- 15+ unit tests using Jasmine/Karma
- Async operations handled with `fixture.whenStable()` and `done()` callbacks
- Spy objects for ShopService mocking
- Coverage: form validation, API calls, loading states, error handling, result display

---

## Summary

Task 012 is complete and fully integrated with task 011 into a production-ready unified Projection page. The feature provides comprehensive inventory forecasting capabilities (`projectAll()` for bulk, `projectItem()` for individual items) without affecting live data. Shop keepers can now make informed decisions about inventory management with accurate future-state simulations.
