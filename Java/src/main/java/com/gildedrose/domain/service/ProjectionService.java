package com.gildedrose.domain.service;

import com.gildedrose.application.dto.ItemDto;
import com.gildedrose.domain.model.Item;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service that projects the future state of one or more shop items without modifying the originals.
 */
@Service
public class ProjectionService {

    private final PricingService pricingService;

    /**
     * Constructs the projection service with the pricing service dependency.
     *
     * @param pricingService the service for computing item prices
     */
    public ProjectionService(final PricingService pricingService) {
        this.pricingService = pricingService;
    }

    /**
     * Projects the state of a single item after the given number of days.
     * The original item is not modified.
     *
     * @param item the item to project
     * @param days the number of days to project forward; must be non-negative
     * @return an {@link ItemDto} representing the projected state
     * @throws IllegalArgumentException if {@code days} is negative
     */
    public ItemDto project(final Item item, final int days) {
        if (days < 0) throw new IllegalArgumentException("Days cannot be negative");
        final Item projectedItem = new Item(item.name, item.sellIn, item.quality);
        for (int i = 0; i < days; i++) {
            ItemUpdaterFactory.forItem(projectedItem).update(projectedItem);
        }
        return new ItemDto(projectedItem.name, projectedItem.sellIn, projectedItem.quality, pricingService.priceFor(projectedItem));
    }

    /**
     * Projects the state of all items in the given list after the given number of days.
     * The original items are not modified.
     *
     * @param items the items to project
     * @param days  the number of days to project forward; must be non-negative
     * @return a list of {@link ItemDto} instances representing the projected states
     * @throws IllegalArgumentException if {@code days} is negative
     */
    public List<ItemDto> projectAll(final List<Item> items, final int days) {
        if (days < 0) throw new IllegalArgumentException("Days cannot be negative");
        return items.stream().map(item -> project(item, days)).toList();
    }
}
