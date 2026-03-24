package com.gildedrose;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GildedRoseTest {

    // --- Conjured items ---

    @Test
    void should_decreaseQualityByTwo_when_conjuredItemHasPositiveSellIn() {
        // Arrange
        Item[] items = new Item[] { new Item("Conjured Mana Cake", 5, 10) };
        GildedRose app = new GildedRose(items);
        // Act
        app.updateQuality();
        // Assert
        assertEquals(8, app.items[0].quality);
    }

    @Test
    void should_decreaseSellInByOne_when_conjuredItemUpdated() {
        // Arrange
        Item[] items = new Item[] { new Item("Conjured Mana Cake", 5, 10) };
        GildedRose app = new GildedRose(items);
        // Act
        app.updateQuality();
        // Assert
        assertEquals(4, app.items[0].sellIn);
    }

    @Test
    void should_decreaseQualityByFour_when_conjuredItemSellInIsExpired() {
        // Arrange
        Item[] items = new Item[] { new Item("Conjured Mana Cake", 0, 10) };
        GildedRose app = new GildedRose(items);
        // Act
        app.updateQuality();
        // Assert
        assertEquals(6, app.items[0].quality);
    }

    @Test
    void should_notDecreaseBelowZero_when_conjuredItemQualityIsOne() {
        // Arrange
        Item[] items = new Item[] { new Item("Conjured Mana Cake", 5, 1) };
        GildedRose app = new GildedRose(items);
        // Act
        app.updateQuality();
        // Assert
        assertEquals(0, app.items[0].quality);
    }

    @Test
    void should_notDecreaseBelowZero_when_conjuredItemQualityIsZeroAndSellInExpired() {
        // Arrange
        Item[] items = new Item[] { new Item("Conjured Mana Cake", -1, 0) };
        GildedRose app = new GildedRose(items);
        // Act
        app.updateQuality();
        // Assert
        assertEquals(0, app.items[0].quality);
    }

}
