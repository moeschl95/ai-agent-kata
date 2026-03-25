# 008 — Angular Frontend Bootstrap

**Status:** ready-for-development

---

## Description

The Gilded Rose shop currently only exposes a REST API with no user interface. This task
bootstraps an Angular application with the Clarity Design System inside a `frontend/` folder
so that all subsequent UI tasks have a solid, runnable foundation to build on. The result is a
navigable single-page application shell (header + routing) connected to the backend via a proxy,
but displaying no real data yet.

---

## Implementation Plan

1. **Scaffold the Angular app** inside `frontend/` using the Angular CLI:
   ```
   ng new frontend --routing=true --style=scss --standalone=true
   ```
   Commit only the generated files; do not write any business logic yet.

2. **Install Clarity Design System**:
   ```
   cd frontend
   npm install @clr/angular @clr/ui @cds/core
   ```

3. **Import Clarity styles** in `src/styles.scss`:
   ```scss
   @import '@clr/ui/clr-ui.min.css';
   ```

4. **Configure the dev-server proxy** — create `frontend/proxy.conf.json`:
   ```json
   {
     "/api": {
       "target": "http://localhost:8080",
       "secure": false
     }
   }
   ```
   Under `angular.json → architect.serve.options`, add `"proxyConfig": "proxy.conf.json"`.

5. **Create the shared models** in `src/app/core/models.ts`:
   - `ShopItem { name: string; sellIn: number; quality: number; }`
   - `ProjectedItem { name: string; sellIn: number; quality: number; }`

6. **Create `ShopService`** in `src/app/core/shop.service.ts` with all five methods:
   - `getItems()`, `advanceDay()`, `getPrice(name)`, `projectItem(name, days)`, `projectAll(days)`
   All methods return `Observable<T>`; no HTTP calls live outside this service.

7. **Write unit tests for `ShopService`** using `HttpTestingController`:
   - Each method must have at least one test verifying the correct HTTP verb, URL, and return type.
   - Test names follow: `should_<expected>_when_<condition>`.

8. **Wire `AppModule` / `app.config.ts`** — provide `HttpClient` and the router.

9. **Set up routes** in `src/app/app.routes.ts` with two lazy-loaded placeholders:
   - `/inventory` → `InventoryComponent` (a simple "Inventory coming soon" component)
   - `/projection` → `ProjectionComponent` (a simple "Projection coming soon" component)
   - `''` redirects to `/inventory`.

10. **Build the app shell** in `AppComponent`:
    - Add a `<clr-header>` navigation bar with links to "Inventory" and "Projection".
    - Add `<router-outlet>` below the header.

11. **Run the app** (`ng serve`) and verify it renders the header with navigation and switches
    pages without errors. Run `ng test` and confirm all service tests pass.

---

## Acceptance Criteria

- [ ] `frontend/` folder exists at the repo root and contains a valid Angular workspace.
- [ ] `npm install` inside `frontend/` succeeds with no peer-dependency errors for Clarity.
- [ ] `ng serve` starts the dev server; the app renders a Clarity header with "Inventory" and "Projection" nav links.
- [ ] Clicking "Inventory" and "Projection" in the nav switches the view without a page reload.
- [ ] `proxy.conf.json` routes `/api/**` to `http://localhost:8080`.
- [ ] `ShopService` exists with all five methods; each returns an `Observable<T>`.
- [ ] All `ShopService` tests pass (`ng test`).
- [ ] No HTTP calls exist outside `ShopService`.
- [ ] `Item.java` and all Java source files are not modified.

---

## Notes

This task produces no visible inventory data — it only lays the groundwork.
Tasks 009–012 each build a concrete feature on top of this foundation.

No conflicts with existing backend tasks 001–007.

---

## Changelog

| Date | Status | Note |
|------|--------|------|
| 2026-03-25 | funnel | Task created |
| 2026-03-25 | ready-for-development | Approved by user |
