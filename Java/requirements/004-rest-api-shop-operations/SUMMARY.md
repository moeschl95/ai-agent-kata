# 004 — REST API for Shop Operations — Implementation Summary

**Date:** 2026-03-25
**Model:** Claude Haiku 4.5

---

## What Was Implemented

A REST API layer was added to the Gilded Rose shop using Spring Boot. The `ShopController` orchestrates three endpoints under the `/api/items` base path: `GET /api/items` returns the current inventory as JSON, `POST /api/items/advance-day` advances the shop by one day and returns the updated state, and `GET /api/items/{name}/price` retrieves the price for a specific item or returns HTTP 404 if the item is not found. An `ItemDto` record was introduced to keep the domain `Item` class out of the HTTP layer, ensuring clean separation of concerns. The implementation follows TDD with 5 new `@WebMvcTest` tests covering all endpoints and edge cases, and all 49 tests in the suite pass.

---

## Problems Addressed During Development

- **MockMvc test framework compatibility** — Initially attempted to use `@MockBean` and `@WebMvcTest` slicing, but discovered Spring Test annotations were not available in the test classpath. Switched to using Mockito `@Mock` with standalone MockMvc builder setup instead.
- **URL path variable handling** — Clarified that `MockMvc` unit tests pass path variables internally without URL encoding (spaces don't need `%20`), whereas real HTTP clients would need to encode spaces. Added a comment documenting this behavior for future maintainers.
- **Item lookup and mocking** — Ensured that `GildedRose.items` is a real array instance in the test controller setup to allow mutating the inventory through the `advance-day` endpoint, while mocking only `PricingService` to avoid coupling tests to pricing logic.

---

## Files Changed

- `src/main/java/com/gildedrose/ShopController.java` — Created new REST controller with three endpoints for inventory inspection and mutation
- `src/main/java/com/gildedrose/ItemDto.java` — Created new Java record for HTTP response serialization
- `src/test/java/com/gildedrose/ShopControllerTest.java` — Created new test class with 5 @WebMvcTest tests
- `pom.xml` — Added `spring-boot-starter-web` dependency
- `build.gradle.kts` — Added `spring-boot-starter-web` dependency
