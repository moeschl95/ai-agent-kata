---
name: task-execution
description: >
  Execute an approved task: read the task file, drive the full Red-Green-Refactor TDD cycle,
  update task status through in-progress → implemented → done, maintain the Changelog at every
  transition, and produce a SUMMARY.md when the user accepts the work. Use this skill whenever
  the user says "implement", "start work", "build it", "go ahead", or anything that signals they
  want code written for an existing ready-for-development task. Also use it when the user reviews
  completed work (accepts or rejects an implemented task), or asks for a summary of what was built.
  Do NOT use this skill for planning or creating new tasks — that is the requirements-engineering
  skill. Do NOT use this skill for bug reports or bug fixes — that is the bug-workflow skill.
---

# Task Execution Workflow

This skill drives an approved task from `ready-for-development` all the way through to `done`,
following TDD and keeping the task file, Changelog, and OVERVIEW.md in sync at every step.

The scope starts when the user asks to implement a `ready-for-development` task, and ends when
the user accepts the result and the task is marked `done`.

---

## Workflow overview

```
ready-for-development  (set by user via requirements-engineering skill)
        │
        ▼  agent starts work
    in-progress
        │
        ▼  TDD: Red → Green → Refactor
        │  run tests, confirm all pass
        ▼
    implemented  ←  agent signals done
        │
      ┌─┴──────────────┐
  User accepts     User rejects
      │                    │
      ▼                    ▼
    done            back to in-progress
    + SUMMARY.md        (note feedback)
```

---

## Step 1 — Start work (ready-for-development → in-progress)

Before writing any code:

1. Read the task file (`.requirements/<ID>-<short-title>/<ID>-<short-title>.md`) to understand the
   description, implementation plan, and acceptance criteria.
2. Update `**Status:**` in the task file to `in-progress`.
3. Append a Changelog row:
   ```
   | YYYY-MM-DD | in-progress | Implementation started |
   ```
4. **MANDATORY — update `/.requirements/OVERVIEW.md`** — change the `Status` cell for this task
   to `in-progress`. **Do this in the same edit pass as step 2. Both files must be updated before
   writing any code. Never leave OVERVIEW.md out of sync with the task file.**

> **Only start work when the user explicitly asks to implement** (e.g. "implement it", "start work",
> "build it"). Approval alone is NOT a trigger — wait for an explicit implementation request.

---

## Step 2 — Implement using TDD (Red → Green → Refactor)

Follow the mandatory Red-Green-Refactor cycle from `AGENTS.md`:

### Red — write a failing test first
- Write the test before any production code.
- Test names must follow `should_<expected behavior>_when_<condition>`.
- Structure every test with Arrange / Act / Assert.
- Run the tests and confirm the new test fails for the right reason.

### Green — write the minimum production code
- Write the smallest change that makes the failing test pass.
- Do not add features, error handling, or abstractions beyond what the test demands.
- Run the tests again and confirm all pass (including pre-existing ones).

### Refactor — clean up without changing behaviour
- Remove duplication, improve naming, flatten complexity.
- Tests must still pass after every refactor step.
- Respect project constraints: do not modify `Item.java`; keep domain classes free of framework
  annotations; follow SOLID and the patterns already established in the codebase.

Repeat the cycle for each acceptance criterion until all criteria are met.

---

## Step 3 — Finish work (in-progress → implemented)

When all acceptance criteria are satisfied and the full test suite passes:

1. **IMPORTANT — Apply code formatting:**
   - Run `.\gradlew.bat spotlessApply` to auto-format all Java code and remove unused imports.
   - Run `.\gradlew.bat spotlessCheck` to verify formatting compliance.
   - Run `.\gradlew.bat test` one final time to confirm all tests still pass after formatting.
   
2. Update `**Status:**` in the task file to `implemented`.
3. Append a Changelog row:
   ```
   | YYYY-MM-DD | implemented | <short summary: what was built, test count> |
   ```
4. **MANDATORY — update `/.requirements/OVERVIEW.md`** — change the `Status` cell for this task
   to `implemented`. **Do this in the same edit pass as step 3. Both files must be updated
   together. Never leave OVERVIEW.md out of sync with the task file.**
5. Report to the user: summarise what was built, which files were changed, and how many tests pass.
   Invite them to review.

