# 022 — Spotless Code Formatting & Unused Import Cleanup

**Status:** implemented

---

## Description

The backend Java source files contain unused imports and inconsistent formatting. A Spotless Gradle plugin should be added to enforce Google Java Format style, automatically remove unused imports, and trim trailing whitespace. Developers can run a single Gradle task to auto-fix all formatting issues, and CI can use a check-only task to fail on violations.

---

## Implementation Plan

1. Add the `com.diffplug.spotless` plugin (v6.25.0) to `build.gradle.kts` plugins block.
2. Configure a `spotless { java { ... } }` block with:
   - `googleJavaFormat()` — enforces consistent code style
   - `removeUnusedImports()` — strips unused import statements
   - `trimTrailingWhitespace()` — removes trailing whitespace
   - `endWithNewline()` — ensures files end with a newline
3. Run `.\gradlew.bat spotlessApply` to auto-format all existing Java source files.
4. Verify `.\gradlew.bat spotlessCheck` passes (no remaining violations).
5. Verify `.\gradlew.bat test` still passes after reformatting.

---

## Acceptance Criteria

- [ ] Spotless plugin is present in `build.gradle.kts`.
- [ ] `.\gradlew.bat spotlessCheck` exits with `BUILD SUCCESSFUL` (no violations).
- [ ] `.\gradlew.bat spotlessApply` auto-fixes all formatting and unused imports without errors.
- [ ] All existing tests pass after formatting is applied.
- [ ] No unused imports remain in any backend Java source file.

---

## Notes

Spotless uses Google Java Format under the hood for style enforcement. The `removeUnusedImports()` step is independent of the formatter and handles import cleanup directly.

---

## Changelog

| Date | Status | Note |
|------|--------|------|
| 2026-03-27 | funnel | Task created |
| 2026-03-27 | ready-for-development | Approved by user |
| 2026-03-27 | in-progress | Implementation started |
| 2026-03-27 | implemented | Spotless plugin configured; all formatting applied and verified |
