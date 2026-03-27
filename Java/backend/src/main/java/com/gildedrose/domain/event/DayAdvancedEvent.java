package com.gildedrose.domain.event;

import com.gildedrose.domain.model.Item;
import java.util.List;

/**
 * Event published once after all items have been updated by a single call to {@link
 * com.gildedrose.GildedRose#updateQuality()}.
 *
 * @param updatedItems a snapshot of the full inventory after the day has advanced
 */
public record DayAdvancedEvent(List<Item> updatedItems) {}
