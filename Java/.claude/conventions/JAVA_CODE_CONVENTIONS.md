# Code Conventions

This file defines the mandatory coding conventions for all Java source code in this project.
They apply to every file touched during development — new code and modified code alike.

---

## 1. `final` on All Parameters

Every method and constructor parameter **must** carry the `final` modifier.

```java
// Correct
public void update(final Item item) { ... }
public DefaultPricingService(final Map<String, Integer> basePrices, final double rate, final int price) { ... }

// Wrong — missing final
public void update(Item item) { ... }
```

Applies to: regular parameters, constructor parameters, catch-clause variables, and enhanced-for variables.

---

## 2. Javadoc on Every Public Class and Method

Every `public` class, interface, record, enum, constructor, and method **must** have a Javadoc comment.

```java
/**
 * Manages the Gilded Rose shop inventory and advances item quality by one day.
 */
public class GildedRose { ... }

/**
 * Advances all items by one day, applying each item's quality-update rules.
 */
public void updateQuality() { ... }
```

- Use `@param` for every parameter.
- Use `@return` when the return type is not `void`.
- Use `@throws` for checked and documented unchecked exceptions.
- Package-private and private members do **not** require Javadoc.

---

## 3. Early Exits Over `if-else` Chains

Prefer **guard clauses** (early returns) over nested `if-else` blocks wherever possible.
This keeps the happy-path at the lowest indentation level and removes unnecessary nesting.

```java
// Correct — guard clauses
public void update(final Item item) {
    item.sellIn--;
    if (item.sellIn < 0) {
        item.quality = 0;
        return;
    }
    if (item.sellIn < 5) {
        item.quality = Math.min(50, item.quality + 3);
        return;
    }
    item.quality = Math.min(50, item.quality + 1);
}

// Wrong — cascading else chain
public void update(final Item item) {
    item.sellIn--;
    if (item.sellIn < 0) {
        item.quality = 0;
    } else if (item.sellIn < 5) {
        item.quality = Math.min(50, item.quality + 3);
    } else {
        item.quality = Math.min(50, item.quality + 1);
    }
}
```

---

## 4. No Plain Arrays — Use `List`, `Set`, or `Map`

Plain Java arrays (`Item[]`, `String[]`) **must not** be used as field types, method parameters, or return types in production code.
Use the appropriate `java.util` collection instead.

```java
// Correct
public List<Item> items;
public GildedRose(final List<Item> items) { ... }
public List<ItemDto> projectAll(final List<Item> items, final int days) { ... }

// Wrong
public Item[] items;
public GildedRose(Item[] items) { ... }
public List<ItemDto> projectAll(Item[] items, int days) { ... }
```

Exceptions:
- The `main(String[] args)` entry-point signature is mandated by the JVM and is exempt.
- Code that must interoperate with a fixed external API (e.g. a library that returns `T[]`) may keep the array at the boundary but should convert to a collection immediately inside the method.
