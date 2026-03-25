# 014 — Clean Architecture & DDD Refactor

**Status:** done

---

## Description

The backend lives in a single flat package (`com.gildedrose`) with no enforced layer boundaries.
This makes it easy for controllers to reach into persistence types, mix infrastructure concerns
with domain logic, and accumulate responsibilities in a single class (`GildedRose`).
The goal is to reorganise the code into explicit Clean Architecture layers — domain, application,
infrastructure, and API — so that dependencies always point inward and each layer is independently
testable.

---

## Implementation Plan

1. **Introduce sub-packages** — create the following packages (no code moves yet, just compile-check):
   - `com.gildedrose.domain.model` — pure domain objects
   - `com.gildedrose.domain.service` — domain service interfaces and implementations
   - `com.gildedrose.domain.event` — domain events
   - `com.gildedrose.domain.repository` — repository port (interface only)
   - `com.gildedrose.application` — application service (use-case orchestration) + DTOs
   - `com.gildedrose.infrastructure.persistence` — JPA entity, JPA repository impl, mapper
   - `com.gildedrose.infrastructure.config` — Spring configuration and data initializer
   - `com.gildedrose.api` — REST controller

2. **Move domain types (Red → Green)**
   - Move `Item.java` → `domain.model` (no modification — goblin rule)
   - Move `ItemUpdater`, `ItemUpdaterFactory`, all `*Updater` impls, `ConjuredDecorator` → `domain.service`
   - Move `PricingService` (interface) + `DefaultPricingService` → `domain.service`
   - Move `DayAdvancedEvent`, `ItemExpiredEvent`, `ItemQualityChangedEvent` → `domain.event`
   - Fix all import errors; run tests — must stay green.

3. **Move infrastructure types (Red → Green)**
   - Move `ItemEntity`, `ItemRepository` (JPA interface), `ItemMapper` → `infrastructure.persistence`
   - Move `ItemDataInitializer`, `GildedRoseConfiguration` → `infrastructure.config`
   - Fix all import errors; run tests.

4. **Extract `ShopService` application service (Red → Green)**
   - Write a failing test that verifies `ShopService` can advance the day, return inventory, return a price, and project an item — without any Spring context.
   - Extract these four operations from `GildedRose` and `ShopController` into a new `ShopService` class in `com.gildedrose.application`.
   - `ShopService` depends on a `domain.repository.ItemRepositoryPort` interface (not the JPA interface directly) and on `PricingService` / `ProjectionService`.
   - Make the tests pass.

5. **Define `ItemRepositoryPort` (domain port)**
   - Create interface `domain.repository.ItemRepositoryPort` with `findAll()` and `saveAll(List<Item>)` operations.
   - Create `infrastructure.persistence.JpaItemRepositoryAdapter` that implements the port using `ItemRepository` and `ItemMapper`.
   - Wire the adapter as the `ItemRepositoryPort` bean in `GildedRoseConfiguration`.

6. **Simplify `GildedRose`**
   - `GildedRose` retains only quality-update logic (its original single responsibility).
   - Remove the persistence and event-publishing constructors; leave only the domain constructor `GildedRose(List<Item>)` and a constructor that also accepts `ApplicationEventPublisher` (needed by `ShopService`).
   - Update all references; run tests.

7. **Update `ShopController` (Red → Green)**
   - `ShopController` now depends solely on `ShopService` and `ItemDto` — remove direct dependencies on `GildedRose`, `PricingService`, and `ProjectionService`.
   - Move `ItemDto` to `com.gildedrose.application.dto`.
   - Update controller tests.

8. **Refactor / clean up**
   - Remove any leftover `com.gildedrose` root-package classes (except `GildedRoseApplication.java`).
   - Verify package-level dependency rules: `api` → `application` → `domain`; `infrastructure` → `domain`; nothing in `domain` imports from `application`, `infrastructure`, or `api`.
   - Run full test suite: all tests must pass.

---

## Acceptance Criteria

- [ ] All production classes (except `GildedRoseApplication` and `Item`) are in one of the four layer packages: `domain`, `application`, `infrastructure`, `api`.
- [ ] No class in `domain` imports from `application`, `infrastructure`, or `api`.
- [ ] No class in `application` imports from `infrastructure` or `api`.
- [ ] `ShopController` has no direct dependency on `GildedRose`, `PricingService`, or `ProjectionService` — only on `ShopService`.
- [ ] A `ItemRepositoryPort` interface exists in `domain.repository`; the JPA adapter lives in `infrastructure.persistence`.
- [ ] All existing tests pass without modification (except import updates).
- [ ] No new public API endpoints are added or removed; all existing endpoint URLs and response shapes remain identical.

---

## Notes

### Constraint — `Item.java` must not be modified
`Item.java` is an immutable dependency (the "goblin" rule). It stays as-is; other classes adapt around it.

### Conflicts
Tasks **010**, **011**, and **012** (all `ready-for-development`) depend on the existing REST API contract (`/api/items`, `/api/items/advance-day`, `/api/items/{name}/price`, `/api/items/{name}/projection`). This refactor must preserve all endpoint URLs and response shapes exactly — those tasks are unaffected as long as the API surface is unchanged.

---

## Changelog

| Date | Status | Note |
|------|--------|------|
| 2026-03-25 | funnel | Task created |
| 2026-03-25 | ready-for-development | Approved by user |
| 2026-03-25 | in-progress | Implementation started |
| 2026-03-25 | implemented | Test files reorganized into matching package structure; integration test adapter fixed to preserve entity IDs; all 86 tests passing || 2026-03-25 | done | Accepted; GildedRose.java moved to domain.service layer; all acceptance criteria met |