# Agent Guidelines

This file defines the coding standards and workflow requirements for AI agents working in this repository.

---

## Test Driven Development (MANDATORY)

**TDD is required.** All code changes must follow the Red-Green-Refactor cycle:

1. **Red** — Write a failing test that describes the desired behavior before writing any production code.
2. **Green** — Write the minimum production code necessary to make the test pass.
3. **Refactor** — Clean up the code without changing behavior. Tests must still pass after refactoring.

Never write production code without a corresponding failing test written first. This applies to new features, bug fixes, and refactors that change behavior.

---

## General Coding Guidelines

### Clarity Over Cleverness
- Write code that is easy to read and understand.
- Prefer explicit, descriptive names for variables, methods, and classes over abbreviations or single-letter names.
- Avoid deeply nested logic; extract methods to flatten complexity.

### Single Responsibility
- Each class and method should do one thing well.
- If a method needs a comment to explain what it does, consider extracting it into a named method instead.

### Small, Focused Methods
- Keep methods short — aim for methods that fit on one screen.
- A method that does setup, execution, and teardown is likely doing too much.

### Avoid Magic Numbers and Strings
- Replace literal values with named constants.
- Example: prefer `MAX_QUALITY = 50` over the bare number `50` scattered throughout the code.

### Don't Repeat Yourself (DRY)
- Duplicate logic is a maintenance liability. Extract shared behavior into reusable methods or abstractions.
- Exception: don't over-abstract prematurely — duplication is better than the wrong abstraction.

### SOLID Principles
- **S**ingle Responsibility: one reason to change per class.
- **O**pen/Closed: extend behavior without modifying existing code (e.g., use polymorphism over `if/else` chains on type names).
- **L**iskov Substitution: subtypes must be substitutable for their base types.
- **I**nterface Segregation: prefer narrow, focused interfaces.
- **D**ependency Inversion: depend on abstractions, not concrete implementations.

### Prefer Composition and Polymorphism Over Conditionals
- Long `if/else` or `switch` blocks that branch on item type or category are a code smell.
- Favor polymorphism (strategy, template method, etc.) to handle type-based behavior.

---

## Testing Guidelines

### Test Naming
- Test names must clearly describe the scenario and expected outcome.
- Use the pattern: `should_<expected behavior>_when_<condition>`.
- Example: `should_decreaseQualityByOne_when_sellInIsPositive`

### One Assertion Per Test (Preferred)
- Each test should validate a single behavior.
- Multiple assertions are acceptable only when they collectively verify one logical outcome.

### Arrange-Act-Assert (AAA)
- Structure every test with three clear sections:
  1. **Arrange** — set up the test data and context.
  2. **Act** — invoke the code under test.
  3. **Assert** — verify the expected outcome.

### Test Edge Cases
- Always test boundary values (e.g., quality at 0, 50; sellIn at 0, -1).
- Negative paths (what should *not* happen) are as important as the happy path.

### Tests Must Be Independent
- Tests must not depend on execution order.
- Each test must set up its own state and not rely on shared mutable state.

---

## Task Workflow (MANDATORY)

When working on a task from the `/requirements/` folder:

1. **Before starting** — update the `**Status:**` line in the task file to `in-progress`, append a Changelog row, **and update `/requirements/OVERVIEW.md`** to reflect the new status.
2. **When finished** — update `**Status:**` to `implemented`, append a Changelog row, **and update `/requirements/OVERVIEW.md`**.
3. **OVERVIEW.md must always be in sync** — every status change must be reflected in both the task file and `/requirements/OVERVIEW.md` in the same edit. Never update one without the other.
4. **Never skip the Changelog** — every status change must be recorded as a new row (date, new status, short note). The Changelog is append-only; never edit or delete an existing row.
5. **Never self-approve** — only the user can move a task to `ready-for-development` or `done`.
6. **When the user accepts and moves a task to `done`** — create a `SUMMARY.md` in the task folder summarising what was implemented and any problems addressed during development. See the `task-execution` skill for the exact template.

Status transitions owned by an agent:
- `ready-for-development` → `in-progress` (**only when the user explicitly asks to implement**, e.g. "implement", "start work", "build it" — approval alone is NOT a signal to begin)
- `in-progress` → `implemented` (when implementation is complete)
- `implemented` → `in-progress` (if the user rejects the review — note the feedback in the Changelog)
- `implemented` → `done` (when the user accepts — agent creates `SUMMARY.md` at this point)

See the `requirements-engineering` skill for task creation and the approval workflow, and the `task-execution` skill for the implementation workflow, status transitions, and SUMMARY.md template.

---

## Bug Workflow (MANDATORY)

When a bug is reported, track it under the `/bugs/` folder using the same Changelog discipline as tasks.

Status lifecycle: `reported` → `in-progress` → `implemented` → `fixed`

1. **On report** — create a bug file in `/bugs/<ID>-<short-title>/`, status `reported`, Changelog row added.
2. **Before fixing** — update status to `in-progress`, append a Changelog row.
3. **When fix is complete** — update status to `implemented`, document root cause and fix, append a Changelog row. Report to the user and invite review.
4. **Never skip the Changelog** — every status change must be recorded. The Changelog is append-only.
5. **Never self-close** — only the user can move a bug to `fixed`.

Status transitions owned by an agent:
- `reported` → `in-progress` (**only when the user explicitly asks to fix**, e.g. "fix it", "go ahead")
- `in-progress` → `implemented` (when fix is verified)
- `implemented` → `in-progress` (if the user rejects — note the feedback in the Changelog)
- `implemented` → `fixed` (when the user accepts)

See the `bug-workflow` skill for the full workflow, file template, and status reference.

---

## Project-Specific Constraints

- **Do not modify `Item.java`** — this class is considered an immutable dependency (the "goblin" rule from the kata).
- All logic changes go into `GildedRose.java` or new classes/interfaces introduced alongside it.
- Run tests with `gradlew test` (Windows: `gradlew.bat test`) or `./mvnw test` before considering any change done.
