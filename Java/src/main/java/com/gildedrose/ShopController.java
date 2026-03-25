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

    public ShopController(GildedRose gildedRose, PricingService pricingService) {
        this.gildedRose = gildedRose;
        this.pricingService = pricingService;
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

    private List<ItemDto> mapItemsToDto(Item[] items) {
        return Arrays.stream(items)
                .map(item -> new ItemDto(item.name, item.sellIn, item.quality))
                .toList();
    }
}
