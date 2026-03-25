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
     * Persists all given items.
     *
     * @param items the items to persist
     */
    void saveAll(List<Item> items);
}
