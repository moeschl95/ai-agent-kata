package com.gildedrose.domain.service;

import com.gildedrose.domain.model.Item;

class NormalItemUpdater implements ItemUpdater {
    @Override
    public void update(final Item item) {
        item.sellIn--;
        int degradeBy = item.sellIn < 0 ? 2 : 1;
        item.quality = Math.max(0, item.quality - degradeBy);
    }
}
