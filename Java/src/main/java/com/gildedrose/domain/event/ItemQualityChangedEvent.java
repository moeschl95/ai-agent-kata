package com.gildedrose.domain.event;

/**
 * Event published when an item's quality changes to a non-zero value during a day advance.
 *
 * @param itemName        the name of the item whose quality changed
 * @param previousQuality the quality before the update
 * @param newQuality      the quality after the update
 */
public record ItemQualityChangedEvent(String itemName, int previousQuality, int newQuality) {
}
