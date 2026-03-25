package com.gildedrose.domain.service;

import com.gildedrose.domain.model.Item;

public interface PricingService {
    int priceFor(Item item);
}
