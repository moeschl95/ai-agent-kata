---
name: getting-started
description: "Onboarding guide for the Gilded Rose Kata Java project. Explains how to run tests, build the project, understand the updateQuality logic, and get oriented in the codebase. Use this skill whenever a developer asks how to get started, how to run tests, what the project does, how the code works, what updateQuality does, or any other orientation/onboarding question about this Gilded Rose Kata project — even if they don't explicitly say 'getting started'."
---

# Getting Started with the Gilded Rose Kata

This skill helps new developers quickly understand and work with the Gilded Rose Kata project in Java.

## Project Overview

The Gilded Rose is a classic refactoring kata. You inherit a working (but messy) inventory system for a fantasy shop. The goal of the kata is to refactor the existing code and then add a new feature ("Conjured" items) — all without breaking existing behavior.

The shop has an array of `Item` objects. Each item has a `name`, a `sellIn` value (days until sell-by date), and a `quality` value. Every day, the system calls `updateQuality()` to adjust these values.

## Project Structure

```
src/
  main/java/com/gildedrose/
    GildedRose.java      ← Core logic: the updateQuality() method lives here
    Item.java             ← Simple data class (name, sellIn, quality)
  test/java/com/gildedrose/
    GildedRoseTest.java   ← Unit tests (JUnit 5) — start writing tests here
    TexttestFixture.java  ← Console output for manual/approval testing
```

**Important constraint:** The `Item` class must not be modified — it's considered "owned by a goblin" in the kata's lore. All changes go into `GildedRose.java`.

## How to Run Tests

The project supports both **Maven** and **Gradle**. Use whichever you prefer:

### Gradle (recommended — wrapper included)
```bash
# Run unit tests
./gradlew test

# Run the TextTest fixture (prints item state over 30 days)
./gradlew -q texttest

# Run TextTest for a specific number of days (e.g. 10)
./gradlew -q texttest --args 10
```

On Windows, use `gradlew.bat` instead of `./gradlew`.

### Maven (wrapper included)
```bash
# Run unit tests
./mvnw clean test

# Compile only
./mvnw clean compile
```

On Windows, use `mvnw.cmd` instead of `./mvnw`.

### Using the IDE
In VS Code or IntelliJ, you can run `GildedRoseTest` directly via the test runner UI — look for the green play button next to the test class or individual test methods.

## How updateQuality() Works

The `updateQuality()` method in `GildedRose.java` is intentionally convoluted — that's the whole point of the kata. Here's what it actually does, broken down by item type:

### Normal Items (e.g. "+5 Dexterity Vest", "Elixir of the Mongoose")
- Quality decreases by **1** each day
- After the sell-by date (`sellIn < 0`), quality decreases by **2** per day
- Quality never drops below **0**

### Aged Brie
- Quality **increases** by 1 each day (it gets better with age)
- After the sell-by date, quality increases by **2** per day
- Quality never exceeds **50**

### Sulfuras, Hand of Ragnaros (legendary item)
- Quality is always **80** and never changes
- `sellIn` never decreases — it doesn't need to be sold

### Backstage Passes to a TAFKAL80ETC Concert
- Quality increases by **1** when there are more than 10 days left
- Quality increases by **2** when there are 10 days or fewer left
- Quality increases by **3** when there are 5 days or fewer left
- Quality drops to **0** after the concert (`sellIn < 0`)
- Quality never exceeds **50**

### Conjured Items (not yet implemented!)
- **This is the feature you need to add.** Conjured items should degrade in quality twice as fast as normal items
- The current code does NOT handle "Conjured" items specially — they're treated as normal items

## The Kata Workflow

A good approach to tackling this kata:

1. **Write characterization tests first.** The existing test in `GildedRoseTest.java` is just a placeholder (`assertEquals("fixme", ...)`). Before changing any code, write tests that capture the current behavior of `updateQuality()` for all item types. This safety net ensures your refactoring doesn't break anything.

2. **Refactor the updateQuality method.** The nested if-else structure is hard to read and extend. Common refactoring approaches include:
   - Extract methods per item type
   - Use polymorphism (strategy pattern)
   - Use guard clauses to flatten the nesting

3. **Add the "Conjured" feature.** Once the code is clean and well-tested, adding Conjured items (degrade twice as fast) should be straightforward.

## Quick Reference: Item Rules Summary

| Item | sellIn changes? | Quality behavior | Quality bounds |
|------|-----------------|------------------|----------------|
| Normal | Yes, -1/day | -1/day, -2 after expiry | 0–50 |
| Aged Brie | Yes, -1/day | +1/day, +2 after expiry | 0–50 |
| Sulfuras | No | Never changes | Always 80 |
| Backstage Passes | Yes, -1/day | +1 (>10d), +2 (≤10d), +3 (≤5d), 0 after | 0–50 |
| Conjured (TODO) | Yes, -1/day | -2/day, -4 after expiry | 0–50 |

## Common Pitfalls

- **Don't modify `Item.java`** — the kata rules forbid it (you can add the `Conjured` logic in `GildedRose.java`)
- **The placeholder test fails on purpose** — replace `"fixme"` with actual assertions before running tests
- **Sulfuras quality is 80**, not 50 — it's exempt from the normal max quality cap
- **"Conjured" matching** — the original code uses string comparison for item names, so make sure your Conjured logic matches the exact name format
