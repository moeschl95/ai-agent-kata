package com.gildedrose.domain.service;

import com.gildedrose.domain.model.Item;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PricingServiceTest {

    private static final int LEGENDARY_PRICE = 999;
    private static final double DISCOUNT_RATE = 0.5;

    private final PricingService pricingService = new DefaultPricingService(
            Map.of("Normal Item", 10, ItemUpdaterFactory.SULFURAS, 80),
            DISCOUNT_RATE,
            LEGENDARY_PRICE
    );

    @Test
    void should_returnBasePrice_when_itemIsInDate() {
        // Arrange
        Item item = new Item("Normal Item", 5, 10);

        // Act
        int price = pricingService.priceFor(item);

        // Assert
        assertEquals(10, price);
    }

    @Test
    void should_returnDiscountedPrice_when_itemIsExpired() {
        // Arrange
        Item item = new Item("Normal Item", -1, 5);

        // Act
        int price = pricingService.priceFor(item);

        // Assert
        assertEquals(5, price); // 10 * (1 - 0.5) = 5
    }

    @Test
    void should_returnBasePriceWithoutDiscount_when_sellInIsZero() {
        // Arrange
        Item item = new Item("Normal Item", 0, 5);

        // Act
        int price = pricingService.priceFor(item);

        // Assert
        assertEquals(10, price);
    }

    @Test
    void should_returnLegendaryPrice_when_itemIsSulfuras() {
        // Arrange
        Item item = new Item(ItemUpdaterFactory.SULFURAS, 0, 80);

        // Act
        int price = pricingService.priceFor(item);

        // Assert
        assertEquals(LEGENDARY_PRICE, price);
    }

    @Test
    void should_returnLegendaryPrice_when_sulfurasHasNegativeSellIn() {
        // Arrange
        Item item = new Item(ItemUpdaterFactory.SULFURAS, -5, 80);

        // Act
        int price = pricingService.priceFor(item);

        // Assert
        assertEquals(LEGENDARY_PRICE, price);
    }

    @Test
    void should_returnLegendaryPrice_when_sulfurasRegardlessOfBasePrice() {
        // Arrange
        PricingService service = new DefaultPricingService(
                Map.of(ItemUpdaterFactory.SULFURAS, 1),
                DISCOUNT_RATE,
                LEGENDARY_PRICE
        );
        Item item = new Item(ItemUpdaterFactory.SULFURAS, 10, 80);

        // Act
        int price = service.priceFor(item);

        // Assert
        assertEquals(LEGENDARY_PRICE, price);
    }
}
