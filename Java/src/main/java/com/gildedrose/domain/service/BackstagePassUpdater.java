package com.gildedrose.domain.service;

import com.gildedrose.domain.model.Item;

class BackstagePassUpdater implements ItemUpdater {
    @Override
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
        if (item.sellIn < 10) {
            item.quality = Math.min(50, item.quality + 2);
            return;
        }
        item.quality = Math.min(50, item.quality + 1);
    }
}
