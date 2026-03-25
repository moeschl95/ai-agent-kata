package com.gildedrose;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Integration tests for {@link GildedRose} with persistence via {@link ItemRepository}.
 */
@DataJpaTest
@ComponentScan(basePackages = "com.gildedrose")
class GildedRoseIntegrationTest {

    @Autowired
    private ItemRepository repository;

    private GildedRose gildedRose;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        gildedRose = new GildedRose(repository);
    }

    @Test
    void should_persistItemsAfterUpdateQuality_when_repositoryIsAvailable() {
        // Arrange
        final ItemEntity item1 = new ItemEntity("+5 Dexterity Vest", 10, 20);
        final ItemEntity item2 = new ItemEntity("Aged Brie", 2, 0);
        repository.saveAll(List.of(item1, item2));

        // Re-initialize GildedRose to load from repository
        gildedRose = new GildedRose(repository);

        // Act
        gildedRose.updateQuality();

        // Assert - check that updated values are persisted in the database
        final List<ItemEntity> persisted = repository.findAll();
        assertEquals(2, persisted.size());

        final ItemEntity vest = persisted.stream()
                .filter(e -> e.name.equals("+5 Dexterity Vest"))
                .findFirst()
                .orElseThrow();
        assertEquals(9, vest.sellIn);
        assertEquals(19, vest.quality);

        final ItemEntity brie = persisted.stream()
                .filter(e -> e.name.equals("Aged Brie"))
                .findFirst()
                .orElseThrow();
        assertEquals(1, brie.sellIn);
        assertEquals(1, brie.quality);
    }

    @Test
    void should_loadItemsFromRepository_when_constructorWithRepositoryIsUsed() {
        // Arrange
        final ItemEntity item1 = new ItemEntity("Test Item", 5, 10);
        final ItemEntity item2 = new ItemEntity("Another Item", 3, 15);
        repository.saveAll(List.of(item1, item2));

        // Act
        gildedRose = new GildedRose(repository);

        // Assert
        assertEquals(2, gildedRose.items.size());
        assertEquals("Test Item", gildedRose.items.get(0).name);
        assertEquals("Another Item", gildedRose.items.get(1).name);
    }

    @Test
    void should_persistMultipleUpdates_when_updateQualityIsCalledMultipleTimes() {
        // Arrange
        final ItemEntity item = new ItemEntity("Aged Brie", 2, 0);
        repository.saveAll(List.of(item));
        gildedRose = new GildedRose(repository);

        // Act
        gildedRose.updateQuality();
        gildedRose.updateQuality();

        // Assert - after two updates, quality should be 2 and sellIn should be 0
        final ItemEntity persisted = repository.findAll().get(0);
        assertEquals(0, persisted.sellIn);
        assertEquals(2, persisted.quality);
    }
}
