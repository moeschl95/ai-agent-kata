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

    // --- Normal items ---

    @Test
    void should_decreaseQualityByOne_when_normalItemHasPositiveSellIn() {
        // Arrange
        Item[] items = new Item[] { new Item("+5 Dexterity Vest", 10, 20) };
        GildedRose app = new GildedRose(items);
        // Act
        app.updateQuality();
        // Assert
        assertEquals(19, app.items[0].quality);
    }

    @Test
    void should_decreaseSellInByOne_when_normalItemUpdated() {
        // Arrange
        Item[] items = new Item[] { new Item("+5 Dexterity Vest", 10, 20) };
        GildedRose app = new GildedRose(items);
        // Act
        app.updateQuality();
        // Assert
        assertEquals(9, app.items[0].sellIn);
    }

    @Test
    void should_decreaseQualityByTwo_when_normalItemSellInIsExpired() {
        // Arrange
        Item[] items = new Item[] { new Item("+5 Dexterity Vest", 0, 20) };
        GildedRose app = new GildedRose(items);
        // Act
        app.updateQuality();
        // Assert
        assertEquals(18, app.items[0].quality);
    }

    @Test
    void should_notDecreaseBelowZero_when_normalItemQualityIsZero() {
        // Arrange
        Item[] items = new Item[] { new Item("+5 Dexterity Vest", 10, 0) };
        GildedRose app = new GildedRose(items);
        // Act
        app.updateQuality();
        // Assert
        assertEquals(0, app.items[0].quality);
    }

    @Test
    void should_notDecreaseBelowZero_when_normalItemQualityIsOneAndSellInExpired() {
        // Arrange
        Item[] items = new Item[] { new Item("+5 Dexterity Vest", 0, 1) };
        GildedRose app = new GildedRose(items);
        // Act
        app.updateQuality();
        // Assert
        assertEquals(0, app.items[0].quality);
    }

    // --- Aged Brie ---

    @Test
    void should_increaseQualityByOne_when_agedBrieHasPositiveSellIn() {
        // Arrange
        Item[] items = new Item[] { new Item("Aged Brie", 10, 20) };
        GildedRose app = new GildedRose(items);
        // Act
        app.updateQuality();
        // Assert
        assertEquals(21, app.items[0].quality);
    }

    @Test
    void should_decreaseSellInByOne_when_agedBrieUpdated() {
        // Arrange
        Item[] items = new Item[] { new Item("Aged Brie", 10, 20) };
        GildedRose app = new GildedRose(items);
        // Act
        app.updateQuality();
        // Assert
        assertEquals(9, app.items[0].sellIn);
    }

    @Test
    void should_increaseQualityByTwo_when_agedBrieSellInIsExpired() {
        // Arrange
        Item[] items = new Item[] { new Item("Aged Brie", 0, 20) };
        GildedRose app = new GildedRose(items);
        // Act
        app.updateQuality();
        // Assert
        assertEquals(22, app.items[0].quality);
    }

    @Test
    void should_notIncreaseAbove50_when_agedBrieQualityIsAtMax() {
        // Arrange
        Item[] items = new Item[] { new Item("Aged Brie", 10, 50) };
        GildedRose app = new GildedRose(items);
        // Act
        app.updateQuality();
        // Assert
        assertEquals(50, app.items[0].quality);
    }

    // --- Sulfuras ---

    @Test
    void should_notChangeQuality_when_sulfurasUpdated() {
        // Arrange
        Item[] items = new Item[] { new Item("Sulfuras, Hand of Ragnaros", 0, 80) };
        GildedRose app = new GildedRose(items);
        // Act
        app.updateQuality();
        // Assert
        assertEquals(80, app.items[0].quality);
    }

    @Test
    void should_notChangeSellIn_when_sulfurasUpdated() {
        // Arrange
        Item[] items = new Item[] { new Item("Sulfuras, Hand of Ragnaros", 0, 80) };
        GildedRose app = new GildedRose(items);
        // Act
        app.updateQuality();
        // Assert
        assertEquals(0, app.items[0].sellIn);
    }

    // --- Backstage passes ---

    @Test
    void should_increaseQualityByOne_when_backstagePassHasMoreThanTenDays() {
        // Arrange
        Item[] items = new Item[] { new Item("Backstage passes to a TAFKAL80ETC concert", 15, 20) };
        GildedRose app = new GildedRose(items);
        // Act
        app.updateQuality();
        // Assert
        assertEquals(21, app.items[0].quality);
    }

    @Test
    void should_increaseQualityByTwo_when_backstagePassHasTenDaysOrLess() {
        // Arrange
        Item[] items = new Item[] { new Item("Backstage passes to a TAFKAL80ETC concert", 10, 20) };
        GildedRose app = new GildedRose(items);
        // Act
        app.updateQuality();
        // Assert
        assertEquals(22, app.items[0].quality);
    }

    @Test
    void should_increaseQualityByThree_when_backstagePassHasFiveDaysOrLess() {
        // Arrange
        Item[] items = new Item[] { new Item("Backstage passes to a TAFKAL80ETC concert", 5, 20) };
        GildedRose app = new GildedRose(items);
        // Act
        app.updateQuality();
        // Assert
        assertEquals(23, app.items[0].quality);
    }

    @Test
    void should_dropQualityToZero_when_backstagePassSellInIsExpired() {
        // Arrange
        Item[] items = new Item[] { new Item("Backstage passes to a TAFKAL80ETC concert", 0, 20) };
        GildedRose app = new GildedRose(items);
        // Act
        app.updateQuality();
        // Assert
        assertEquals(0, app.items[0].quality);
    }

    @Test
    void should_notIncreaseAbove50_when_backstagePassQualityIsNearMax() {
        // Arrange
        Item[] items = new Item[] { new Item("Backstage passes to a TAFKAL80ETC concert", 10, 49) };
        GildedRose app = new GildedRose(items);
        // Act
        app.updateQuality();
        // Assert
        assertEquals(50, app.items[0].quality);
    }

    @Test
    void should_decreaseSellInByOne_when_backstagePassUpdated() {
        // Arrange
        Item[] items = new Item[] { new Item("Backstage passes to a TAFKAL80ETC concert", 15, 20) };
        GildedRose app = new GildedRose(items);
        // Act
        app.updateQuality();
        // Assert
        assertEquals(14, app.items[0].sellIn);
    }

    // --- Conjured Aged Brie ---

    @Test
    void should_increaseQualityByTwo_when_conjuredAgedBrieHasPositiveSellIn() {
        // Arrange
        Item[] items = new Item[] { new Item("Conjured Aged Brie", 10, 20) };
        GildedRose app = new GildedRose(items);
        // Act
        app.updateQuality();
        // Assert
        assertEquals(22, app.items[0].quality);
    }

    @Test
    void should_decreaseSellInByOne_when_conjuredAgedBrieUpdated() {
        // Arrange
        Item[] items = new Item[] { new Item("Conjured Aged Brie", 10, 20) };
        GildedRose app = new GildedRose(items);
        // Act
        app.updateQuality();
        // Assert
        assertEquals(9, app.items[0].sellIn);
    }

    @Test
    void should_increaseQualityByFour_when_conjuredAgedBrieSellInIsExpired() {
        // Arrange
        Item[] items = new Item[] { new Item("Conjured Aged Brie", 0, 20) };
        GildedRose app = new GildedRose(items);
        // Act
        app.updateQuality();
        // Assert
        assertEquals(24, app.items[0].quality);
    }

    @Test
    void should_notIncreaseAbove50_when_conjuredAgedBrieQualityIsAtMax() {
        // Arrange
        Item[] items = new Item[] { new Item("Conjured Aged Brie", 10, 50) };
        GildedRose app = new GildedRose(items);
        // Act
        app.updateQuality();
        // Assert
        assertEquals(50, app.items[0].quality);
    }

    @Test
    void should_notIncreaseAbove50_when_conjuredAgedBrieQualityIsNearMaxAndSellInExpired() {
        // Arrange
        Item[] items = new Item[] { new Item("Conjured Aged Brie", -1, 48) };
        GildedRose app = new GildedRose(items);
        // Act
        app.updateQuality();
        // Assert
        assertEquals(50, app.items[0].quality);
    }

    // --- Conjured Backstage passes ---

    @Test
    void should_increaseQualityByTwo_when_conjuredBackstagePassHasMoreThanTenDays() {
        // Arrange
        Item[] items = new Item[] { new Item("Conjured Backstage passes to a TAFKAL80ETC concert", 15, 20) };
        GildedRose app = new GildedRose(items);
        // Act
        app.updateQuality();
        // Assert
        assertEquals(22, app.items[0].quality);
    }

    @Test
    void should_increaseQualityByFour_when_conjuredBackstagePassHasTenDaysOrLess() {
        // Arrange
        Item[] items = new Item[] { new Item("Conjured Backstage passes to a TAFKAL80ETC concert", 10, 20) };
        GildedRose app = new GildedRose(items);
        // Act
        app.updateQuality();
        // Assert
        assertEquals(24, app.items[0].quality);
    }

    @Test
    void should_increaseQualityBySix_when_conjuredBackstagePassHasFiveDaysOrLess() {
        // Arrange
        Item[] items = new Item[] { new Item("Conjured Backstage passes to a TAFKAL80ETC concert", 5, 20) };
        GildedRose app = new GildedRose(items);
        // Act
        app.updateQuality();
        // Assert
        assertEquals(26, app.items[0].quality);
    }

    @Test
    void should_dropQualityToZero_when_conjuredBackstagePassSellInIsExpired() {
        // Arrange
        Item[] items = new Item[] { new Item("Conjured Backstage passes to a TAFKAL80ETC concert", 0, 20) };
        GildedRose app = new GildedRose(items);
        // Act
        app.updateQuality();
        // Assert
        assertEquals(0, app.items[0].quality);
    }

    @Test
    void should_notIncreaseAbove50_when_conjuredBackstagePassQualityIsNearMax() {
        // Arrange
        Item[] items = new Item[] { new Item("Conjured Backstage passes to a TAFKAL80ETC concert", 10, 48) };
        GildedRose app = new GildedRose(items);
        // Act
        app.updateQuality();
        // Assert
        assertEquals(50, app.items[0].quality);
    }

    @Test
    void should_notIncreaseAbove50_when_conjuredBackstagePassQualityIsNearMaxWithFiveDaysRemaining() {
        // Arrange
        Item[] items = new Item[] { new Item("Conjured Backstage passes to a TAFKAL80ETC concert", 5, 47) };
        GildedRose app = new GildedRose(items);
        // Act
        app.updateQuality();
        // Assert
        assertEquals(50, app.items[0].quality);
    }

    @Test
    void should_decreaseSellInByOne_when_conjuredBackstagePassUpdated() {
        // Arrange
        Item[] items = new Item[] { new Item("Conjured Backstage passes to a TAFKAL80ETC concert", 15, 20) };
        GildedRose app = new GildedRose(items);
        // Act
        app.updateQuality();
        // Assert
        assertEquals(14, app.items[0].sellIn);
    }

}
