# 021 — Angular 19 Upgrade

**Status:** done

---

## Description

Upgrade the Angular frontend from version 15 to version 19 to gain access to modern Angular
features (signals, standalone components, improved build system) and stay within the Angular
LTS support window. The upgrade requires updating peer dependencies: TypeScript, zone.js, Angular
CDK, and Angular CLI. VMware Clarity (`@clr/angular` v17.12.2) is already compatible with Angular 19
and does not need to be upgraded. The app must continue to work correctly after the upgrade with
all existing unit tests passing.

---

## Implementation Plan

Angular's own migration guide recommends upgrading one major version at a time
(15 → 16 → 17 → 18 → 19). Use `ng update` at each step to apply automated migrations.

### Phase 1 — Angular 15 → 16
1. Run `npx ng update @angular/core@16 @angular/cli@16` and apply schematic migrations.
2. Update `@angular/cdk@^16` and `@angular-devkit/build-angular@^16`.
3. Update TypeScript to `~5.0.x` (Angular 16 requirement).
4. Verify `npm run build` and `npm test` pass.

### Phase 2 — Angular 16 → 17
5. Run `npx ng update @angular/core@17 @angular/cli@17`.
6. Replace `@angular-devkit/build-angular` with `@angular/build` (the new esbuild builder introduced in Angular 17) — update `angular.json` builder references from `@angular-devkit/build-angular:browser` to `@angular/build:application`.
7. Update `@angular/cdk@^17`.
8. Clarity packages stay at current versions (`@clr/angular` v17.12.2, confirmed Angular 19 compatible).
9. Update TypeScript to `~5.2.x` and zone.js to `~0.14.x`.
10. Verify builds and tests pass.

### Phase 3 — Angular 17 → 18
11. Run `npx ng update @angular/core@18 @angular/cli@18`.
12. Update `@angular/cdk@^18`.
13. No Clarity update needed — `@clr/angular` v17.12.2 is compatible with Angular 19.
14. Update TypeScript to `~5.4.x` and zone.js to `~0.14.x`.
15. Verify builds and tests pass.

### Phase 4 — Angular 18 → 19
16. Run `npx ng update @angular/core@19 @angular/cli@19`.
17. Update `@angular/cdk@^19`.
18. No Clarity update needed — `@clr/angular` v17.12.2, `@clr/ui` v17.12.2, and `@cds/core` v6.17.0 are confirmed compatible with Angular 19.
19. Update TypeScript to `~5.6.x` and zone.js to `~0.15.x`.
20. Update `tsconfig.json` if needed: change `moduleResolution` from `"node"` to `"bundler"` (recommended for Angular 17+).
21. Run `npm run build` and `npm test`; fix any breaking API changes, template compilation errors, or removed APIs surfaced by the migration schematics.
22. Also run `npm run build` for a production build to verify no tree-shaking or build-time issues.

---

## Acceptance Criteria

- [ ] `@angular/core` version in `package.json` is `^19.x`.
- [ ] `@angular/cli` version in `package.json` is `~19.x`.
- [ ] TypeScript version is compatible with Angular 19 (`~5.6.x`).
- [ ] `zone.js` version is `~0.15.x`.
- [ ] Clarity packages remain at current versions (`@clr/angular` v17.12.2, `@clr/ui` v17.12.2, `@cds/core` v6.17.0 — confirmed compatible with Angular 19).
- [ ] `npm run build` completes without errors.
- [ ] `npm test` runs all existing Karma/Jasmine unit tests and they all pass.
- [ ] The Angular dev server (`npm run start`) starts and the inventory list page renders correctly.
- [ ] No functionality regression: inventory list, advance-day, and projection pages all work.

---

## Notes

**Clarity compatibility:** `@clr/angular` v17.12.2 has been confirmed by the user to support Angular 19. No Clarity upgrade is required as part of this task.

**Build system change (Phase 2):** Angular 17 introduced `@angular/build` with an esbuild-based
builder as the new default. This produces faster builds but may require updating `angular.json`
builder targets and any custom webpack plugins (none currently in this project).

**Conflicts:**

- **020-playwright-e2e-tests** (`ready-for-development`): Task 020 sets up E2E tests against `ng serve`. The Angular 19 upgrade changes the version `ng serve` runs under and may alter the build configuration in `angular.json`. If 020 is implemented before 021, the Playwright `webServer` config and any `ng serve` flags may need revisiting after the upgrade.

---

## Changelog

| Date | Status | Note |
|------|--------|------|
| 2026-03-27 | funnel | Task created |
| 2026-03-27 | funnel | Revised: confirmed @clr/angular v17.12.2 supports Angular 19; Clarity upgrade steps removed |
| 2026-03-27 | ready-for-development | Approved by user |
| 2026-03-27 | in-progress | Implementation started by agent |
| 2026-03-27 | implemented | Angular 19 upgrade completed: all 4 phases done (15→16→17→18→19), builds pass, tests pass. Fixed angular.json: browserTarget→buildTarget in serve config || 2026-03-27 | done | Accepted by user |