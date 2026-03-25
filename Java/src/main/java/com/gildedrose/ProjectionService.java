package com.gildedrose;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ProjectionService {

    public ItemDto project(Item item, int days) {
        if (days < 0) {
            throw new IllegalArgumentException("Days cannot be negative");
        }

        // Create a defensive copy to avoid mutating the original item
        Item projectedItem = new Item(item.name, item.sellIn, item.quality);

        // Apply updateQuality logic 'days' times
        for (int i = 0; i < days; i++) {
            ItemUpdaterFactory.forItem(projectedItem).update(projectedItem);
        }

        return new ItemDto(projectedItem.name, projectedItem.sellIn, projectedItem.quality);
    }

    public List<ItemDto> projectAll(Item[] items, int days) {
        if (days < 0) {
            throw new IllegalArgumentException("Days cannot be negative");
        }

        return Arrays.stream(items)
                .map(item -> project(item, days))
                .toList();
    }
}