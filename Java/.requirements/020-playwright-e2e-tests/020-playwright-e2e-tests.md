# 020 — Playwright E2E Tests

**Status:** done

---

## Description

Add Playwright end-to-end tests to the Angular frontend so that key user journeys can be verified
in a real browser. Tests live under `frontend/e2e/` and run against a live Angular dev server.
This complements the existing Karma/Jasmine unit tests with browser-level integration coverage.

---

## Implementation Plan

1. Install `@playwright/test` as a dev dependency in `frontend/`.
2. Add `playwright.config.ts` at the `frontend/` root, configuring:
   - `webServer` to auto-start `ng serve` before tests run
   - Base URL pointing to `http://localhost:4200`
   - A single Chromium project (extendable later)
   - Test directory set to `e2e/`
3. Create `frontend/e2e/` folder with an initial smoke test file covering:
   - The inventory list page loads and displays the datagrid
   - The "Advance Day" button is visible and clickable
   - The projection page is reachable via navigation
4. Add a `test:e2e` script to `frontend/package.json` that runs `playwright test`.
5. Add a `playwright:install` script for first-time browser installation (`playwright install --with-deps chromium`).

---

## Acceptance Criteria

- [ ] `@playwright/test` is listed in `devDependencies` of `frontend/package.json`.
- [ ] `frontend/playwright.config.ts` exists and configures `webServer`, `baseURL`, and `testDir: 'e2e'`.
- [ ] At least one E2E test file exists under `frontend/e2e/`.
- [ ] The E2E tests cover: inventory page loads, Advance Day button visible, Projection page navigable.
- [ ] `npm run test:e2e` from `frontend/` runs Playwright and all tests pass against a running dev server.
- [ ] Existing Karma/Jasmine unit tests are not affected.

---

## Notes

- Tests require the Angular dev server; Playwright's `webServer` option handles auto-starting it.
- If the backend API is unavailable, tests that depend on real data will fail — API mocking via `page.route()` should be used in E2E tests to avoid the hard dependency on the Spring Boot backend.
- First-time setup requires `npx playwright install chromium` (downloads ~100 MB).

**Conflict with 021-angular-19-upgrade:** Task 021 upgrades Angular from 15 to 19 and changes the build configuration in `angular.json`. If this task is implemented first, the `webServer` config and any `ng serve` flags may need revisiting after the Angular upgrade lands.

---

## Changelog

| Date | Status | Note |
|------|--------|------|
| 2026-03-27 | funnel | Task created |
| 2026-03-27 | ready-for-development | Approved by user |
| 2026-03-27 | ready-for-development | Conflict noted with 021: Angular 19 upgrade changes angular.json build config; Playwright webServer config may need revisiting after upgrade |
| 2026-03-27 | in-progress | Implementation started |
| 2026-03-27 | implemented | Playwright setup complete: config added, 4 E2E tests created, all tests pass; existing 42 Jasmine unit tests unaffected |
| 2026-03-27 | done | Accepted by user |
