---
name: frontend-angular
description: >
  Build, scaffold, and guide Angular + Clarity frontend features for the Gilded Rose shop UI.
  Use this skill whenever the user asks about the frontend, Angular, Clarity components, the
  Angular app structure, creating new pages/components/services, connecting to the REST API,
  or asks how the UI works. Also trigger this skill when the user mentions "frontend task",
  "Angular task", "UI feature", "Clarity datagrid", "Angular forms", "Angular routing", or
  anything that implies working inside the `frontend/` folder. Even if they don't say "Angular"
  explicitly — if they are describing a browser-facing feature, use this skill.
---

# Frontend Development — Angular + Clarity

This skill covers all frontend work for the Gilded Rose shop: project setup, component authoring,
API integration, routing, and testing. The frontend lives in the `frontend/` subfolder of the repo
and connects to the Spring Boot backend via a local proxy.

---

## Technology Stack

| Layer | Choice | Version |
|---|---|---|
| Framework | Angular | 17+ (standalone components) |
| UI library | Clarity Design System (`@clr/angular`, `@clr/ui`) | 17+ |
| HTTP | Angular `HttpClient` | built-in |
| Routing | Angular Router | built-in |
| Forms | Angular Reactive Forms | built-in |
| Testing | Angular Testing Library + Jasmine/Karma | built-in |
| Build | Angular CLI (`ng`) | matching Angular version |

> **Why Clarity?** VMware Clarity provides enterprise-grade, accessible UI components —
> especially `clr-datagrid`, `clr-modal`, `clr-alert`, and form controls — that map cleanly
> onto the shop's inventory and projection workflows.

---

## Project Layout

```
frontend/                          ← Angular workspace root
├── angular.json
├── package.json
├── tsconfig.json
├── proxy.conf.json                ← Dev-server proxy to backend :8080
└── src/
    ├── main.ts
    ├── index.html
    ├── styles.scss                ← Import Clarity + global styles here
    └── app/
        ├── app.config.ts          ← provideRouter, provideHttpClient
        ├── app.routes.ts          ← Route definitions
        ├── app.component.ts/.html ← Shell with <clr-header> nav + <router-outlet>
        ├── core/
        │   └── shop.service.ts    ← Single service wrapping all REST calls
        └── features/
            ├── inventory/
            │   ├── inventory.component.ts
            │   └── inventory.component.html
            └── projection/
                ├── projection.component.ts
                └── projection.component.html
```

Keep **one service** (`ShopService`) for all HTTP calls. Features are grouped under `features/`.
Add a `core/` folder only for services and interfaces shared across features.

---

## Bootstrap Setup (first-time only)

```bash
# From the repo root
ng new frontend --routing=true --style=scss --standalone=true
cd frontend
npm install @clr/angular @clr/ui @cds/core
```

**`src/styles.scss`** — import Clarity styles:
```scss
@import '@clr/ui/clr-ui.min.css';
```

**`src/app/app.config.ts`**:
```ts
import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { routes } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(),
  ]
};
```

**`proxy.conf.json`** — in the `frontend/` root:
```json
{
  "/api": {
    "target": "http://localhost:8080",
    "secure": false
  }
}
```

**`angular.json`** — wire the proxy under `architect.serve.options`:
```json
"proxyConfig": "proxy.conf.json"
```

Start backend on port 8080 first, then run the frontend:
```bash
ng serve --proxy-config proxy.conf.json
```

---

## Shared Model Interfaces

Create `src/app/core/models.ts`:

```ts
export interface ShopItem {
  name: string;
  sellIn: number;
  quality: number;
}

export interface ProjectedItem {
  name: string;
  sellIn: number;
  quality: number;
}
```

---

## ShopService — All REST Calls in One Place

`src/app/core/shop.service.ts`:

```ts
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ShopItem, ProjectedItem } from './models';

@Injectable({ providedIn: 'root' })
export class ShopService {
  private readonly http = inject(HttpClient);
  private readonly base = '/api/items';

  getItems(): Observable<ShopItem[]> {
    return this.http.get<ShopItem[]>(this.base);
  }

  advanceDay(): Observable<ShopItem[]> {
    return this.http.post<ShopItem[]>(`${this.base}/advance-day`, {});
  }

  getPrice(name: string): Observable<number> {
    return this.http.get<number>(`${this.base}/${encodeURIComponent(name)}/price`);
  }

  projectItem(name: string, days: number): Observable<ProjectedItem> {
    return this.http.get<ProjectedItem>(
      `${this.base}/${encodeURIComponent(name)}/projection`,
      { params: { days } }
    );
  }

  projectAll(days: number): Observable<ProjectedItem[]> {
    return this.http.get<ProjectedItem[]>(
      `${this.base}/projection`,
      { params: { days } }
    );
  }
}
```

---

## Typical Component Pattern (standalone)

