package com.gildedrose.domain.event;

/**
 * Event published when an item's quality reaches 0 during a day advance, indicating that the item
 * has expired.
 *
 * @param itemName the name of the item that has expired
 */
public record ItemExpiredEvent(String itemName) {}
