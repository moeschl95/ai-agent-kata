package com.gildedrose;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller exposing shop operations for the Gilded Rose inventory.
 * <p>
 * Provides endpoints for listing items, advancing the shop day, retrieving item prices,
 * and projecting future item states.
 * </p>
 */
@RestController
@RequestMapping("/api/items")
public class ShopController {

    private final GildedRose gildedRose;
    private final PricingService pricingService;
    private final ProjectionService projectionService;

    /**
     * Constructs the controller with its required service dependencies.
     *
     * @param gildedRose        the shop containing the current inventory
     * @param pricingService    the service used to compute item prices
     * @param projectionService the service used to project future item states
     */
    public ShopController(final GildedRose gildedRose, final PricingService pricingService, final ProjectionService projectionService) {
        this.gildedRose = gildedRose;
        this.pricingService = pricingService;
        this.projectionService = projectionService;
    }

    /**
     * Returns all items currently held in the shop.
     *
     * @return a list of item DTOs representing the current inventory
     */
    @GetMapping
    public List<ItemDto> getItems() {
        return mapItemsToDto(gildedRose.items);
    }

    /**
     * Advances the shop by one day, updating all item qualities, and returns the updated inventory.
     *
     * @return a list of item DTOs after quality update
     */
    @PostMapping("/advance-day")
    public List<ItemDto> advanceDay() {
        gildedRose.updateQuality();
        return mapItemsToDto(gildedRose.items);
    }

    /**
     * Returns the current price of a named item.
     *
     * @param name the name of the item to price
     * @return the item price, or 404 if no matching item is found
     */
    @GetMapping("/{name}/price")
    public ResponseEntity<Integer> getPrice(@PathVariable final String name) {
        return gildedRose.items.stream()
                .filter(i -> i.name.equals(name))
                .findFirst()
                .map(item -> ResponseEntity.ok(pricingService.priceFor(item)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Projects the state of a named item after the given number of days.
     *
     * @param name the name of the item to project
     * @param days the number of days to project forward; must be non-negative
     * @return the projected item DTO, or 400 if {@code days} is negative, or 404 if not found
     */
    @GetMapping("/{name}/projection")
    public ResponseEntity<ItemDto> getProjection(@PathVariable final String name, @RequestParam final int days) {
        if (days < 0) return ResponseEntity.badRequest().build();
        return gildedRose.items.stream()
                .filter(i -> i.name.equals(name))
                .findFirst()
                .map(item -> ResponseEntity.ok(projectionService.project(item, days)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Projects the state of all shop items after the given number of days.
     *
     * @param days the number of days to project forward; must be non-negative
     * @return a list of projected item DTOs, or 400 if {@code days} is negative
     */
    @GetMapping("/projection")
    public ResponseEntity<List<ItemDto>> getBulkProjection(@RequestParam final int days) {
        if (days < 0) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(projectionService.projectAll(gildedRose.items, days));
    }

    private List<ItemDto> mapItemsToDto(final List<Item> items) {
        return items.stream()
                .map(item -> new ItemDto(item.name, item.sellIn, item.quality))
                .toList();
    }
}
