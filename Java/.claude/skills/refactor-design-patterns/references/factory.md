# Factory Method / Static Factory

## Intent
Decouple object creation from the code that uses objects. A factory centralises the decision of *which* concrete type to instantiate, keeping that logic in one place.

## Why It Fits Here
Once you have multiple `ItemUpdater` strategies, `GildedRose` must pick the right one. Without a factory, the selection logic lives in `updateQuality()` — the same class you just cleaned up. A factory isolates that concern.

## Implementation Option A — Static Factory Method

Simple and sufficient when strategies are stateless singletons.

```java
class ItemUpdaterFactory {
    // Constants prevent magic strings spreading across the codebase
    static final String AGED_BRIE      = "Aged Brie";
    static final String SULFURAS       = "Sulfuras, Hand of Ragnaros";
    static final String BACKSTAGE_PASS = "Backstage passes to a TAFKAL80ETC concert";
    static final String CONJURED       = "Conjured Mana Cake";

    // Pre-built instances (strategies are stateless, safe to share)
    private static final ItemUpdater NORMAL    = new NormalItemUpdater();
    private static final ItemUpdater BRIE      = new AgedBrieUpdater();
    private static final ItemUpdater LEGENDARY = new SulfurasUpdater();
    private static final ItemUpdater PASS      = new BackstagePassUpdater();
    private static final ItemUpdater CONJURED_UPDATER = new ConjuredItemUpdater();

    public static ItemUpdater forItem(Item item) {
        switch (item.name) {
            case AGED_BRIE:      return BRIE;
            case SULFURAS:       return LEGENDARY;
            case BACKSTAGE_PASS: return PASS;
            case CONJURED:       return CONJURED_UPDATER;
            default:             return NORMAL;
        }
    }
}
```

## Implementation Option B — Map-Based Factory

Preferred when the set of item types needs to be registered dynamically.

```java
class ItemUpdaterFactory {
    private final Map<String, ItemUpdater> registry = new HashMap<>();

    public ItemUpdaterFactory() {
        registry.put("Aged Brie",      new AgedBrieUpdater());
        registry.put("Sulfuras, Hand of Ragnaros", new SulfurasUpdater());
        registry.put("Backstage passes to a TAFKAL80ETC concert", new BackstagePassUpdater());
    }

    public ItemUpdater forItem(Item item) {
        return registry.getOrDefault(item.name, new NormalItemUpdater());
    }
}
```

## Integration with GildedRose

```java
class GildedRose {
    Item[] items;

    public void updateQuality() {
        for (Item item : items) {
            ItemUpdaterFactory.forItem(item).update(item);
        }
    }
}
```

## Pitfalls
- Place string constants in the factory (or a dedicated `ItemNames` class), not scattered across updater classes.
- If using the map-based approach, the `NormalItemUpdater` default must handle `sellIn < 0` correctly since it applies to all unnamed items.
