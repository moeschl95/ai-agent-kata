# Frontend Code Conventions

This file defines the mandatory coding conventions for all Angular/TypeScript frontend code in this project.
They apply to every file touched during development — new code and modified code alike.

---

## 1. Standalone Components (mandatory)

All components **must** be standalone (`standalone: true`). Do not use `NgModule`-based declarations.
This is enforced by the technology stack and `angular.json` defaults.

```ts
// Correct
@Component({
  selector: 'app-inventory',
  standalone: true,
  imports: [CommonModule, ClarityModule],
  templateUrl: './inventory.component.html',
})
export class InventoryComponent { ... }

// Wrong — NgModule-based
@NgModule({ declarations: [InventoryComponent] })
export class InventoryModule { }
```

---

## 2. Prefer Clarity Components

Always prefer a Clarity component over a plain HTML equivalent:

| Instead of | Use |
|---|---|
| `<table>` | `<clr-datagrid>` |
| custom overlay | `<clr-modal>` |
| custom banner | `<clr-alert>` |
| bare `<input>` | `<input clrInput>` inside `<clr-input-container>` |
| bare `<button>` | `<button clrButton>` |

Only fall back to plain HTML when no Clarity equivalent exists.

---

## 3. RxJS — Subjects Must Be Private

`Subject`, `BehaviorSubject`, `ReplaySubject`, and `AsyncSubject` must **always** be `private`.
Expose state to consumers only through a typed `Observable`:

```ts
// Correct
private readonly _items$ = new BehaviorSubject<ShopItem[]>([]);
readonly items$ = this._items$.asObservable();

// Wrong — never expose a Subject directly
readonly items$ = new BehaviorSubject<ShopItem[]>([]);   // ❌
```

---

## 4. RxJS — Unsubscribe with takeUntilDestroyed

Every `subscribe()` call in a component **must** be paired with `takeUntilDestroyed` to prevent memory leaks.
Use Angular's `DestroyRef` (Angular 16+) pattern:

```ts
import { Component, DestroyRef, OnInit, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Component({ standalone: true, ... })
export class MyComponent implements OnInit {
  private readonly destroyRef = inject(DestroyRef);

  ngOnInit(): void {
    this.shopService.getItems()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(items => this.items = items);
  }
}
```

> Prefer `takeUntilDestroyed(this.destroyRef)` over the manual `Subject`/`ngOnDestroy` pattern.
> Both are acceptable if you must support older Angular; be consistent within a component.
