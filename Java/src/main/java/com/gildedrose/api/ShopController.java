package com.gildedrose.api;

import com.gildedrose.application.ShopService;
import com.gildedrose.application.dto.ItemDto;
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

    private final ShopService shopService;

    /**
     * Constructs the controller with the shop service dependency.
     *
     * @param shopService the application service orchestrating shop operations
     */
    public ShopController(final ShopService shopService) {
        this.shopService = shopService;
    }

    /**
     * Returns all items currently held in the shop, optionally sorted.
     *
     * @param sortBy the field to sort by (name, sellIn, quality) or null for default order
     * @param sortDir the sort direction (asc, desc) or null for default
     * @return a list of item DTOs representing the current inventory
     */
    @GetMapping
    public List<ItemDto> getItems(
            @RequestParam(required = false) final String sortBy,
            @RequestParam(required = false) final String sortDir) {
        return shopService.getAllItems(sortBy, sortDir);
    }

    /**
     * Advances the shop by one day, updating all item qualities, and returns the updated inventory.
     *
     * @return a list of item DTOs after quality update
     */
    @PostMapping("/advance-day")
    public List<ItemDto> advanceDay() {
        return shopService.advanceDay();
    }

    /**
     * Returns the current price of a named item.
     *
     * @param name the name of the item to price
     * @return the item price, or 404 if no matching item is found
     */
    @GetMapping("/{name}/price")
    public ResponseEntity<Integer> getPrice(@PathVariable final String name) {
        final Integer price = shopService.getPriceFor(name);
        if (price == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(price);
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
        final ItemDto projection = shopService.projectItem(name, days);
        if (projection == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(projection);
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
        return ResponseEntity.ok(shopService.projectAllItems(days));
    }
}
