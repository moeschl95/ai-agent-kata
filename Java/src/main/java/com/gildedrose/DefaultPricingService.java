package com.gildedrose;

import java.util.Map;

class DefaultPricingService implements PricingService {

    private final Map<String, Integer> basePrices;
    private final double expiredDiscountRate;
    private final int legendaryPrice;

    DefaultPricingService(Map<String, Integer> basePrices, double expiredDiscountRate, int legendaryPrice) {
        this.basePrices = basePrices;
        this.expiredDiscountRate = expiredDiscountRate;
        this.legendaryPrice = legendaryPrice;
    }

    @Override
    public int priceFor(Item item) {
        if (item.name.equals(ItemUpdaterFactory.SULFURAS)) {
            return legendaryPrice;
        }
        int basePrice = basePrices.get(item.name);
        if (item.sellIn < 0) {
            return (int) Math.round(basePrice * (1 - expiredDiscountRate));
        }
        return basePrice;
    }
}
