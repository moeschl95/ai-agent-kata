# 001 — Clarity CSS Variables Not Loaded

**Status:** fixed

---

## Description

The Angular frontend using Clarity Design System rendered without any styling. Chrome DevTools reported CSS custom properties such as `--clr-header-6-bg-color` as undefined. All Clarity components appeared unstyled despite the packages being installed.

---

## Root Cause

Two separate issues combined to break the full token resolution chain:

### Issue 1 — SCSS `@import` of a `.css` file is a no-op at runtime

`src/styles.scss` contained:

```scss
@import '@clr/ui/clr-ui.min.css';
```

When a Sass `@import` path ends in `.css`, the Sass compiler does **not** inline the file. Instead, it emits the statement verbatim as a native CSS `@import` in the output bundle. The browser then tries to fetch `/clr-ui.min.css` as a URL — which fails because `node_modules` is not served over HTTP — so the file is silently never applied.

### Issue 2 — `@cds/core` global token file was never loaded

Clarity's CSS variables form a two-level chain:

```
--clr-header-6-bg-color
  → var(--clr-header-bg-color)
      → var(--cds-global-color-cool-gray-1000)   ← defined in @cds/core
```

`@clr/ui/clr-ui.min.css` only defines the `--clr-*` aliases; the underlying `--cds-global-color-*` tokens are defined in `@cds/core/global.min.css`. That file was never referenced anywhere in the project, so the entire token chain bottomed out as undefined.

---

## Fix

Removed the broken `@import` from `src/styles.scss` and registered both CSS files in the Angular build pipeline via `angular.json`, where Angular's webpack/esbuild pipeline bundles them directly into the output CSS:

### `frontend/angular.json` — styles array (build target)

```json
"styles": [
  "node_modules/@cds/core/global.min.css",
  "node_modules/@clr/ui/clr-ui.min.css",
  "src/styles.scss"
]
```

Order matters: `@cds/core/global.min.css` must come **before** `@clr/ui/clr-ui.min.css` so that the `--cds-global-color-*` tokens are declared before `--clr-*` variables reference them.

### `frontend/src/styles.scss` — removed broken import

```scss
/* Before */
@import '@clr/ui/clr-ui.min.css';   ← removed

/* After — file only contains global app overrides */
```

---

## Acceptance Criteria

- [x] Clarity components render with correct typography, colours, and layout.
- [x] Chrome DevTools shows no "undefined" CSS custom property warnings for `--clr-*` or `--cds-*` variables.
- [x] `ng build` completes without errors.

---

## Affected Files

| File | Change |
|------|--------|
| `frontend/angular.json` | Added `@cds/core/global.min.css` and `@clr/ui/clr-ui.min.css` to the `styles` array |
| `frontend/src/styles.scss` | Removed `@import '@clr/ui/clr-ui.min.css'` |

---

## Changelog

| Date | Status | Note |
|------|--------|------|
| 2026-03-25 | reported | Clarity CSS variables undefined; frontend unstyled |
| 2026-03-25 | fixed | Removed SCSS @import; added both @cds/core and @clr/ui CSS to angular.json styles array |
