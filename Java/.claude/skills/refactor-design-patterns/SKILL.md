---
name: refactor-design-patterns
description: >
  Analyze Java code for code smells and refactor using appropriate Gang-of-Four and SOLID design patterns.
  Use this skill whenever a user asks to refactor code, apply design patterns, improve code structure, fix
  code smells, make code more maintainable or extensible, or asks about Strategy, Factory, Template Method,
  Chain of Responsibility, or other OOP patterns in this project. Also use it when the user says the code is
  messy, hard to extend, has too many if/else chains, or wants to add a new item type cleanly. Even if they
  don't say "design pattern" explicitly — if they want cleaner, more structured code, use this skill.
---

# Refactor with Design Patterns

This skill guides analysis of the Gilded Rose codebase for code smells, then applies appropriate design patterns to produce clean, extensible Java code.

## Phase 1: Code Analysis

Before refactoring, read the source files and identify smells. Start with:
```
src/main/java/com/gildedrose/GildedRose.java
src/main/java/com/gildedrose/Item.java
src/test/java/com/gildedrose/GildedRoseTest.java
```

### Code Smell Checklist

Scan for these specific smells in the Gilded Rose code:

| Smell | Symptom | Typical Location |
|-------|---------|-----------------|
| **Long Method** | `updateQuality()` > 20 lines with nested conditionals | `GildedRose.java` |
| **Magic Strings** | Hardcoded item names like `"Aged Brie"`, `"Sulfuras..."` | `updateQuality()` comparisons |
| **Deeply Nested Conditionals** | 4+ levels of `if/else` | `updateQuality()` body |
| **Feature Envy** | All logic in one class instead of near the data | `GildedRose` knows too much |
| **Switch/If-Chain on Type** | Multiple `if (name.equals(...))` discriminating behavior | `updateQuality()` |
| **Open/Closed Violation** | Adding a new item type requires editing `updateQuality()` | Architecture-level |

Report each smell found with: the smell name, a one-line description of where it appears, and its severity (low/medium/high).

---

## Phase 2: Pattern Recommendation

After identifying smells, map them to patterns. Use the table below as a guide, but apply judgement — not every smell needs every pattern.

| Smell(s) Found | Recommended Pattern | Reference File |
|----------------|--------------------|-----------------------------|
| If-chain on item type | **Strategy** | `references/strategy.md` |
| Need to create the right strategy | **Factory Method** | `references/factory.md` |
| Shared update logic (sellIn decrement, quality bounds) | **Template Method** | `references/template-method.md` |
| Complex chained conditions, open-ended extensibility | **Chain of Responsibility** | `references/chain-of-responsibility.md` |

Read only the reference files relevant to the patterns you are recommending. Each reference file contains:
- Pattern intent and motivation
- Java implementation skeleton tailored to the Gilded Rose
- Step-by-step migration instructions
- Pitfalls to avoid

Present the recommended pattern(s) to the user with a brief rationale before writing code.

---

## Phase 3: Refactoring

### Guiding Constraints

- **Never modify `Item.java`** — it is owned by a goblin (kata rule). All behavior goes in new classes or in `GildedRose.java`.
- **Keep all existing tests green** — run `gradlew test` before and after. Do not change test behavior, only add coverage.
- **Preserve public API** — `GildedRose(Item[])` constructor and `updateQuality()` method signatures must not change.
- **Introduce constants** for magic strings (`"Aged Brie"`, `"Sulfuras, Hand of Ragnaros"`, `"Backstage passes to a TAFKAL80ETC concert"`).

### Refactoring Sequence

Follow this order to keep the build green at each step:

1. **Extract constants** — Replace magic strings with named constants. Tests still pass.
2. **Write characterization tests** (if test coverage is thin) — Capture current behavior before changing structure.
3. **Apply the recommended pattern** — Implement new classes/interfaces alongside existing code.
4. **Delegate from `updateQuality()`** — Replace inline logic with calls to new pattern participants, one item type at a time.
5. **Remove dead code** — Once all item types are delegated, clean up the original if/else body.
6. **Run tests** — `gradlew test` must pass. Fix any failures before continuing.

### Quality Gates

After each step:
- Build compiles without warnings.
- All pre-existing tests pass.
- No new magic strings introduced.
- Cyclomatic complexity of `updateQuality()` is 1 (just a loop with delegation).

---

## Phase 4: Extensibility Demonstration

After the refactor, show how adding the new **"Conjured"** item type (degrades in quality twice as fast as normal items) now requires:
- Adding a single new class or enum case.
- Zero changes to `GildedRose.java`.

This demonstrates the Open/Closed Principle has been achieved.

---

## Communication Style

- Present the smell analysis as a concise table or bullet list.
- Explain *why* a pattern fits before showing any code.
- Show diffs or before/after snippets rather than full-file rewrites where possible.
- If the user seems unfamiliar with a pattern, give a one-sentence plain-language summary before the technical detail.
