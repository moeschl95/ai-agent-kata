package com.gildedrose.infrastructure.persistence;

import com.gildedrose.domain.model.Item;
import com.gildedrose.domain.repository.ItemRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Adapter implementing {@link ItemRepositoryPort} (domain contract) using Spring Data JPA.
 * This adapter isolates the domain layer from JPA-specific details.
 */
@Component
public class JpaItemRepositoryAdapter implements ItemRepositoryPort {

    private final ItemRepository jpaRepository;
    private final Map<Item, Long> itemIdMap; // Track IDs to preserve them on save

    /**
     * Constructs the adapter with the JPA repository.
     *
     * @param jpaRepository the Spring Data JPA interface
     */
    public JpaItemRepositoryAdapter(final ItemRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
        this.itemIdMap = new HashMap<>();
    }

    @Override
    public List<Item> findAll() {
        final List<ItemEntity> entities = jpaRepository.findAll();
        final List<Item> items = new ArrayList<>(ItemMapper.toDomains(entities));
        
        // Build the ID map for future saves
        itemIdMap.clear();
        for (int i = 0; i < entities.size(); i++) {
            itemIdMap.put(items.get(i), entities.get(i).id);
        }
        
        return items;
    }

    @Override
    public void saveAll(final List<Item> items) {
        final List<ItemEntity> entities = ItemMapper.toEntitiesWithIds(items, itemIdMap);
        jpaRepository.saveAll(entities);
    }
}
