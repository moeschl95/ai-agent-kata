package com.gildedrose.domain.service;

import com.gildedrose.domain.model.Item;

class AgedBrieUpdater implements ItemUpdater {
  @Override
  public void update(final Item item) {
    item.sellIn--;
    int increaseBy = item.sellIn < 0 ? 2 : 1;
    item.quality = Math.min(50, item.quality + increaseBy);
  }
}
