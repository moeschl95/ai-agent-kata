# 003 — Inventory Table Sends Duplicate API Requests During Sort

**Status:** fixed

---

## Description

When sorting the inventory table, the frontend sends two consecutive requests to `api/items`:
1. First request includes sort parameters and returns sorted results
2. Immediately followed by a second request without sort parameters, which overwrites the first response

This causes the sorted UI to briefly show the sorted data, then revert to unsorted order.

---

## Root Cause

The `clr-datagrid` in `inventory.component.html` was conditionally rendered with
`*ngIf="!loading && !error && items.length > 0"`. Because `loading` was included in the
condition, the datagrid was **destroyed** every time a request started and **recreated** when
it completed. Clarity fires `clrDgRefresh` on every datagrid initialization with a default
null-sort state. When a sort request completed, `lastRequestedSortBy` was set to `'name'` but
the freshly recreated datagrid immediately fired `clrDgRefresh` with `null` sort. The guard in
`onDatagridRefresh` saw `null !== 'name'` and fired a second, unsorted API call.

---

## Fix

Removed `loading` from the datagrid's `*ngIf` condition so the datagrid is never destroyed
during reloads. The standalone `<clr-spinner>` now only appears on the initial empty-state load
(`*ngIf="loading && items.length === 0"`). Subsequent reloads show Clarity's built-in
`[clrDgLoading]="loading"` spinner inside the datagrid, keeping the component alive and
preserving its sort state. A new failing test (`should_keepDatagridInDom_when_reloadingAfterInitialLoad`)
was added to reproduce the bug and verify the fix.

---

## Acceptance Criteria

- [ ] Sorting the inventory table sends only one API request with sort parameters
- [ ] Sorted results persist and are not overwritten by a subsequent request
- [ ] No duplicate requests appear in browser network developer tools

---

## Affected Files

| File | Change |
|------|--------|
| `frontend/src/app/features/inventory/inventory.component.html` | Changed datagrid `*ngIf` to exclude `loading`; added `[clrDgLoading]="loading"`; spinner now only shown on initial empty load |
| `frontend/src/app/features/inventory/inventory.component.spec.ts` | Added `should_keepDatagridInDom_when_reloadingAfterInitialLoad` regression test |

---

## Changelog

| Date | Status | Note |
|------|--------|------|
| 2026-03-27 | reported | Bug reported: Inventory sort triggers duplicate API requests |
| 2026-03-27 | in-progress | Starting investigation and fix |
| 2026-03-27 | implemented | Root cause found: datagrid destroyed/recreated by *ngIf on loading; fixed with [clrDgLoading]; regression test added; all 15 tests pass |
| 2026-03-27 | fixed | Approved by user |
