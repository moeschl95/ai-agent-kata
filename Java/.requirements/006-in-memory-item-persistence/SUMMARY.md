# 006 — In-Memory Item Persistence — Implementation Summary

**Date:** 2026-03-25
**Model:** Claude Haiku 4.5

---

## What Was Implemented

Introduced a complete Spring Data JPA persistence layer backed by H2 in-memory database, enabling items to survive across application restarts within a JVM session. The architecture leverages `ItemEntity` as a dedicated persistence model, `ItemRepository` for CRUD operations, `ItemMapper` for frictionless domain-to-persistence conversions, and `ItemDataInitializer` to seed default inventory on startup. `GildedRose` now accepts an optional `ItemRepository` constructor, loads items on initialization, and persists updated state after each `updateQuality()` call. This abstraction allows swapping to MariaDB purely through configuration changes without modifying Java code.

---

## Problems Addressed During Development

- **ID Preservation on Persistence**: Initial implementation lost database IDs when persisting updated items because the mapper didn't preserve them. Solved by introducing `ItemIdMap` in `GildedRose` to track original IDs and `ItemMapper.toEntityWithId()` to preserve them during updates.
- **Backward Compatibility**: Maintained the existing no-arg/List-based constructor in `GildedRose` to ensure all existing unit tests pass without modification, while introducing a new repository-aware constructor for Spring bean injection.
- **Separation of Concerns**: `ItemDataInitializer` as a separate component ensures seeding logic doesn't clutter `GildedRoseConfiguration` and leverages Spring's `ApplicationReadyEvent` to seed only after the full app context is ready.
- **Configuration Documentation**: Added comprehensive commented-out MariaDB configuration block in `application.properties` to guide future database migrations.

---

## Files Changed

- [pom.xml](pom.xml#L30-L42) — Added `spring-boot-starter-data-jpa` and `com.h2database:h2` (runtime scope) dependencies
- [build.gradle.kts](build.gradle.kts#L16-L20) — Added corresponding Gradle dependencies
- [src/main/java/com/gildedrose/ItemEntity.java](src/main/java/com/gildedrose/ItemEntity.java) — **Created**: JPA entity mapped to ITEMS table with `@Id @GeneratedValue` ID field
- [src/main/java/com/gildedrose/ItemRepository.java](src/main/java/com/gildedrose/ItemRepository.java) — **Created**: Spring Data JPA repository interface extending `JpaRepository<ItemEntity, Long>`
- [src/main/java/com/gildedrose/ItemMapper.java](src/main/java/com/gildedrose/ItemMapper.java) — **Created**: Utility class for bidirectional Item ↔ ItemEntity conversion with ID preservation
- [src/main/java/com/gildedrose/ItemDataInitializer.java](src/main/java/com/gildedrose/ItemDataInitializer.java) — **Created**: Spring component using `@EventListener(ApplicationReadyEvent.class)` to seed default items if table is empty
- [src/main/java/com/gildedrose/GildedRose.java](src/main/java/com/gildedrose/GildedRose.java) — Modified to add repository-aware constructor, ID tracking map, and persistence logic in `updateQuality()`
- [src/main/java/com/gildedrose/GildedRoseConfiguration.java](src/main/java/com/gildedrose/GildedRoseConfiguration.java) — Updated `gildedRose()` bean to use repository constructor and removed hardcoded item list
- [src/main/resources/application.properties](src/main/resources/application.properties) — Added H2 JPA configuration and commented-out MariaDB block for future migrations
- [src/test/java/com/gildedrose/ItemRepositoryTest.java](src/test/java/com/gildedrose/ItemRepositoryTest.java) — **Created**: `@DataJpaTest` slice tests for repository CRUD operations (3 tests)
- [src/test/java/com/gildedrose/GildedRoseIntegrationTest.java](src/test/java/com/gildedrose/GildedRoseIntegrationTest.java) — **Created**: Integration tests verifying persistence of updated items (3 tests)

---

## Test Results

- **Total tests passing:** 76 (34 existing GildedRoseTest + 10 ShopControllerTest + 16 ProjectionServiceTest + 6 PricingServiceTest + 4 GildedRoseApplicationTest + 3 ItemRepositoryTest + 3 GildedRoseIntegrationTest)
- **New tests added:** 6 (3 repository + 3 integration)
- **All acceptance criteria met:** ✓
