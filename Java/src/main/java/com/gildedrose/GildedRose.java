package com.gildedrose;

import org.springframework.context.ApplicationEventPublisher;

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

    /** Optional publisher for domain events (null when no Spring context is available). */
    private final ApplicationEventPublisher publisher;

    /** Map to track the original database ID for each item (used for persistence). */
    private final Map<Item, Long> itemIdMap;

    /**
     * Constructs a new GildedRose shop with the given list of items.
     * This constructor is used for testing and does not persist items or publish events.
     *
     * @param items the initial inventory of the shop
     */
    public GildedRose(final List<Item> items) {
        this.items = items;
        this.repository = null;
        this.publisher = null;
        this.itemIdMap = new HashMap<>();
    }

    /**
     * Constructs a new GildedRose shop with the given list of items and an event publisher.
     * This constructor is used for testing with event verification.
     *
     * @param items     the initial inventory of the shop
     * @param publisher the event publisher used to publish domain events
     */
    public GildedRose(final List<Item> items, final ApplicationEventPublisher publisher) {
        this.items = items;
        this.repository = null;
        this.publisher = publisher;
        this.itemIdMap = new HashMap<>();
    }

    /**
     * Constructs a new GildedRose shop using a repository to load and persist items.
     * No events are published when using this constructor.
     *
     * @param repository the repository to load and persist items
     */
    public GildedRose(final ItemRepository repository) {
        this(repository, null);
    }

    /**
     * Constructs a new GildedRose shop using a repository to load and persist items.
     * Loads all items from the repository on construction.
     *
     * @param repository the repository to load and persist items
     * @param publisher  the event publisher used to publish domain events
     */
    public GildedRose(final ItemRepository repository, final ApplicationEventPublisher publisher) {
        this.repository = repository;
        this.publisher = publisher;
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
     * If a publisher is available, fires domain events for each changed item and
     * a single {@link DayAdvancedEvent} at the end.
     */
    public void updateQuality() {
        for (final Item item : items) {
            final int qualityBefore = item.quality;
            ItemUpdaterFactory.forItem(item).update(item);
            publishItemEventIfChanged(item, qualityBefore);
        }
        if (repository != null) {
            final List<ItemEntity> entities = ItemMapper.toEntitiesWithIds(items, itemIdMap);
            repository.saveAll(entities);
        }
        if (publisher != null) {
            publisher.publishEvent(new DayAdvancedEvent(items));
        }
    }

    private void publishItemEventIfChanged(final Item item, final int qualityBefore) {
        if (publisher == null || item.quality == qualityBefore) {
            return;
        }
        if (item.quality == 0) {
            publisher.publishEvent(new ItemExpiredEvent(item.name));
        } else {
            publisher.publishEvent(new ItemQualityChangedEvent(item.name, qualityBefore, item.quality));
        }
    }
}
