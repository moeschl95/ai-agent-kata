package com.gildedrose.api;

import com.gildedrose.application.ShopService;
import com.gildedrose.application.dto.ItemDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Items", description = "Shop inventory operations")
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
    @Operation(summary = "List all shop items", description = "Retrieve all items currently held in the shop inventory, with optional sorting.")
    @ApiResponse(responseCode = "200", description = "Items retrieved successfully", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ItemDto.class)))
    public List<ItemDto> getItems(
            @Parameter(description = "Field to sort by: name, sellIn, or quality") @RequestParam(required = false) final String sortBy,
            @Parameter(description = "Sort direction: asc for ascending, desc for descending") @RequestParam(required = false) final String sortDir) {
        return shopService.getAllItems(sortBy, sortDir);
    }

    /**
     * Advances the shop by one day, updating all item qualities, and returns the updated inventory.
     *
     * @return a list of item DTOs after quality update
     */
    @PostMapping("/advance-day")
    @Operation(summary = "Advance shop by one day", description = "Progress the shop forward by one day, applying quality adjustments to all items based on their type and sell-in status.")
    @ApiResponse(responseCode = "200", description = "Shop advanced successfully, updated inventory returned",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ItemDto.class)))
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
    @Operation(summary = "Get price for item", description = "Retrieve the current market price of a named item.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item price retrieved successfully", 
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "integer", example = "10"))),
            @ApiResponse(responseCode = "404", description = "Item not found")
    })
    public ResponseEntity<Integer> getPrice(
            @Parameter(description = "The name of the item") @PathVariable final String name) {
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
    @Operation(summary = "Get item state projection", description = "Simulate the state of a named item after n days without mutating the live inventory.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Projection calculated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ItemDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid days parameter (must be non-negative)"),
            @ApiResponse(responseCode = "404", description = "Item not found")
    })
    public ResponseEntity<ItemDto> getProjection(
            @Parameter(description = "The name of the item to project") @PathVariable final String name,
            @Parameter(description = "Number of days to project forward (must be non-negative)") @RequestParam final int days) {
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
    @Operation(summary = "Get bulk shop inventory projection", description = "Simulate the state of all shop items after n days without mutating the live inventory.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Bulk projection calculated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ItemDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid days parameter (must be non-negative)")
    })
    public ResponseEntity<List<ItemDto>> getBulkProjection(
            @Parameter(description = "Number of days to project forward (must be non-negative)") @RequestParam final int days) {
        if (days < 0) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(shopService.projectAllItems(days));
    }
}
