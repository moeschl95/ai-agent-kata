package com.gildedrose;

class AgedBrieUpdater implements ItemUpdater {
    @Override
    public void update(Item item) {
        item.sellIn--;
        int increaseBy = item.sellIn < 0 ? 2 : 1;
        item.quality = Math.min(50, item.quality + increaseBy);
    }
}
