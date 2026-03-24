# Chain of Responsibility Pattern

## Intent
Pass a request along a chain of handlers. Each handler decides either to process the request or to pass it to the next handler in the chain.

## Why It Might Fit Here
Chain of Responsibility is an *alternative* to the Strategy + Factory approach when:
- You want handlers to decide for themselves whether they apply, rather than a central factory deciding.
- You anticipate overlapping rules (e.g., an item that is both "Conjured" *and* "Backstage Pass").
- You want to register handlers at runtime without changing a factory.

For the standard Gilded Rose kata, Strategy + Factory is usually simpler. Prefer Chain of Responsibility only when the above points apply to your situation.

## Implementation

```java
abstract class UpdateHandler {
    private UpdateHandler next;

    // Builder-style chaining
    public UpdateHandler setNext(UpdateHandler next) {
        this.next = next;
        return next;
    }

    // Each handler either handles the item or passes it along
    public void handle(Item item) {
        if (canHandle(item)) {
            doUpdate(item);
        } else if (next != null) {
            next.handle(item);
        }
    }

    protected abstract boolean canHandle(Item item);
    protected abstract void doUpdate(Item item);
}
```

### Concrete Handlers

```java
class SulfurasHandler extends UpdateHandler {
    @Override
    protected boolean canHandle(Item item) {
        return "Sulfuras, Hand of Ragnaros".equals(item.name);
    }
    @Override
    protected void doUpdate(Item item) {
        // Legendary item — no change ever
    }
}

class AgedBrieHandler extends UpdateHandler {
    @Override
    protected boolean canHandle(Item item) {
        return "Aged Brie".equals(item.name);
    }
    @Override
    protected void doUpdate(Item item) {
        item.sellIn--;
        item.quality = Math.min(50, item.quality + (item.sellIn < 0 ? 2 : 1));
    }
}

class BackstagePassHandler extends UpdateHandler {
    @Override
    protected boolean canHandle(Item item) {
        return item.name.startsWith("Backstage passes");
    }
    @Override
    protected void doUpdate(Item item) {
        item.sellIn--;
        if (item.sellIn < 0)      { item.quality = 0; return; }
        if (item.sellIn < 5)      { item.quality = Math.min(50, item.quality + 3); return; }
        if (item.sellIn < 10)     { item.quality = Math.min(50, item.quality + 2); return; }
        item.quality = Math.min(50, item.quality + 1);
    }
}

class NormalItemHandler extends UpdateHandler {
    @Override
    protected boolean canHandle(Item item) {
        return true; // Fallback — always handles
    }
    @Override
    protected void doUpdate(Item item) {
        item.sellIn--;
        item.quality = Math.max(0, item.quality - (item.sellIn < 0 ? 2 : 1));
    }
}
```

### Wiring the Chain in GildedRose

```java
class GildedRose {
    Item[] items;
    private final UpdateHandler chain;

    public GildedRose(Item[] items) {
        this.items = items;
        // Build chain: priority order matters — most specific first, fallback last
        UpdateHandler sulfuras = new SulfurasHandler();
        sulfuras
            .setNext(new AgedBrieHandler())
            .setNext(new BackstagePassHandler())
            .setNext(new NormalItemHandler()); // catch-all must be at the end
        this.chain = sulfuras;
    }

    public void updateQuality() {
        for (Item item : items) {
            chain.handle(item);
        }
    }
}
```

## Comparison: Strategy vs Chain of Responsibility

| Concern | Strategy + Factory | Chain of Responsibility |
|---------|--------------------|------------------------|
| Handler selection | Centralised in factory | Each handler self-selects |
| Adding a new item type | Add class + register in factory | Add class + insert in chain |
| Overlapping rules | Requires careful factory logic | Natural — multiple handlers can fire |
| Code simplicity | Simpler for fixed type sets | More flexible, slightly more boilerplate |

## Pitfalls
- Order matters — always put the catch-all (`NormalItemHandler`) last.
- If you need multiple handlers to fire for one item (e.g., logging + updating), change `handle()` to always call `next` after `doUpdate()`.
- Avoid building the chain in a static initializer if handlers have mutable state — use a factory or constructor injection instead.
