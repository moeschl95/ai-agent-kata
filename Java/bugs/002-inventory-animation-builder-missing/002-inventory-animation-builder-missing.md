# 002 — Inventory table blank: AnimationBuilder not provided

**Status:** fixed

---

## Description

Nothing is rendered in the inventory table. The Chrome console shows:

```
inventory.component.ts:61 ERROR NullInjectorError: R3InjectorError(Standalone[InventoryComponent])[AnimationBuilder -> AnimationBuilder -> AnimationBuilder -> AnimationBuilder]:
  NullInjectorError: No provider for AnimationBuilder!
```

`ClarityModule` (from `@clr/angular`) internally depends on Angular's `AnimationBuilder`, which is registered by the animations module. Because the app uses the standalone `bootstrapApplication` API in `main.ts` and no animations provider is included, `AnimationBuilder` is never registered, causing the Clarity datagrid to throw at runtime and leaving the inventory view blank.

---

## Root Cause

`main.ts` bootstraps with `bootstrapApplication` and only provides `provideRouter` and `provideHttpClient`. `provideAnimations()` (or `provideAnimationsAsync()`) from `@angular/platform-browser/animations` is absent, so Angular's DI container has no `AnimationBuilder` binding when Clarity tries to inject it.

---

## Fix

Add `provideAnimations()` to the `providers` array in `main.ts`.

---

## Acceptance Criteria

- [x] No `NullInjectorError` for `AnimationBuilder` in the Chrome console.
- [x] The inventory table renders items correctly.
- [x] Existing Angular unit tests still pass.

---

## Affected Files

| File | Change |
|------|--------|
| `frontend/src/main.ts` | Added `provideAnimations()` import and provider |

---

## Changelog

| Date | Status | Note |
|------|--------|------|
| 2026-03-25 | reported | Bug reported: AnimationBuilder not provided — inventory table blank |
| 2026-03-25 | in-progress | Investigation and fix started |
| 2026-03-25 | implemented | Added provideAnimations() to main.ts; all 13 unit tests pass |
| 2026-03-25 | fixed | Accepted by user |
