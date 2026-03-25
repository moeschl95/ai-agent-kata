package com.gildedrose;

import java.util.List;
import java.util.Map;

/**
 * Utility class for mapping between {@link Item} (domain model) and {@link ItemEntity} (persistence model).
 * Centralizes conversion logic to maintain separation of concerns.
 */
public final class ItemMapper {

    private ItemMapper() {
    }

    /**
     * Converts a domain {@link Item} to a persistence {@link ItemEntity}.
     *
     * @param item the domain item to convert
     * @return an ItemEntity with the same values
     */
    public static ItemEntity toEntity(final Item item) {
        return new ItemEntity(item.name, item.sellIn, item.quality);
    }

    /**
     * Converts a domain {@link Item} to a persistence {@link ItemEntity} with the given ID.
     * Used to preserve IDs when updating existing entities.
     *
     * @param item the domain item to convert
     * @param id   the ID to assign to the entity
     * @return an ItemEntity with the same values and the specified ID
     */
    public static ItemEntity toEntityWithId(final Item item, final Long id) {
        final ItemEntity entity = new ItemEntity(item.name, item.sellIn, item.quality);
        entity.id = id;
        return entity;
    }

    /**
     * Converts a persistence {@link ItemEntity} to a domain {@link Item}.
     *
     * @param entity the persistence entity to convert
     * @return an Item with the same values
     */
    public static Item toDomain(final ItemEntity entity) {
        return new Item(entity.name, entity.sellIn, entity.quality);
    }

    /**
     * Converts a list of domain {@link Item}s to a list of {@link ItemEntity}s with preserved IDs.
     * Used when updating existing items to preserve their database IDs.
     *
     * @param items the domain items to convert
     * @param idMap map of Item to its original ID
     * @return a list of ItemEntities with preserved IDs
     */
    public static List<ItemEntity> toEntitiesWithIds(final List<Item> items, final Map<Item, Long> idMap) {
        return items.stream()
                .map(item -> {
                    final Long id = idMap.get(item);
                    return toEntityWithId(item, id);
                })
                .toList();
    }

    /**
     * Converts a list of domain {@link Item}s to a list of {@link ItemEntity}s.
     *
     * @param items the domain items to convert
     * @return a list of ItemEntities with the same values
     */
    public static List<ItemEntity> toEntities(final List<Item> items) {
        return items.stream().map(ItemMapper::toEntity).toList();
    }

    /**
     * Converts a list of persistence {@link ItemEntity}s to a list of domain {@link Item}s.
     *
     * @param entities the persistence entities to convert
     * @return a list of Items with the same values
     */
    public static List<Item> toDomains(final List<ItemEntity> entities) {
        return entities.stream().map(ItemMapper::toDomain).toList();
    }
}
