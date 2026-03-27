# 020 — Playwright E2E Tests — Implementation Summary

**Date:** 2026-03-27
**Model:** Claude Haiku 4.5

---

## What Was Implemented

Playwright end-to-end testing framework has been fully integrated into the Angular frontend. The implementation includes a `playwright.config.ts` configuration that auto-starts the Angular development server before tests run, targeting `http://localhost:4200`. Four E2E smoke tests were created covering key user journeys: inventory page loads, datagrid displays, Advance Day button is visible and clickable, and navigation to the Projection page. The `test:e2e` and `playwright:install` npm scripts were added to enable easy test execution and browser setup. All 4 E2E tests pass, and the existing 42 Karma/Jasmine unit tests remain passing with no regressions.

---

## Problems Addressed During Development

- **Invalid abort error code**: Initial attempt to mock API responses used an invalid Playwright error code (`blockedclient`). Resolved by removing the mock and letting tests run against the live dev server, which gracefully handles the backend.
- **URL routing**: The app redirects the root path `/` to `/inventory` by design. Updated the first test to use a regex pattern to match both paths.
- **Browser installation**: Playwright requires browser binaries to be installed separately. Added a dedicated `playwright:install` script to document and facilitate first-time setup.

---

## How to Run the Playwright Tests

**First-time setup** (browser installation):
```bash
cd frontend
npm run playwright:install
```

**Run all E2E tests**:
```bash
cd frontend
npm run test:e2e
```

The tests will automatically start the Angular dev server on `http://localhost:4200` and run against it. After execution, an HTML test report is generated and ready to view.

---

## Files Changed

- `frontend/playwright.config.ts` — Created Playwright configuration with webServer auto-start, baseURL, and test directory setup
- `frontend/e2e/inventory.e2e.spec.ts` — Created E2E test file with 4 smoke tests covering inventory page, datagrid, Advance Day button, and Projection page navigation
- `frontend/package.json` — Added `@playwright/test` dev dependency, added `test:e2e` and `playwright:install` npm scripts
- `.claude/conventions/FRONTEND_CODE_CONVENTIONS.md` — Added section 5 documenting E2E test file naming convention (`<feature>.e2e.spec.ts`)
