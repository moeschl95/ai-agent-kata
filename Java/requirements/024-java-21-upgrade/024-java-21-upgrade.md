# 024 — Java 21 Upgrade

**Status:** ready-for-development

---

## Description

Upgrade the project from Java 17 to Java 21 to take advantage of newer language features, performance improvements, and long-term support. Java 21 introduces stable records, sealed classes, pattern matching enhancements, and virtual threads — all of which can improve code clarity and application efficiency.

---

## Implementation Plan

1. Update `build.gradle.kts` — change `sourceCompatibility` and `targetCompatibility` from `JavaVersion.VERSION_17` to `JavaVersion.VERSION_21`.

2. Update `gradle.properties` — review JVM arguments and add any flags needed for Java 21 (document any deprecations or removals that affect the project).

3. Run `gradlew clean test` (Windows: `.\gradlew.bat clean test`) to ensure all tests pass with the new Java version.

4. Check for any deprecated APIs or warnings in the compilation output and address them if present.

5. Verify the backend Spring Boot application starts without errors: `gradlew bootRun`.

6. Run the full test suite for both backend and frontend (E2E tests) to confirm no regressions.

---

## Acceptance Criteria

- [ ] `build.gradle.kts` `sourceCompatibility` and `targetCompatibility` are set to `JavaVersion.VERSION_21`.
- [ ] `gradle.properties` is reviewed and updated if needed for Java 21 compatibility.
- [ ] All backend tests pass with `gradlew test`.
- [ ] The backend application starts successfully with `gradlew bootRun` (no startup errors).
- [ ] No compilation warnings or deprecation errors related to Java 17 → 21 upgrade.
- [ ] All E2E tests pass (Playwright tests in `frontend/e2e/`).

---

## Notes

- Java 21 is an LTS (Long-Term Support) release, making it a stable choice for production.
- The upgrade is straightforward since Java 21 is backward compatible with Java 17 code patterns.
- No significant code changes are expected; this is primarily a configuration update.

---

## Changelog

| Date | Status | Note |
|------|--------|------|
| 2026-03-27 | funnel | Task created |
| 2026-03-27 | ready-for-development | Approved |
