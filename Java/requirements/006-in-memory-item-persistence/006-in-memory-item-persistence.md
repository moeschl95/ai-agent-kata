# 006 — In-Memory Item Persistence

**Status:** ready-for-development

---

## Description

Items in the Gilded Rose shop currently exist only in a hardcoded in-memory array that is lost
on every restart. This task introduces a persistence layer using Spring Data JPA backed by an
H2 in-memory database, so items survive between application restarts (within the same JVM
session) and can be managed independently of the boot configuration. The abstraction layer
(Spring Data `JpaRepository`) is chosen deliberately so that switching to a real database such
as MariaDB in the future requires only a dependency and `application.properties` change — no
Java code changes.

---

## Implementation Plan

1. **Write failing tests (Red)**:
   - `ItemRepositoryTest` (Spring Data slice with `@DataJpaTest`): verify that an `ItemEntity`
     can be saved and loaded by ID and by name.
   - `GildedRoseIntegrationTest`: seed two items via the repository; call `updateQuality()`;
     assert that the persisted state reflects the updated values.

2. **Add dependencies** to both `pom.xml` and `build.gradle.kts`:
   - `spring-boot-starter-data-jpa`
   - `com.h2database:h2` (runtime/test scope)

3. **Create `ItemEntity.java`** — a JPA `@Entity` mapped to a `ITEMS` table. Fields:
   `Long id` (`@Id @GeneratedValue`), `String name`, `int sellIn`, `int quality`. Since
   `Item.java` must not be modified, this is a separate persistence model.

4. **Create `ItemRepository.java`** — `public interface ItemRepository extends
   JpaRepository<ItemEntity, Long>`. No custom query methods needed initially.

5. **Create `ItemMapper.java`** — static utility with two methods:
   - `toEntity(Item item) → ItemEntity`
   - `toDomain(ItemEntity entity) → Item`
   This keeps the conversion logic in one place for easy maintenance.

6. **Adapt `GildedRose.java`**:
   - Add a second constructor `GildedRose(ItemRepository repository)` (primary Spring bean).
   - On construction, load all entities from the repository, map them to `Item[]`, and assign to
     the existing `items` field — preserving full backward compatibility with existing tests.
   - At the end of `updateQuality()`, persist the updated `items` array back to the repository
     (map each `Item` back to `ItemEntity` and call `repository.saveAll(...)`).
   - The no-arg/array constructor used by existing unit tests is retained unchanged.

7. **Create `ItemDataInitializer.java`** — a `@Component` using
   `@EventListener(ApplicationReadyEvent.class)` that seeds the default shop inventory if the
   `ITEMS` table is empty. Extract the seed list from `GildedRoseConfiguration.gildedRose()`
   into this initializer.

8. **Update `GildedRoseConfiguration.gildedRose()` bean** — inject `ItemRepository` and
   construct `GildedRose` with the repository so it loads persisted items at startup.

9. **Document the MariaDB swap** in `application.properties` as commented-out properties:
   ```properties
   # To switch from H2 in-memory to MariaDB, replace the block below with:
   # spring.datasource.url=jdbc:mariadb://localhost:3306/gildedrose
   # spring.datasource.username=<user>
   # spring.datasource.password=<password>
   # spring.datasource.driver-class-name=org.mariadb.jdbc.Driver
   # spring.jpa.database-platform=org.hibernate.dialect.MariaDBDialect
   # and add mariadb-java-client to dependencies.
   ```

10. **Run all tests (Green)** — `@DataJpaTest`, integration test, and all existing unit tests pass.

11. **Refactor** if needed — ensure `ItemMapper`, `ItemDataInitializer`, and `GildedRose` each have
    a single responsibility; no business logic in the persistence layer.

---

## Acceptance Criteria

- [ ] Items are stored in and loaded from an H2 in-memory database at application startup.
- [ ] After `updateQuality()` is called, the updated state is persisted to the database.
- [ ] Application startup seeds default items if none exist in the database yet.
- [ ] All new repository and integration behaviour is covered by tests written before production code (TDD).
- [ ] All existing unit tests (`GildedRoseTest`, `PricingServiceTest`) continue to pass without modification.
- [ ] `Item.java` is not modified.
- [ ] `GildedRose.java` still exposes the `public Item[] items` field (backward-compatible with tasks 004 and 005).
- [ ] Switching to MariaDB requires only a dependency addition and `application.properties` changes — no Java code changes.
- [ ] The `application.properties` file contains a commented-out MariaDB configuration block.

---

## Notes

### Why 1 task instead of 2?

"Easy swap to MariaDB" is achieved entirely through the architectural choice of Spring Data JPA
and profile-based configuration — there is no separate code to write. Splitting into two tasks
would create an artificial boundary with no independent deliverable.

### Why H2 and not a plain Java `Map`?

Using H2 + JPA means the swap to any JPA-compatible database (MariaDB, PostgreSQL, MySQL) is
purely a configuration concern. A plain `Map` would require replacing the persistence layer
entirely when switching databases.

### Conflicts

- **Task 004 (REST API for Shop Operations):** That task's `@WebMvcTest` slice tests mock
  `GildedRose` and `PricingService` directly, so the repository is not visible at the slice
  test level. However, integration tests for task 004 will need the repository to be seeded.
  Task 006 should be implemented **before** task 004 to avoid rework.

- **Task 005 (Item State Projection):** The projection endpoint reads `GildedRose.items` — this
  field is preserved by the design in this task, so no breaking change. No reordering required.

---

## Changelog

| Date | Status | Note |
|------|--------|------|
| 2026-03-25 | funnel | Task created |
| 2026-03-25 | ready-for-development | Approved by user |
