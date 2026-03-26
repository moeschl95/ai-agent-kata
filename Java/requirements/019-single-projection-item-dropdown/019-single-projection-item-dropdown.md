# 019 — Single Projection Item Dropdown

**Status:** ready-for-development

---

## Description

Replace the plain text input field in the Single Item Projection section with a dropdown select list. Users should select an item from the available inventory instead of typing the item name. This improves usability by preventing typos and making it clear which items are available for projection.

---

## Implementation Plan

1. **Fetch available item names** — When the projection component initializes, call the backend `/api/items` endpoint to retrieve all available items and extract the names into an array.

2. **Create dropdown form control** — Update the reactive form in `projection.component.ts` to replace the `itemName` text input with a dropdown select control bound to the list of available items.

3. **Update the template** — Replace the `clrInput` text input in `projection.component.html` with a Clarity dropdown (`<clr-select>`) that displays all available item names.

4. **Handle selection errors** — Ensure validation errors are displayed if no item is selected, matching the existing validation pattern.

5. **Write unit tests** — Add tests to verify:
   - Items are loaded on component initialization
   - The dropdown contains the correct item names
   - Form submission works when an item is selected
   - Validation error appears when no item is selected
   - Error state is handled gracefully if item fetch fails

6. **Verify visual consistency** — Confirm the dropdown styling matches the existing Clarity datagrid and form controls on the page.

---

## Acceptance Criteria

- [ ] Dropdown displays all available items from the backend inventory
- [ ] Selecting an item and submitting the form works correctly
- [ ] Validation error displays if form is submitted without selecting an item
- [ ] Items are loaded when the component initializes
- [ ] If item fetch fails, an error message is displayed and the dropdown is disabled
- [ ] All existing projection functionality still works (bulk and per-item projection after item selection)
- [ ] Unit tests cover the new dropdown functionality (initialization, selection, validation, error handling)
- [ ] No TypeScript or template compilation errors

---

## Notes

- The dropdown uses Clarity's `<clr-select>` component for consistency with the existing form design.
- Leverage the existing `ShopService` to fetch items.
- Reuse existing error handling patterns from the bulk projection section.

---

## Changelog

| Date | Status | Note |
|------|--------|------|
| 2026-03-26 | funnel | Task created |
| 2026-03-26 | ready-for-development | Approved by user |
