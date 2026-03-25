package com.gildedrose;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ShopController {

    private final GildedRose gildedRose;
    private final PricingService pricingService;
    private final ProjectionService projectionService;

    public ShopController(GildedRose gildedRose, PricingService pricingService, ProjectionService projectionService) {
        this.gildedRose = gildedRose;
        this.pricingService = pricingService;
        this.projectionService = projectionService;
    }

    @GetMapping
    public List<ItemDto> getItems() {
        return mapItemsToDto(gildedRose.items);
    }

    @PostMapping("/advance-day")
    public List<ItemDto> advanceDay() {
        gildedRose.updateQuality();
        return mapItemsToDto(gildedRose.items);
    }

    @GetMapping("/{name}/price")
    public ResponseEntity<Integer> getPrice(@PathVariable String name) {
        Item item = Arrays.stream(gildedRose.items)
                .filter(i -> i.name.equals(name))
                .findFirst()
                .orElse(null);

        if (item == null) {
            return ResponseEntity.notFound().build();
        }

        int price = pricingService.priceFor(item);
        return ResponseEntity.ok(price);
    }

    @GetMapping("/{name}/projection")
    public ResponseEntity<ItemDto> getProjection(@PathVariable String name, @RequestParam int days) {
        if (days < 0) {
            return ResponseEntity.badRequest().build();
        }

        Item item = Arrays.stream(gildedRose.items)
                .filter(i -> i.name.equals(name))
                .findFirst()
                .orElse(null);

        if (item == null) {
            return ResponseEntity.notFound().build();
        }

        ItemDto projected = projectionService.project(item, days);
        return ResponseEntity.ok(projected);
    }

    @GetMapping("/projection")
    public ResponseEntity<List<ItemDto>> getBulkProjection(@RequestParam int days) {
        if (days < 0) {
            return ResponseEntity.badRequest().build();
        }

        List<ItemDto> projected = projectionService.projectAll(gildedRose.items, days);
        return ResponseEntity.ok(projected);
    }

    private List<ItemDto> mapItemsToDto(Item[] items) {
        return Arrays.stream(items)
                .map(item -> new ItemDto(item.name, item.sellIn, item.quality))
                .toList();
    }
}
