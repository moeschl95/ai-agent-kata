package com.gildedrose;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages the Gilded Rose shop inventory and advances item quality by one day.
 */
public class GildedRose {

    /** The list of items currently held in the shop. */
    public List<Item> items;

    /** Optional repository for persisting items (null if using constructor without repository). */
    private final ItemRepository repository;

    /** Map to track the original database ID for each item (used for persistence). */
    private final Map<Item, Long> itemIdMap;

    /**
     * Constructs a new GildedRose shop with the given list of items.
     * This constructor is used for testing and does not persist items.
     *
     * @param items the initial inventory of the shop
     */
    public GildedRose(final List<Item> items) {
        this.items = items;
        this.repository = null;
        this.itemIdMap = new HashMap<>();
    }

    /**
     * Constructs a new GildedRose shop using a repository to load and persist items.
     * Loads all items from the repository on construction.
     *
     * @param repository the repository to load and persist items
     */
    public GildedRose(final ItemRepository repository) {
        this.repository = repository;
        this.itemIdMap = new HashMap<>();

        final List<ItemEntity> entities = repository.findAll();
        this.items = ItemMapper.toDomains(entities);

        // Track the original IDs for later persistence
        for (int i = 0; i < entities.size(); i++) {
            itemIdMap.put(items.get(i), entities.get(i).id);
        }
    }

    /**
     * Advances all items by one day, applying each item's quality-update rules.
     * If a repository is available, persists the updated items to the database.
     */
    public void updateQuality() {
        for (final Item item : items) {
            ItemUpdaterFactory.forItem(item).update(item);
        }
        if (repository != null) {
            final List<ItemEntity> entities = ItemMapper.toEntitiesWithIds(items, itemIdMap);
            repository.saveAll(entities);
        }
    }
}
