package com.gildedrose.application.dto;

/**
 * Immutable data transfer object representing the public state of a shop item.
 *
 * @param name    the item name
 * @param sellIn  the number of days remaining to sell the item
 * @param quality the current quality value of the item
 */
public record ItemDto(String name, int sellIn, int quality) {
}
