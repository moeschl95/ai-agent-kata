package com.gildedrose.infrastructure.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/** Integration tests for {@link ItemRepository} using Spring Data JPA slice testing. */
@DataJpaTest
class ItemRepositoryTest {

  @Autowired private ItemRepository repository;

  @Test
  void should_saveAndLoadItemById_when_entityIsPersisted() {
    // Arrange
    final ItemEntity entity = new ItemEntity("Test Item", 10, 50);

    // Act
    final ItemEntity saved = repository.save(entity);
    final Optional<ItemEntity> loaded = repository.findById(saved.id);

    // Assert
    assertTrue(loaded.isPresent());
    assertEquals("Test Item", loaded.get().name);
    assertEquals(10, loaded.get().sellIn);
    assertEquals(50, loaded.get().quality);
  }

  @Test
  void should_persistMultipleItems_when_saveAllIsCalled() {
    // Arrange
    final ItemEntity item1 = new ItemEntity("Aged Brie", 2, 0);
    final ItemEntity item2 = new ItemEntity("Sulfuras, Hand of Ragnaros", 0, 80);

    // Act
    repository.saveAll(java.util.List.of(item1, item2));
    final long count = repository.count();

    // Assert
    assertEquals(2, count);
  }

  @Test
  void should_updateExistingItem_when_entityIsSavedAgain() {
    // Arrange
    final ItemEntity entity = new ItemEntity("Original Name", 5, 25);
    final ItemEntity saved = repository.save(entity);

    // Act
    saved.name = "Updated Name";
    saved.quality = 30;
    repository.save(saved);
    final Optional<ItemEntity> loaded = repository.findById(saved.id);

    // Assert
    assertTrue(loaded.isPresent());
    assertEquals("Updated Name", loaded.get().name);
    assertEquals(30, loaded.get().quality);
  }
}
