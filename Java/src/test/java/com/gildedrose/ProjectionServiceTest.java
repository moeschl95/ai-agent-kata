package com.gildedrose;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProjectionServiceTest {

    private final ProjectionService projectionService = new ProjectionService();

    @Test
    void should_returnOriginalState_when_daysIsZero() {
        // Arrange
        Item item = new Item("Normal item", 10, 20);
        // Act
        ItemDto result = projectionService.project(item, 0);
        // Assert
        assertEquals("Normal item", result.name());
        assertEquals(10, result.sellIn());
        assertEquals(20, result.quality());
    }

    @Test
    void should_projectNormalItemForwardByOneDay_when_daysIsOne() {
        // Arrange
        Item item = new Item("Normal item", 10, 20);
        // Act
        ItemDto result = projectionService.project(item, 1);
        // Assert
        assertEquals("Normal item", result.name());
        assertEquals(9, result.sellIn());
        assertEquals(19, result.quality());
    }

    @Test
    void should_projectNormalItemForwardByFiveDays_when_daysIsFive() {
        // Arrange
        Item item = new Item("Normal item", 10, 20);
        // Act
        ItemDto result = projectionService.project(item, 5);
        // Assert
        assertEquals("Normal item", result.name());
        assertEquals(5, result.sellIn());
        assertEquals(15, result.quality());
    }

    @Test
    void should_projectNormalItemPastExpiration_when_daysExceedsSellIn() {
        // Arrange
        Item item = new Item("Normal item", 2, 10);
        // Act
        ItemDto result = projectionService.project(item, 5);
        // Assert
        assertEquals("Normal item", result.name());
        assertEquals(-3, result.sellIn());
        assertEquals(2, result.quality()); // Quality decreases but doesn't go below 0
    }

    @Test
    void should_projectAgedBrieCorrectly_when_daysIsOne() {
        // Arrange
        Item item = new Item("Aged Brie", 10, 20);
        // Act
        ItemDto result = projectionService.project(item, 1);
        // Assert
        assertEquals("Aged Brie", result.name());
        assertEquals(9, result.sellIn());
        assertEquals(21, result.quality());
    }

    @Test
    void should_projectAgedBriePastExpiration_when_daysExceedsSellIn() {
        // Arrange
        Item item = new Item("Aged Brie", 2, 10);
        // Act
        ItemDto result = projectionService.project(item, 5);
        // Assert
        assertEquals("Aged Brie", result.name());
        assertEquals(-3, result.sellIn());
        assertEquals(18, result.quality()); // Aged Brie increases quality even after expiration
    }

    @Test
    void should_notIncreaseQualityBeyondFifty_when_agedBrieQualityIsFortyNine() {
        // Arrange
        Item item = new Item("Aged Brie", 10, 49);
        // Act
        ItemDto result = projectionService.project(item, 1);
        // Assert
        assertEquals("Aged Brie", result.name());
        assertEquals(9, result.sellIn());
        assertEquals(50, result.quality()); // Quality should not exceed 50
    }

    @Test
    void should_projectBackstagePassCorrectly_when_daysIsOne() {
        // Arrange
        Item item = new Item("Backstage passes to a TAFKAL80ETC concert", 15, 20);
        // Act
        ItemDto result = projectionService.project(item, 1);
        // Assert
        assertEquals("Backstage passes to a TAFKAL80ETC concert", result.name());
        assertEquals(14, result.sellIn());
        assertEquals(21, result.quality());
    }

    @Test
    void should_projectBackstagePassToZero_when_sellInReachesZero() {
        // Arrange
        Item item = new Item("Backstage passes to a TAFKAL80ETC concert", 1, 20);
        // Act
        ItemDto result = projectionService.project(item, 1);
        // Assert
        assertEquals("Backstage passes to a TAFKAL80ETC concert", result.name());
        assertEquals(0, result.sellIn());
        assertEquals(23, result.quality()); // Quality increases by 3 when sellIn <= 5
    }

    @Test
    void should_projectBackstagePassToZeroAfterExpiration_when_sellInBecomesNegative() {
        // Arrange
        Item item = new Item("Backstage passes to a TAFKAL80ETC concert", 1, 20);
        // Act
        ItemDto result = projectionService.project(item, 2);
        // Assert
        assertEquals("Backstage passes to a TAFKAL80ETC concert", result.name());
        assertEquals(-1, result.sellIn());
        assertEquals(0, result.quality()); // Quality drops to 0 after expiration
    }

    @Test
    void should_notChangeSulfuras_when_daysIsOne() {
        // Arrange
        Item item = new Item("Sulfuras, Hand of Ragnaros", 0, 80);
        // Act
        ItemDto result = projectionService.project(item, 1);
        // Assert
        assertEquals("Sulfuras, Hand of Ragnaros", result.name());
        assertEquals(0, result.sellIn());
        assertEquals(80, result.quality()); // Sulfuras never changes
    }

    @Test
    void should_projectConjuredItemCorrectly_when_daysIsOne() {
        // Arrange
        Item item = new Item("Conjured Mana Cake", 10, 20);
        // Act
        ItemDto result = projectionService.project(item, 1);
        // Assert
        assertEquals("Conjured Mana Cake", result.name());
        assertEquals(9, result.sellIn());
        assertEquals(18, result.quality()); // Conjured items degrade twice as fast
    }

    @Test
    void should_projectConjuredAgedBrieCorrectly_when_daysIsOne() {
        // Arrange
        Item item = new Item("Conjured Aged Brie", 10, 20);
        // Act
        ItemDto result = projectionService.project(item, 1);
        // Assert
        assertEquals("Conjured Aged Brie", result.name());
        assertEquals(9, result.sellIn());
        assertEquals(22, result.quality()); // Aged Brie increases by 1, doubled by Conjured = +2
    }

    @Test
    void should_projectAllItems_when_projectAllIsCalled() {
        // Arrange
        List<Item> items = List.of(
                new Item("Normal item", 10, 20),
                new Item("Aged Brie", 5, 10)
        );
        // Act
        List<ItemDto> results = projectionService.projectAll(items, 1);
        // Assert
        assertEquals(2, results.size());
        assertEquals("Normal item", results.get(0).name());
        assertEquals(9, results.get(0).sellIn());
        assertEquals(19, results.get(0).quality());
        assertEquals("Aged Brie", results.get(1).name());
        assertEquals(4, results.get(1).sellIn());
        assertEquals(11, results.get(1).quality());
    }

    @Test
    void should_returnOriginalStates_when_projectAllWithDaysZero() {
        // Arrange
        List<Item> items = List.of(
                new Item("Normal item", 10, 20),
                new Item("Aged Brie", 5, 10)
        );
        // Act
        List<ItemDto> results = projectionService.projectAll(items, 0);
        // Assert
        assertEquals(2, results.size());
        assertEquals("Normal item", results.get(0).name());
        assertEquals(10, results.get(0).sellIn());
        assertEquals(20, results.get(0).quality());
        assertEquals("Aged Brie", results.get(1).name());
        assertEquals(5, results.get(1).sellIn());
        assertEquals(10, results.get(1).quality());
    }

    @Test
    void should_returnEmptyList_when_projectAllWithEmptyArray() {
        // Arrange
        List<Item> items = List.of();
        // Act
        List<ItemDto> results = projectionService.projectAll(items, 5);
        // Assert
        assertEquals(0, results.size());
    }
}