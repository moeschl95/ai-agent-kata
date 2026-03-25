package com.gildedrose.infrastructure.persistence;

import com.gildedrose.domain.model.Item;
import com.gildedrose.domain.service.GildedRose;
import com.gildedrose.domain.repository.ItemRepositoryPort;
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
    private ItemRepository jpaRepository;

    private GildedRose gildedRose;
    private ItemRepositoryPort repositoryPort;

    @BeforeEach
    void setUp() {
        jpaRepository.deleteAll();
        // Create an adapter for testing that preserves entity IDs
        repositoryPort = new ItemRepositoryPort() {
            private java.util.Map<Item, Long> idMap = new java.util.IdentityHashMap<>();

            @Override
            public List<Item> findAll() {
                return jpaRepository.findAll().stream()
                        .map(entity -> {
                            Item item = ItemMapper.toDomain(entity);
                            idMap.put(item, entity.id);
                            return item;
                        })
                        .toList();
            }

            @Override
            public List<Item> findAll(String sortBy, String sortDir) {
                // For this test implementation, delegate to findAll() without sorting
                return findAll();
            }

            @Override
            public void saveAll(List<Item> items) {
                List<ItemEntity> entities = items.stream()
                        .map(item -> {
                            Long id = idMap.get(item);
                            if (id != null) {
                                return ItemMapper.toEntityWithId(item, id);
                            } else {
                                return ItemMapper.toEntity(item);
                            }
                        })
                        .toList();
                jpaRepository.saveAll(entities);
            }
        };
        gildedRose = new GildedRose(repositoryPort);
    }

    @Test
    void should_persistItemsAfterUpdateQuality_when_repositoryIsAvailable() {
        // Arrange
        final ItemEntity item1 = new ItemEntity("+5 Dexterity Vest", 10, 20);
        final ItemEntity item2 = new ItemEntity("Aged Brie", 2, 0);
        jpaRepository.saveAll(List.of(item1, item2));

        // Re-initialize GildedRose to load from repository
        gildedRose = new GildedRose(repositoryPort);

        // Act
        gildedRose.updateQuality();

        // Assert - check that updated values are persisted in the database
        final List<ItemEntity> persisted = jpaRepository.findAll();
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
        jpaRepository.saveAll(List.of(item1, item2));

        // Act
        gildedRose = new GildedRose(repositoryPort);

        // Assert
        assertEquals(2, gildedRose.items.size());
        assertEquals("Test Item", gildedRose.items.get(0).name);
        assertEquals("Another Item", gildedRose.items.get(1).name);
    }

    @Test
    void should_persistMultipleUpdates_when_updateQualityIsCalledMultipleTimes() {
        // Arrange
        final ItemEntity item = new ItemEntity("Aged Brie", 2, 0);
        jpaRepository.saveAll(List.of(item));
        gildedRose = new GildedRose(repositoryPort);

        // Act
        gildedRose.updateQuality();
        gildedRose.updateQuality();

        // Assert - after two updates, quality should be 2 and sellIn should be 0
        final ItemEntity persisted = jpaRepository.findAll().get(0);
        assertEquals(0, persisted.sellIn);
        assertEquals(2, persisted.quality);
    }
}
