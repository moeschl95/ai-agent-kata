---
name: test-and-coverage
description: "Run tests, verify they pass, identify coverage gaps, and produce a clear coverage report for the Gilded Rose Kata Java project. Use this skill whenever a user asks to run tests, check if tests pass, find what's not tested, measure coverage, identify missing tests, find coverage gaps, see which branches or behaviors are untested, or wants a report of test quality — even if they don't use the words 'coverage' or 'JaCoCo'. Also use it when the user asks 'does the code work?', 'what should I test next?', 'what's missing from the tests?', or anything about the health or completeness of the test suite."
---

# Test & Coverage Reporter

This skill runs the project's tests, enables code coverage measurement, and produces a structured report
of what is and isn't covered — so the developer knows exactly where to write tests next.

---

## Step 1 — Run existing tests

Run the tests and capture whether they pass or fail.

```bash
# Gradle (preferred — wrapper included)
gradlew.bat test          # Windows
./gradlew test            # Unix/Mac

# Maven alternative
mvnw.cmd clean test       # Windows
./mvnw clean test         # Unix/Mac
```

Report clearly: how many tests ran, how many passed, how many failed. If tests fail, show the failure
message and the assertion that failed. The existing placeholder test in `GildedRoseTest` asserts
`assertEquals("fixme", ...)` and is **expected to fail** — note this explicitly so the developer
isn't alarmed.

---

## Step 2 — Enable JaCoCo code coverage

Check whether `build.gradle` already has the JaCoCo plugin. Look for `id 'jacoco'` or `apply plugin: 'jacoco'`.

**If JaCoCo is not present**, add it to `build.gradle`:

```groovy
plugins {
    id 'java'
    id 'jacoco'          // ← add this line
}

// Add this block anywhere after the plugins block:
jacocoTestReport {
    reports {
        xml.required = true
        html.required = true
    }
}

test {
    useJUnitPlatform()
    finalizedBy jacocoTestReport   // auto-generate report after every test run
}
```

Explain to the developer what you changed and why, so they understand what JaCoCo is.

---

## Step 3 — Run tests with coverage

```bash
gradlew.bat test jacocoTestReport    # Windows
./gradlew test jacocoTestReport      # Unix/Mac
```

The HTML report is written to:   `build/reports/jacoco/test/html/index.html`
The XML report (machine-readable): `build/reports/jacoco/test/jacocoTestReport.xml`

---

## Step 4 — Analyze the coverage report

Read the XML report at `build/reports/jacoco/test/jacocoTestReport.xml`.

For each class (focus on `GildedRose` — that's where all the logic lives), extract:

| Metric        | What it means                                          |
|---------------|--------------------------------------------------------|
| LINE          | Which lines of source code were executed               |
| BRANCH        | Which if/else branches were taken (most important!)    |
| METHOD        | Which methods were called at all                       |

Pay particular attention to **BRANCH** coverage in `GildedRose.updateQuality()`. The method is a deep
nest of conditionals — every untested branch corresponds to a real business rule that could regress
silently.

---

## Step 5 — Map coverage gaps to business rules

Don't just report line numbers. Translate each missed branch or line into the **business rule** it
represents, using this reference for the Gilded Rose rules:

| Rule                                               | Key branch(es) in updateQuality()                        |
|----------------------------------------------------|-----------------------------------------------------------|
| Normal item: quality decreases by 1 each day       | `!name.equals("Aged Brie") && !name.equals("Backstage…")`, `quality > 0`, `!name.equals("Sulfuras…")` |
| Normal item: quality degrades 2x after sell-by     | `sellIn < 0`, not Aged Brie, not Backstage, not Sulfuras |
| Aged Brie: quality increases each day              | `name.equals("Aged Brie")` path                          |
| Aged Brie: quality increases 2x after sell-by      | `sellIn < 0` + Aged Brie path                            |
| Sulfuras: never changes quality or sellIn          | `name.equals("Sulfuras…")` guards                        |
| Backstage pass: +1 quality when >10 days left      | Backstage branch, `sellIn >= 11`                         |
| Backstage pass: +2 quality when ≤10 days left      | `sellIn < 11`                                            |
| Backstage pass: +3 quality when ≤5 days left       | `sellIn < 6`                                             |
| Backstage pass: quality → 0 after concert          | `sellIn < 0` + Backstage path                            |
| Quality never exceeds 50                           | `quality < 50` guards                                    |
| Quality never drops below 0                        | `quality > 0` guards                                     |
| Conjured items (not yet implemented)               | No branch exists — purely a gap in production code       |

---

## Step 6 — Produce the coverage report

Output a structured report in this exact format:

---

### Test Results
- **X tests ran** — Y passed, Z failed
- [List any failing tests with the assertion message]
- Note: The default placeholder test (`foo()`) is expected to fail — it's a starting point, not a real test.

### Coverage Summary
| Metric   | Covered | Total | % |
|----------|---------|-------|---|
| Lines    | …       | …     | … |
| Branches | …       | …     | … |
| Methods  | …       | …     | … |

### Coverage Gaps (what's not tested)

For each untested or partially-tested rule, report:

**[Item type / rule]**
- What the rule does (one sentence)
- Which branch in `updateQuality()` is uncovered (approximate line range)
- Suggested test: a short JUnit 5 snippet showing how to test it

### What to Test Next (priority order)

Rank the gaps by risk: branches that are never taken at all are higher priority than branches that
are sometimes taken. Suggest writing tests in this order, with a brief reason for each.

---

## Tips for writing good tests

When the developer is ready to write tests, guide them toward this pattern:

```java
@Test
void normalItemDegradesByOneEachDay() {
    Item[] items = new Item[]{ new Item("+5 Dexterity Vest", 10, 20) };
    GildedRose app = new GildedRose(items);
    app.updateQuality();
    assertEquals(9, app.items[0].sellIn);
    assertEquals(19, app.items[0].quality);
}
```

Each test should:
- Have a descriptive name that reads like a business rule
- Set up exactly one scenario (one item type, one boundary condition)
- Assert both `sellIn` and `quality` where relevant
- Use `@ParameterizedTest` with `@CsvSource` for boundary values (e.g., quality = 0, 1, 49, 50)

---

## Re-running after new tests are written

After the developer adds tests, re-run `gradlew.bat test jacocoTestReport` and repeat the analysis.
Show the delta: how many new branches are now covered, which gaps remain. Keep iterating until
branch coverage on `GildedRose` is ≥ 90% — that's a reasonable bar before refactoring safely.
