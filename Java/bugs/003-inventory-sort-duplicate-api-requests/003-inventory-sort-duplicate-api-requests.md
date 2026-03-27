# 003 — Inventory Table Sends Duplicate API Requests During Sort

**Status:** reported

---

## Description

When sorting the inventory table, the frontend sends two consecutive requests to `api/items`:
1. First request includes sort parameters and returns sorted results
2. Immediately followed by a second request without sort parameters, which overwrites the first response

This causes the sorted UI to briefly show the sorted data, then revert to unsorted order.

---

## Root Cause

under investigation

---

## Fix

_To be filled when fix is implemented._

---

## Acceptance Criteria

- [ ] Sorting the inventory table sends only one API request with sort parameters
- [ ] Sorted results persist and are not overwritten by a subsequent request
- [ ] No duplicate requests appear in browser network developer tools

---

## Affected Files

| File | Change |
|------|--------|
| _to be filled during fix_ | |

---

## Changelog

| Date | Status | Note |
|------|--------|------|
| 2026-03-27 | reported | Bug reported: Inventory sort triggers duplicate API requests |
