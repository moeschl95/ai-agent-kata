package com.gildedrose;

class ItemUpdaterFactory {
    static final String AGED_BRIE = "Aged Brie";
    static final String SULFURAS = "Sulfuras, Hand of Ragnaros";
    static final String BACKSTAGE_PASS = "Backstage passes to a TAFKAL80ETC concert";
    static final String CONJURED_PREFIX = "Conjured";

    private static final ItemUpdater NORMAL = new NormalItemUpdater();
    private static final ItemUpdater BRIE = new AgedBrieUpdater();
    private static final ItemUpdater LEGENDARY = new SulfurasUpdater();
    private static final ItemUpdater PASS = new BackstagePassUpdater();
    private static final ItemUpdater CONJURED = new ConjuredItemUpdater();

    static ItemUpdater forItem(Item item) {
        if (item.name.equals(AGED_BRIE)) return BRIE;
        if (item.name.equals(SULFURAS)) return LEGENDARY;
        if (item.name.equals(BACKSTAGE_PASS)) return PASS;
        if (item.name.startsWith(CONJURED_PREFIX)) return CONJURED;
        return NORMAL;
    }
}
