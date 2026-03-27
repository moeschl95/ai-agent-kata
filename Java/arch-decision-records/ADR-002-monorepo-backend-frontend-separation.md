# ADR-002: Monorepo Structure with `/backend` and `/frontend` Separation

**Date:** 2026-03-27  
**Status:** Approved  
**Decision Maker:** User

## Context

The repository currently has an asymmetric structure:
- Backend code (`src/`, `build/`) mixed with build configuration at root level
- Frontend code neatly organized under `/frontend/`
- As the project grows, the flat structure reduces clarity and makes it harder to:
  - Understand project organization at a glance
  - Parallelize backend and frontend development
  - Manage different dependency ecosystems clearly

## Decision

Restructure the repository into a monorepo with clear separation:

```
Root
├── backend/                     ← Backend Java source and build output
│   ├── src/
│   ├── build/
│   ├── gradlew
│   └── gradlew.bat
├── frontend/                    ← Frontend Angular code (unchanged)
├── gradle/                      ← Gradle wrapper config (stays at root)
│   └── wrapper/
├── build.gradle.kts             ← Java build config
├── settings.gradle.kts
├── gradle.properties
├── arch-decision-records/       ← Repo-level metadata
├── requirements/
├── bugs/
├── AGENTS.md
├── README.md
└── ...
```

## Consequences

### Positive
✅ **Clear monorepo structure** — Frontend and backend are at the same organizational level  
✅ **Improved discoverability** — Developers immediately understand where code lives  
✅ **Scalable organization** — Additional backend modules could be added under `backend/` in future  
✅ **Reduced cognitive load** — Root directory reserved for repo-level metadata and build orchestration  
✅ **Team independence** — Backend and frontend teams can work in parallel without interference  

### Negative
⚠️ **Build invocation consistency** — Gradle commands must run from project root but reference `backend/`  
⚠️ **Path updates** — Java code is inside `backend/`, so build config and test discovery need adjustment  
⚠️ **One-time refactoring cost** — Moving folders and updating paths across build and test files  

## Alternatives Considered

1. **Keep current flat structure** — Simpler initially but scales poorly as project grows. Rejected.
2. **Move Gradle config to `/backend/`** — Would require CI/CD scripts and developer workflows to `cd backend && gradlew` first. Rejected in favor of keeping build config at root.
3. **Alternative monorepo tools** — Maven multi-module or Gradle composite builds. Determined that simple folder move is sufficient for current project scope.

## Implementation Details

1. Move `src/` → `backend/src/`
2. Move `build/` → `backend/build/`
3. Move `gradlew` and `gradlew.bat` → `backend/`
4. Update `build.gradle.kts` to configure sourceSets to reference `backend/src`
5. Verify all tests run and pass

## References

- [Gradle Documentation: Source Sets](https://docs.gradle.org/current/userguide/java_plugin.html#sec:java_source_sets)
- [Monorepo Pattern](https://en.wikipedia.org/wiki/Monorepo)
