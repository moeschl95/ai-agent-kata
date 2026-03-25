package com.gildedrose.domain.service;

import com.gildedrose.domain.model.Item;

public class ItemUpdaterFactory {
    public static final String AGED_BRIE = "Aged Brie";
    public static final String SULFURAS = "Sulfuras, Hand of Ragnaros";
    public static final String BACKSTAGE_PASS = "Backstage passes to a TAFKAL80ETC concert";
    public static final String CONJURED_PREFIX = "Conjured";

    private static final ItemUpdater NORMAL = new NormalItemUpdater();
    private static final ItemUpdater BRIE = new AgedBrieUpdater();
    private static final ItemUpdater LEGENDARY = new SulfurasUpdater();
    private static final ItemUpdater PASS = new BackstagePassUpdater();
    private static final ItemUpdater CONJURED = new ConjuredItemUpdater();

    public static ItemUpdater forItem(final Item item) {
        // Check for dual-type conjured items first
        if (item.name.startsWith(CONJURED_PREFIX + " ")) {
            String baseName = item.name.substring((CONJURED_PREFIX + " ").length());
            ItemUpdater baseUpdater = forBaseName(baseName);
            
            // If it's a special type, wrap it with ConjuredDecorator
            if (baseUpdater != CONJURED && baseUpdater != NORMAL) {
                return new ConjuredDecorator(baseUpdater);
            }
        }

        if (item.name.equals(AGED_BRIE)) return BRIE;
        if (item.name.equals(SULFURAS)) return LEGENDARY;
        if (item.name.equals(BACKSTAGE_PASS)) return PASS;
        if (item.name.startsWith(CONJURED_PREFIX)) return CONJURED;
        return NORMAL;
    }

    private static ItemUpdater forBaseName(final String baseName) {
        if (baseName.equals(AGED_BRIE)) return BRIE;
        if (baseName.equals(BACKSTAGE_PASS)) return PASS;
        if (baseName.equals(SULFURAS)) return LEGENDARY;
        if (baseName.startsWith(CONJURED_PREFIX)) return CONJURED;
        return NORMAL;
    }
}
