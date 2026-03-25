package com.gildedrose.infrastructure.config;

import com.gildedrose.domain.model.Item;
import com.gildedrose.domain.repository.ItemRepositoryPort;
import com.gildedrose.infrastructure.persistence.ItemEntity;
import com.gildedrose.infrastructure.persistence.ItemRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Initializes the item database with default shop inventory on application startup.
 * Seeds the ITEMS table only if no items exist yet.
 */
@Component
public class ItemDataInitializer {

    private final ItemRepository jpaRepository;

    /**
     * Constructs the initializer with the given repository.
     *
     * @param jpaRepository the JPA repository to seed with default items
     */
    public ItemDataInitializer(final ItemRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    /**
     * Initializes the database with default items when the application is ready.
     * Only seeds if the ITEMS table is currently empty.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        if (jpaRepository.count() == 0) {
            seedDefaultItems();
        }
    }

    /**
     * Seeds the repository with the default shop inventory.
     */
    private void seedDefaultItems() {
        final List<ItemEntity> defaultItems = List.of(
                new ItemEntity("+5 Dexterity Vest", 10, 20),
                new ItemEntity("Aged Brie", 2, 0),
                new ItemEntity("Elixir of the Mongoose", 5, 7),
                new ItemEntity("Sulfuras, Hand of Ragnaros", 0, 80),
                new ItemEntity("Backstage passes to a TAFKAL80ETC concert", 15, 20),
                new ItemEntity("Conjured Mana Cake", 3, 6)
        );
        jpaRepository.saveAll(defaultItems);
    }
}
