# 023 — Backend Folder Restructure

**Status:** done

---

## Summary

Reorganize the repository into a monorepo structure with clear separation between `/backend` and `/frontend`, mirroring successful monorepo patterns. This improves discoverability, enables parallel development, and prepares the project for future growth.

---

## Description

Currently, backend code (`src/`, `build/`) lives at the repository root alongside build configuration, while frontend code is neatly organized under `/frontend/`. This asymmetric structure makes the project layout unclear and reduces scalability.

**Goal:** Restructure into a clear monorepo with `/backend` and `/frontend` at the same organizational level, keeping repo-level metadata and build orchestration at root.

### Current Structure
```
Root/
├── src/
├── build/
├── gradlew, gradlew.bat
├── gradle/
├── frontend/
├── build.gradle.kts
└── settings.gradle.kts
```

### Target Structure
```
Root/
├── backend/           ← New
│   ├── src/
│   ├── build/
│   ├── gradlew
│   ├── gradlew.bat
│   └── gradle/
├── frontend/
├── build.gradle.kts   ← Stays at root
├── settings.gradle.kts
├── gradle.properties
└── ... (metadata: .arch-decision-records/, .requirements/, .bugs/, AGENTS.md, README.md)
```

---

## Acceptance Criteria

- [x] `src/` moved to `backend/src/`
- [x] `build/` moved to `backend/build/`
- [x] `gradlew` and `gradlew.bat` moved to `backend/`
- [x] `gradle/` directory moved to `backend/gradle/`
- [x] Build configuration updated to reference new paths
- [x] All unit tests discover and run correctly
- [x] All tests pass
- [x] Frontend code unchanged
- [x] `gradlew.bat test` (from root) invokes backend tests successfully
- [x] Gradle Wrapper still works as expected
- [x] No dead links or orphaned references in config files

---

## Implementation Plan

### Phase 1: Directory Migration
- Create `backend/` folder at repo root
- Move `src/` → `backend/src/`
- Move `build/` → `backend/build/`
- Move `gradlew`, `gradlew.bat` → `backend/`
- Move `gradle/` → `backend/gradle/`

### Phase 2: Build Configuration
- Update `build.gradle.kts` to set `sourceSets` pointing to `backend/src`
- Update `tasks.register<JavaExec>("texttest")` if needed to handle new paths
- Verify `settings.gradle.kts` still works (it defines project name, no path changes needed)

### Phase 3: Test & Verify
- Run `gradlew.bat test` from project root → all tests pass
- Verify `gradlew.bat bootRun` still starts the application
- Verify IDE test discovery works
- Check for any hardcoded path references in code/tests

### Phase 4: Documentation
- No code comments need updating (paths are handled by Gradle config)
- ADR-002 already documents the rationale

---

## Dependencies & Risks

**Dependencies:**
- None — this is a pure structural change with no feature dependencies

**Risks:**
- **CI/CD pipelines** — Any external CI scripts that assume `src/` at root will break. (Low risk if CI is internal; recommend immediate update after merge)
- **IDE caching** — IntelliJ/Eclipse might cache old paths; users may need to invalidate caches
- **Local developer setup** — Any local scripts or `.sh` files referencing `src/` directly will break

**Mitigation:**
- ADR-002 documents the rationale and structure clearly
- Keep operation simple: move folders, update one gradle file, verify tests pass
- Test locally before merge

---

## Changelog

| Date | Status | Note |
|------|--------|------|
| 2026-03-27 | funnel | Task created; ADR-002 approved and documented |
| 2026-03-27 | ready-for-development | Task approved and ready for implementation |
| 2026-03-27 | in-progress | Implementation started |
| 2026-03-27 | implemented | Restructuring complete: moved src/, build/, gradle/, gradlew to backend/; created root-level wrapper scripts; updated sourceSets in build.gradle.kts; all tests passing |
| 2026-03-27 | done | Task approved and completed