---

## Step 4 — Handle the user's review

### If the user accepts (says "done", "looks good", "approved", "lgtm", "ship it", etc.)

Transition to `done`:

1. Update `**Status:**` in the task file to `done`.
2. Append a Changelog row:
   ```
   | YYYY-MM-DD | done | Accepted by user |
   ```
3. **MANDATORY — update `/.requirements/OVERVIEW.md`** — change the `Status` cell for this task
   to `done`. **Do this in the same edit pass as step 1. Never leave OVERVIEW.md out of sync
   with the task file.**
4. Create `requirements/<ID>-<short-title>/SUMMARY.md` using the template below.

### If the user rejects (requests changes or reports failures)

Move back to `in-progress`:

1. Update `**Status:**` in the task file to `in-progress`.
2. Append a Changelog row:
   ```
   | YYYY-MM-DD | in-progress | Returned: <short summary of feedback> |
   ```
3. **MANDATORY — update `/.requirements/OVERVIEW.md`** — change the `Status` cell for this task
   to `in-progress`. **Do this in the same edit pass as step 1. Never leave OVERVIEW.md out of
   sync with the task file.**
4. Address the feedback, then return to Step 2.

---

## SUMMARY.md template

Create this file at `.requirements/<ID>-<short-title>/SUMMARY.md` when the task moves to `done`.
Fill every section with real content — do not leave template text.

```markdown
# <ID> — <Human-readable title> — Implementation Summary

**Date:** YYYY-MM-DD
**Model:** <model name>

---

## What Was Implemented

<2–4 sentences describing what was built. Focus on the outcome: what the code now does that it
didn't do before. Reference specific classes, methods, or patterns introduced.>

---

## Problems Addressed During Development

<Bullet list of noteworthy problems, surprises, or non-obvious decisions encountered while
implementing. Include: edge cases discovered, design pivots, test failures that revealed bugs,
refactoring decisions, or constraints that shaped the solution.>

- …
- …

---

## Files Changed

<List of files that were created or modified as part of this task.>

- `path/to/File.java` — <one-line description of the change>
- …
```

---

## Status reference (full lifecycle)

| Status | Set by | Meaning |
|---|---|---|
| `funnel` | Agent (requirements-engineering) | Freshly created, awaiting approval |
| `ready-for-development` | User | Approved — safe to implement |
| `in-progress` | Agent (this skill) | Actively being implemented |
| `implemented` | Agent (this skill) | Done — waiting for user review |
| `done` | User | Accepted — task is closed |

Transition rules:
- Only the **user** can move `funnel` → `ready-for-development`.
- Only the **user** can move `implemented` → `done`.
- This skill moves `ready-for-development` → `in-progress` (when the user asks to implement).
- This skill moves `in-progress` → `implemented` (when all criteria pass).
- This skill moves `implemented` → `in-progress` (if the user rejects — note the feedback).

> **Bug reports and bug fixes use a separate workflow.** If the user describes something broken
> or unexpected in the running app, use the `bug-workflow` skill instead.

---

## Rules and constraints

- **Never start without an explicit implementation request.** Approval alone does not trigger
  implementation. Wait for a clear signal: "implement", "build it", "start work", etc.
- **Never write production code without a failing test first.** TDD is mandatory per `AGENTS.md`.
- **Follow project code conventions.** Before writing any Java code, consult
  [`.claude/conventions/JAVA_CODE_CONVENTIONS.md`](../../../.claude/conventions/JAVA_CODE_CONVENTIONS.md):
  `final` on all parameters, Javadoc on every public class/method, early exits over `if-else`, and
  `List`/`Set`/`Map` instead of plain arrays.
- **Do not modify `Item.java`.** It is an immutable dependency (the kata's "goblin" rule).
- **Keep domain classes annotation-free.** Spring or framework annotations belong only in
  `@Configuration` classes, not in `GildedRose.java`, `Item.java`, or the updater hierarchy.
- **Changelog is append-only.** Never edit or delete an existing row. Add a new row for every
  status change or significant decision.
- **Always update `/.requirements/OVERVIEW.md`** whenever the task status changes.
- **Run the full test suite** (`gradlew test`) before marking a task `implemented`.