```ts
import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ClarityModule } from '@clr/angular';
import { ShopService } from '../../core/shop.service';
import { ShopItem } from '../../core/models';

@Component({
  selector: 'app-inventory',
  standalone: true,
  imports: [CommonModule, ClarityModule],
  templateUrl: './inventory.component.html',
})
export class InventoryComponent implements OnInit {
  private readonly shopService = inject(ShopService);
  items: ShopItem[] = [];
  loading = false;

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading = true;
    this.shopService.getItems().subscribe({
      next: items => { this.items = items; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }
}
```

Use `inject()` instead of constructor injection — it is the modern idiomatic Angular 17+ approach.
Always handle the `error` callback — show a `<clr-alert>` to the user when something goes wrong.

---

## Clarity Components Quick Reference

| Situation | Clarity component | Selector / module |
|---|---|---|
| Table of items | Datagrid | `<clr-datagrid>` |
| Row in a datagrid | `<clr-dg-row>` + `<clr-dg-cell>` | — |
| Column header | `<clr-dg-column>` | — |
| Modal / dialog | `<clr-modal>` | — |
| Primary button | `<button clrButton>` | — |
| Outline button | `<button clrButton type="outline">` | — |
| Input field | `<input clrInput>` | wrap in `<clr-input-container>` |
| Label for input | `<label clrLabel>` | — |
| Error message | `<clr-control-error>` | inside container |
| Alert banner | `<clr-alert>` | — |
| Success/error badge | `<span class="badge badge-success">` | CSS only |
| Spinner / loading | `<clr-spinner>` | — |

Import `ClarityModule` from `@clr/angular` in each standalone component's `imports` array.

---

## Routing Conventions

`src/app/app.routes.ts`:

```ts
import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', redirectTo: 'inventory', pathMatch: 'full' },
  {
    path: 'inventory',
    loadComponent: () =>
      import('./features/inventory/inventory.component').then(m => m.InventoryComponent)
  },
  {
    path: 'projection',
    loadComponent: () =>
      import('./features/projection/projection.component').then(m => m.ProjectionComponent)
  },
];
```

Use `loadComponent` (lazy-load) for every feature route — keep initial bundle small.

---

## Code Conventions

All mandatory frontend coding conventions are defined in
[`.claude/conventions/FRONTEND_CODE_CONVENTIONS.md`](../../conventions/FRONTEND_CODE_CONVENTIONS.md).

Read that file before writing any frontend code. Key rules at a glance:
- All components must be `standalone: true` — no `NgModule`
- Always prefer Clarity components over plain HTML equivalents
- `Subject`/`BehaviorSubject` must be `private`; expose only via `.asObservable()`
- Every `subscribe()` must use `takeUntilDestroyed(this.destroyRef)`

---

## Testing Guidelines

### Unit-test a service

```ts
describe('ShopService', () => {
  let service: ShopService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()]
    });
    service = TestBed.inject(ShopService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should fetch all items', () => {
    const mockItems: ShopItem[] = [{ name: 'Aged Brie', sellIn: 10, quality: 20 }];
    service.getItems().subscribe(items => expect(items).toEqual(mockItems));
    httpMock.expectOne('/api/items').flush(mockItems);
  });
});
```

### Unit-test a component

Use `TestBed.configureTestingModule` with the component's standalone imports.
Mock `ShopService` with `jasmine.createSpyObj` and return `of(...)` from RxJS.

```ts
describe('InventoryComponent', () => {
  let shopServiceSpy: jasmine.SpyObj<ShopService>;

  beforeEach(async () => {
    shopServiceSpy = jasmine.createSpyObj('ShopService', ['getItems']);
    shopServiceSpy.getItems.and.returnValue(of([]));

    await TestBed.configureTestingModule({
      imports: [InventoryComponent],
      providers: [{ provide: ShopService, useValue: shopServiceSpy }]
    }).compileComponents();
  });
  // ...
});
```

### Test naming

Follow the same convention as the Java tests:
`should_<expected behavior>_when_<condition>`

---

## Running the App

1. Start the backend:
   ```bash
   # from repo root
   .\gradlew.bat bootRun
   ```

2. Start the frontend dev server:
   ```bash
   cd frontend
   ng serve
   ```

3. Open `http://localhost:4200` in a browser.

---

## Common Mistakes to Avoid

- **Do not** call `HttpClient` directly in components — always go through `ShopService`.
- **Do not** subscribe in the service — return `Observable<T>` and let the component subscribe.
- **Do not** use two-way binding on Datagrid rows for selection state management; use component properties.
- **Do not** forget to import `ClarityModule` in each standalone component.
- **Do not** put router path strings in more than one place — define them as constants if reused.
- **Do check** for `days < 0` in the Projection form before calling the API.
- **Do not** expose `Subject` or `BehaviorSubject` as `public` — always use `.asObservable()`.
- **Do not** subscribe without `takeUntilDestroyed(this.destroyRef)` — every subscription must be cleaned up.
- **Do not** use `NgModule`-based components — all components must be `standalone: true`.
