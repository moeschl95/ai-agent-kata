package com.gildedrose;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * JPA entity representing an item stored in the database.
 * Mapped to the ITEMS table and used for persistence operations.
 */
@Entity
@Table(name = "ITEMS")
public class ItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String name;

    public int sellIn;

    public int quality;

    /**
     * Constructs a new ItemEntity with the specified attributes.
     *
     * @param name    the name of the item
     * @param sellIn  the number of days to sell the item
     * @param quality the quality level of the item
     */
    public ItemEntity(final String name, final int sellIn, final int quality) {
        this.name = name;
        this.sellIn = sellIn;
        this.quality = quality;
    }

    /**
     * Default constructor required by JPA.
     */
    public ItemEntity() {
    }
}
