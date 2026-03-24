package com.gildedrose;

class ConjuredItemUpdater implements ItemUpdater {
    @Override
    public void update(Item item) {
        item.sellIn--;
        int degradeBy = item.sellIn < 0 ? 4 : 2;
        item.quality = Math.max(0, item.quality - degradeBy);
    }
}
