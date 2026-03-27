package com.gildedrose.domain.service;

import com.gildedrose.domain.model.Item;

class ConjuredDecorator implements ItemUpdater {
  private final ItemUpdater baseUpdater;

  ConjuredDecorator(final ItemUpdater baseUpdater) {
    this.baseUpdater = baseUpdater;
  }

  @Override
  public void update(final Item item) {
    int qualityBefore = item.quality;
    baseUpdater.update(item);
    int qualityDelta = item.quality - qualityBefore;

    // Apply the same delta again to double the effect
    int newQuality = item.quality + qualityDelta;
    item.quality = Math.max(0, Math.min(50, newQuality));
  }
}
