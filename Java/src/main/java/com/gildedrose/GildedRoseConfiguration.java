package com.gildedrose;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Spring configuration class that defines beans for the Gilded Rose shop.
 * <p>
 * Reads pricing configuration from application properties and creates the
 * {@link PricingService} and {@link GildedRose} beans.
 * </p>
 */
@Configuration
public class GildedRoseConfiguration {

    @Value("${gilded-rose.legendary-price:999}")
    private int legendaryPrice;

    @Value("${gilded-rose.expired-discount-rate:0.5}")
    private double expiredDiscountRate;

    @Value("${gilded-rose.base-prices.aged-brie:50}")
    private int agedBriePrice;

    @Value("${gilded-rose.base-prices.backstage-pass:80}")
    private int backstagePassPrice;

    @Value("${gilded-rose.base-prices.conjured-mana-cake:25}")
    private int conjuredManaCakePrice;

    @Value("${gilded-rose.base-prices.sulfuras:999}")
    private int sulfurasPrice;

    @Value("${gilded-rose.base-prices.normal-item:20}")
    private int normalItemPrice;

    /**
     * Creates and configures a {@link PricingService} bean using application properties.
     *
     * @return the configured {@link DefaultPricingService}
     */
    @Bean
    public PricingService pricingService() {
        final Map<String, Integer> basePrices = new HashMap<>();
        basePrices.put("Aged Brie", agedBriePrice);
        basePrices.put("Backstage pass to a TAFKAL80ETC concert", backstagePassPrice);
        basePrices.put("Conjured Mana Cake", conjuredManaCakePrice);
        basePrices.put("Sulfuras, Hand of Ragnaros", sulfurasPrice);
        basePrices.put("Normal item", normalItemPrice);

        return new DefaultPricingService(basePrices, expiredDiscountRate, legendaryPrice);
    }

    /**
     * Creates a {@link GildedRose} bean pre-populated with the default set of shop items.
     *
     * @return a {@link GildedRose} instance containing the initial inventory
     */
    @Bean
    public GildedRose gildedRose() {
        return new GildedRose(List.of(
                new Item("+5 Dexterity Vest", 10, 20),
                new Item("Aged Brie", 2, 0),
                new Item("Elixir of the Mongoose", 5, 7),
                new Item("Sulfuras, Hand of Ragnaros", 0, 80),
                new Item("Backstage passes to a TAFKAL80ETC concert", 15, 20),
                new Item("Conjured Mana Cake", 3, 6)
        ));
    }
}
