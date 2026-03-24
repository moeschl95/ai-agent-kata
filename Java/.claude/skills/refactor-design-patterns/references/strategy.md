# Strategy Pattern

## Intent
Define a family of algorithms, encapsulate each one, and make them interchangeable. Strategy lets the algorithm vary independently from clients that use it.

## Why It Fits Here
`updateQuality()` contains a large if/else chain that picks different update logic based on item name. Each branch *is* a strategy. Extracting them removes the branching and makes adding new item types a matter of creating a new class rather than editing existing code.

## Core Structure

```java
// Strategy interface
interface ItemUpdater {
    void update(Item item);
}

// One concrete strategy per item category
class NormalItemUpdater implements ItemUpdater {
    public void update(Item item) {
        item.sellIn--;
        int degradeBy = item.sellIn < 0 ? 2 : 1;
        item.quality = Math.max(0, item.quality - degradeBy);
    }
}

class AgedBrieUpdater implements ItemUpdater {
    public void update(Item item) {
        item.sellIn--;
        int increaseBy = item.sellIn < 0 ? 2 : 1;
        item.quality = Math.min(50, item.quality + increaseBy);
    }
}

class SulfurasUpdater implements ItemUpdater {
    public void update(Item item) {
        // Legendary item: never changes
    }
}

class BackstagePassUpdater implements ItemUpdater {
    public void update(Item item) {
        item.sellIn--;
        if (item.sellIn < 0) {
            item.quality = 0;
        } else if (item.sellIn < 5) {
            item.quality = Math.min(50, item.quality + 3);
        } else if (item.sellIn < 10) {
            item.quality = Math.min(50, item.quality + 2);
        } else {
            item.quality = Math.min(50, item.quality + 1);
        }
    }
}
```

## Migration Steps

1. Create the `ItemUpdater` interface.
2. Add one concrete class per item category (Normal, Aged Brie, Sulfuras, Backstage Pass, Conjured).
3. Wire them into `GildedRose` via a `Map<String, ItemUpdater>` (or a factory — see `factory.md`).
4. Replace `updateQuality()` loop body with a single delegation call:
   ```java
   for (Item item : items) {
       resolveUpdater(item).update(item);
   }
   ```
5. Delete the original if/else block.

## Pitfalls
- Don't pass the index `i` to the updater — pass the `Item` object directly.
- Keep quality bounds (`0..50`) inside each strategy, or extract them to a shared utility — do not re-check them in `GildedRose`.
- Sulfuras quality is fixed at 80 — the updater leaves it alone entirely.
