package com.gildedrose.domain.service;

import com.gildedrose.domain.model.Item;

class SulfurasUpdater implements ItemUpdater {
    @Override
    public void update(final Item item) {
        // Legendary item: quality and sellIn never change
    }
}
