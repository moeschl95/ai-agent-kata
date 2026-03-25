package com.gildedrose;

import java.util.List;

/**
 * Manages the Gilded Rose shop inventory and advances item quality by one day.
 */
public class GildedRose {

    /** The list of items currently held in the shop. */
    public List<Item> items;

    /**
     * Constructs a new GildedRose shop with the given list of items.
     *
     * @param items the initial inventory of the shop
     */
    public GildedRose(final List<Item> items) {
        this.items = items;
    }

    /**
     * Advances all items by one day, applying each item's quality-update rules.
     */
    public void updateQuality() {
        for (final Item item : items) {
            ItemUpdaterFactory.forItem(item).update(item);
        }
    }
}
