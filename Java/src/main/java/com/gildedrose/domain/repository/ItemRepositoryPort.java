package com.gildedrose.domain.repository;

import com.gildedrose.domain.model.Item;

import java.util.List;

/**
 * Domain port defining the contract for item persistence.
 * Implementations are in the infrastructure layer.
 */
public interface ItemRepositoryPort {
    /**
     * Loads all items from persistence.
     *
     * @return a list of all items
     */
    List<Item> findAll();

    /**
     * Loads all items from persistence, optionally sorted by the specified field and direction.
     *
     * @param sortBy the field to sort by (name, sellIn, quality) or null for default order
     * @param sortDir the sort direction (asc, desc) or null for ascending if sortBy is provided
     * @return a list of items, sorted if sortBy is provided
     */
    List<Item> findAll(String sortBy, String sortDir);

    /**
     * Persists all given items.
     *
     * @param items the items to persist
     */
    void saveAll(List<Item> items);
}
