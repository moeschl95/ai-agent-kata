package com.gildedrose.domain.service;

import com.gildedrose.domain.model.Item;

import java.util.Map;

/**
 * Default implementation of {@link PricingService} that computes prices based on a
 * configurable base-price map, an expired item discount rate, and a special legendary price.
 */
public class DefaultPricingService implements PricingService {

    private final Map<String, Integer> basePrices;
    private final double expiredDiscountRate;
    private final int legendaryPrice;

    /**
     * Constructs a new pricing service.
     *
     * @param basePrices          a map of item name to base price
     * @param expiredDiscountRate the discount rate applied to expired items (e.g. 0.5 for 50% off)
     * @param legendaryPrice      the fixed price charged for legendary items regardless of their state
     */
    public DefaultPricingService(final Map<String, Integer> basePrices, final double expiredDiscountRate, final int legendaryPrice) {
        this.basePrices = basePrices;
        this.expiredDiscountRate = expiredDiscountRate;
        this.legendaryPrice = legendaryPrice;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Legendary items always return the legendary price. Expired items receive a discount.
     * </p>
     */
    @Override
    public int priceFor(final Item item) {
        if (item.name.equals(ItemUpdaterFactory.SULFURAS)) {
            return legendaryPrice;
        }
        int basePrice = basePrices.get(item.name);
        if (item.sellIn < 0) {
            return (int) Math.round(basePrice * (1 - expiredDiscountRate));
        }
        return basePrice;
    }

    /**
     * Returns the configured legendary item price.
     *
     * @return the legendary price
     */
    public int getLegendaryPrice() {
        return legendaryPrice;
    }

    /**
     * Returns the configured discount rate applied to expired items.
     *
     * @return the expired discount rate
     */
    public double getExpiredDiscountRate() {
        return expiredDiscountRate;
    }
}
