package com.gildedrose.infrastructure.persistence;

import com.gildedrose.domain.model.Item;
import com.gildedrose.domain.repository.ItemRepositoryPort;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

/**
 * Adapter implementing {@link ItemRepositoryPort} (domain contract) using Spring Data JPA. This
 * adapter isolates the domain layer from JPA-specific details.
 */
@Component
public class JpaItemRepositoryAdapter implements ItemRepositoryPort {

  private final ItemRepository jpaRepository;
  private final Map<Item, Long> itemIdMap; // Track IDs to preserve them on save
  private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("name", "sellIn", "quality");

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
  public List<Item> findAll(final String sortBy, final String sortDir) {
    // If no sortBy is provided, return items in default order
    if (sortBy == null || !ALLOWED_SORT_FIELDS.contains(sortBy)) {
      return findAll();
    }

    // Validate and determine direction
    final Sort.Direction direction =
        "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;

    final Sort sort = Sort.by(direction, sortBy);
    final List<ItemEntity> entities = jpaRepository.findAll(sort);
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
