package com.gildedrose.domain.service;

import com.gildedrose.application.ShopService.GildedRoseUseCase;
import com.gildedrose.domain.event.DayAdvancedEvent;
import com.gildedrose.domain.event.ItemExpiredEvent;
import com.gildedrose.domain.event.ItemQualityChangedEvent;
import com.gildedrose.domain.model.Item;
import com.gildedrose.domain.repository.ItemRepositoryPort;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

/**
 * Core domain service for the Gilded Rose shop.
 * Manages quality updates for items and publishes domain events.
 * Implements the use case interface for advancing the day.
 * Maintains backward compatibility with test code by supporting both update signatures.
 */
public class GildedRose implements GildedRoseUseCase {

    /** The list of items currently held in the shop (for backward compatibility with tests). */
    public List<Item> items;

    /** Optional repository port for persisting items (null if using constructor for testing only). */
    private final ItemRepositoryPort repository;

    /** Optional publisher for domain events (null when no Spring context is available). */
    private final ApplicationEventPublisher publisher;

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
    }

    /**
     * Constructs a new GildedRose shop using a repository port to load and persist items.
     * Events are not published when using this constructor.
     *
     * @param repository the repository port for item persistence
     */
    public GildedRose(final ItemRepositoryPort repository) {
        this(repository, null);
    }

    /**
     * Constructs a new GildedRose shop using a repository port to load and persist items.
     * Publishes domain events when advancing the day.
     *
     * @param repository the repository port for item persistence
     * @param publisher  the event publisher used to publish domain events
     */
    public GildedRose(final ItemRepositoryPort repository, final ApplicationEventPublisher publisher) {
        this.items = repository.findAll(); // Load items from repository
        this.repository = repository;
        this.publisher = publisher;
    }

    /**
     * Backward-compatible updateQuality() method for test code.
     * Updates the quality of all items in the internal items list.
     * Only works when GildedRose was constructed with List<Item> or List<Item> + publisher.
     * If constructed with a repository, this delegates to the repository-aware updateQuality(List).
     */
    public void updateQuality() {
        if (items == null) {
            throw new IllegalStateException(
                    "updateQuality() can only be called on GildedRose instances constructed with List<Item> or ItemRepositoryPort");
        }
        updateQuality(items);
        // If we have a repository, persist the updated items
        if (repository != null) {
            repository.saveAll(items);
        }
    }

    /**
     * Implementation of {@link GildedRoseUseCase}: advances all items by one day,
     * applying each item's quality-update rules.
     * Publishes domain events for each changed item and a single {@link DayAdvancedEvent} at the end.
     *
     * @param items the items to update
     */
    @Override
    public void updateQuality(final List<Item> items) {
        for (final Item item : items) {
            final int qualityBefore = item.quality;
            ItemUpdaterFactory.forItem(item).update(item);
            publishItemEventIfChanged(item, qualityBefore);
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
