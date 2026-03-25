package com.gildedrose;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShopControllerTest {

    private GildedRose gildedRose;

    @Mock
    private PricingService pricingService;

    @Mock
    private ProjectionService projectionService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        gildedRose = new GildedRose(List.of());
        ShopController shopController = new ShopController(gildedRose, pricingService, projectionService);
        mockMvc = MockMvcBuilders.standaloneSetup(shopController).build();
    }

    @Test
    void should_returnAllItems_when_getItemsEndpointIsCalled() throws Exception {
        List<Item> items = List.of(
                new Item("Aged Brie", 10, 20),
                new Item("Normal item", 5, 15)
        );
        gildedRose.items = items;

        mockMvc.perform(get("/api/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", equalTo("Aged Brie")))
                .andExpect(jsonPath("$[0].sellIn", equalTo(10)))
                .andExpect(jsonPath("$[0].quality", equalTo(20)))
                .andExpect(jsonPath("$[1].name", equalTo("Normal item")))
                .andExpect(jsonPath("$[1].sellIn", equalTo(5)))
                .andExpect(jsonPath("$[1].quality", equalTo(15)));
    }

    @Test
    void should_returnEmptyArray_when_noItemsExist() throws Exception {
        gildedRose.items = List.of();

        mockMvc.perform(get("/api/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void should_updateQualityAndReturnUpdatedItems_when_advanceDayEndpointIsCalled() throws Exception {
        List<Item> items = List.of(
                new Item("Normal item", 5, 10)
        );
        gildedRose.items = items;

        mockMvc.perform(post("/api/items/advance-day"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", equalTo("Normal item")));
    }

    @Test
    void should_returnPriceAsJson_when_getPriceForKnownItem() throws Exception {
        Item item = new Item("Aged Brie", 10, 20);
        gildedRose.items = List.of(item);

        when(pricingService.priceFor(item)).thenReturn(50);

        // Note: MockMvc passes path variables internally without URL encoding.
        // In a real HTTP request, the space would need to be encoded as %20.
        mockMvc.perform(get("/api/items/Aged Brie/price"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", equalTo(50)));
    }

    @Test
    void should_returnNotFound_when_getPriceForUnknownItem() throws Exception {
        gildedRose.items = List.of();

        mockMvc.perform(get("/api/items/UnknownItem/price"))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_returnProjectedItem_when_projectionEndpointIsCalled() throws Exception {
        Item item = new Item("Normal item", 10, 20);
        gildedRose.items = List.of(item);
        ItemDto projectedItem = new ItemDto("Normal item", 9, 19);

        when(projectionService.project(item, 1)).thenReturn(projectedItem);

        mockMvc.perform(get("/api/items/Normal item/projection?days=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", equalTo("Normal item")))
                .andExpect(jsonPath("$.sellIn", equalTo(9)))
                .andExpect(jsonPath("$.quality", equalTo(19)));
    }

    @Test
    void should_returnNotFound_when_projectionForUnknownItem() throws Exception {
        gildedRose.items = List.of();

        mockMvc.perform(get("/api/items/UnknownItem/projection?days=1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_returnBadRequest_when_projectionWithNegativeDays() throws Exception {
        Item item = new Item("Normal item", 10, 20);
        gildedRose.items = List.of(item);

        mockMvc.perform(get("/api/items/Normal item/projection?days=-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_returnProjectedItems_when_bulkProjectionEndpointIsCalled() throws Exception {
        List<Item> items = List.of(
                new Item("Normal item", 10, 20),
                new Item("Aged Brie", 5, 10)
        );
        gildedRose.items = items;
        List<ItemDto> projectedItems = List.of(
                new ItemDto("Normal item", 9, 19),
                new ItemDto("Aged Brie", 4, 11)
        );

        when(projectionService.projectAll(items, 1)).thenReturn(projectedItems);

        mockMvc.perform(get("/api/items/projection?days=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", equalTo("Normal item")))
                .andExpect(jsonPath("$[0].sellIn", equalTo(9)))
                .andExpect(jsonPath("$[0].quality", equalTo(19)))
                .andExpect(jsonPath("$[1].name", equalTo("Aged Brie")))
                .andExpect(jsonPath("$[1].sellIn", equalTo(4)))
                .andExpect(jsonPath("$[1].quality", equalTo(11)));
    }

    @Test
    void should_returnBadRequest_when_bulkProjectionWithNegativeDays() throws Exception {
        List<Item> items = List.of(
                new Item("Normal item", 10, 20)
        );
        gildedRose.items = items;

        mockMvc.perform(get("/api/items/projection?days=-1"))
                .andExpect(status().isBadRequest());
    }
}
