# 007 — Remove Maven Build

**Status:** done

---

## Description

The project currently supports two build systems: Maven and Gradle. Since only Gradle is needed, all Maven-related files and artefacts should be removed to reduce clutter, eliminate duplicate configuration, and make the project easier to maintain. After this cleanup, Gradle will be the sole build tool for compiling, testing, and packaging the application.

---

## Implementation Plan

1. Delete `pom.xml` from the project root.
2. Delete the Maven wrapper scripts `mvnw` and `mvnw.cmd` from the project root.
3. Delete the `.mvn/` directory (contains `wrapper/MavenWrapperDownloader.java`, `maven-wrapper.properties`, and `maven-wrapper.jar`).
4. Delete the `target/` directory (Maven build output; Gradle uses `build/`).
5. Update `README.md` — remove any references to Maven commands (e.g. `./mvnw test`) and replace with Gradle equivalents (`./gradlew test`).
6. Verify `gradlew.bat test` still passes after all deletions.

---

## Acceptance Criteria

- [ ] `pom.xml` no longer exists in the repository.
- [ ] `mvnw` and `mvnw.cmd` no longer exist in the repository.
- [ ] `.mvn/` directory no longer exists in the repository.
- [ ] `target/` directory no longer exists in the repository.
- [ ] `README.md` contains no references to Maven or `mvnw`.
- [ ] `./gradlew test` (or `gradlew.bat test` on Windows) runs and all tests pass.

---

## Notes

No functional code changes are involved. This is a pure project-structure cleanup.

---

## Changelog

| Date | Status | Note |
|------|--------|------|
| 2026-03-25 | funnel | Task created |
| 2026-03-25 | ready-for-development | Approved by user |
| 2026-03-25 | in-progress | Implementation started |
| 2026-03-25 | implemented | Maven files deleted, README clean, all Gradle tests pass |
| 2026-03-25 | done | Accepted by user |
