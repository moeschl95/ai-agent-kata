# 003 — Spring Boot Migration

**Status:** ready-for-development

---

## Description

The Gilded Rose application should be migrated from a standalone plain-Java project to a Spring
Boot application. The GildedRose engine and PricingService will be wired as Spring-managed beans
and configuration (item prices, discount rate, legendary price) will be externalised to
`application.properties`. The Gradle build files will be converted from Groovy DSL to Kotlin DSL
at the same time as the Java version upgrade. The migration establishes the Spring Boot foundation
without adding a REST API — that is a separate future feature.

---

## Implementation Plan

1. **Write failing Spring Boot integration tests (Red)** in a new `GildedRoseApplicationTest.java`:
   - Verify the Spring application context loads without errors.
   - Verify `GildedRose` and `PricingService` beans are present in the context.
   - Verify `DefaultPricingService` receives the configured legendary price and discount rate.

2. **Migrate Gradle files to Kotlin DSL and adopt Spring Boot** (combined with the Java upgrade):
   - Rename `build.gradle` → `build.gradle.kts` and `settings.gradle` → `settings.gradle.kts`;
     convert all Groovy syntax to idiomatic Kotlin DSL.
   - Apply the `org.springframework.boot` plugin (version `3.2.x`) and `io.spring.dependency-management`.
   - Replace the plain `java` plugin with Spring Boot's managed plugin chain.
   - Set `java.sourceCompatibility = JavaVersion.VERSION_17` (Kotlin DSL style).
   - Add `implementation("org.springframework.boot:spring-boot-starter")`.
   - Add `testImplementation("org.springframework.boot:spring-boot-starter-test")` (brings JUnit 5, AssertJ).
   - Remove the manually-pinned JUnit Jupiter dependencies (now managed by Spring Boot BOM).
   - Keep the `jacoco` plugin and `jacocoTestReport` task, rewritten in Kotlin DSL.
   - Retain the `texttest` `JavaExec` task in Kotlin DSL.

3. **Update `pom.xml`** to use `spring-boot-starter-parent` as the parent POM and add matching
   `spring-boot-starter` and `spring-boot-starter-test` dependencies. Upgrade Java source level
   to 17 minimum (required by Spring Boot 3.x). (`pom.xml` remains Groovy-free; no DSL change needed.)

4. **Create `GildedRoseApplication.java`** in `src/main/java/com/gildedrose/`:
   - Annotated with `@SpringBootApplication`.
   - Contains a standard `main(String[] args)` method calling `SpringApplication.run(...)`.

5. **Create `GildedRoseConfiguration.java`** (`@Configuration` class):
   - Define a `@Bean` for `GildedRose` constructed with an empty `Item[]` by default.
   - Define a `@Bean` for `DefaultPricingService` reading `basePrices`, `expiredDiscountRate`,
     and `legendaryPrice` from `application.properties` via `@ConfigurationProperties` or `@Value`.

6. **Add `src/main/resources/application.properties`** with sensible defaults:
   - `gilded-rose.legendary-price=999`
   - `gilded-rose.expired-discount-rate=0.5`
   - `gilded-rose.base-prices.<name>=<price>` entries for known items.

7. **Run tests (Green)** — all new Spring Boot integration tests pass and all existing unit tests
   continue to pass (they remain framework-free and compile unchanged under the new BOM).

8. **Refactor** if needed — keep `GildedRose.java` and `Item.java` free of Spring annotations
   (domain purity; all wiring lives in the `@Configuration` class).

9. **Run full test suite** (`gradlew test`) before marking task implemented.

---

## Acceptance Criteria

- [ ] `GildedRoseApplication` starts with `java -jar` without errors.
- [ ] Spring application context loads all beans without errors in tests.
- [ ] `GildedRose` and `PricingService` are resolvable as Spring beans.
- [ ] `DefaultPricingService` is configured from `application.properties` (legendary price, discount rate).
- [ ] `Item.java` is **not modified**.
- [ ] `GildedRose.java` carries **no Spring annotations** (domain stays pure).
- [ ] All existing unit tests (`GildedRoseTest`, `PricingServiceTest`) continue to pass.
- [ ] Java source level is upgraded to **17** (required by Spring Boot 3.x).
- [ ] Gradle build files are converted to **Kotlin DSL** (`build.gradle.kts`, `settings.gradle.kts`).
- [ ] `./gradlew test` succeeds from the Kotlin DSL build files.
- [ ] Both Gradle and Maven builds produce a runnable Spring Boot fat-jar.

---

## Notes

- Spring Boot 3.x requires Java 17+. The current project targets Java 8 (`sourceCompatibility = '1.8'`).
  The Java version upgrade and the Kotlin DSL migration happen together in the same build-file step.
- Kotlin DSL (`build.gradle.kts`) is the modern Gradle standard: it provides type safety, better
  IDE support, and is consistent with Spring Initializr defaults. The old `build.gradle` (Groovy)
  is deleted as part of this task.
- `item.java` and `GildedRose.java` must remain free of Spring annotations (domain purity);
  all wiring lives in the `@Configuration` class.
- The existing `TexttestFixture` Gradle task (`texttest`) should be retained, rewritten in Kotlin DSL.
- **Future feature:** A REST API exposing `POST /items/update` and `GET /items/{name}/price` is a
  natural next step once this migration is in place (planned as task 004).

### Conflicts

| Task | Overlap |
|------|------|
| 002-dual-type-conjured-items | Task 002 modifies `ItemUpdaterFactory.forItem()`, which is used internally by `GildedRose`. The Spring Boot wiring in this task must be applied on top of 002's changes. Task 002 is currently `implemented` (not yet `done`). Sequencing dependency only — no code conflict expected. |

---

## Changelog

| Date | Status | Note |
|------|--------|------|
| 2026-03-25 | funnel | Task created |
| 2026-03-25 | funnel | Revised: removed REST API scope; added future-feature note; simplified plan to spring-boot-starter (no web) |
| 2026-03-25 | funnel | Revised: added Gradle Kotlin DSL migration alongside the Java 17 upgrade |
| 2026-03-25 | ready-for-development | Approved by user |
