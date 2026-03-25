package com.gildedrose;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShopControllerTest {

    private GildedRose gildedRose;

    @Mock
    private PricingService pricingService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        gildedRose = new GildedRose(new Item[]{});
        ShopController shopController = new ShopController(gildedRose, pricingService);
        mockMvc = MockMvcBuilders.standaloneSetup(shopController).build();
    }

    @Test
    void should_returnAllItems_when_getItemsEndpointIsCalled() throws Exception {
        Item[] items = new Item[]{
                new Item("Aged Brie", 10, 20),
                new Item("Normal item", 5, 15)
        };
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
        gildedRose.items = new Item[]{};

        mockMvc.perform(get("/api/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void should_updateQualityAndReturnUpdatedItems_when_advanceDayEndpointIsCalled() throws Exception {
        Item[] items = new Item[]{
                new Item("Normal item", 5, 10)
        };
        gildedRose.items = items;

        mockMvc.perform(post("/api/items/advance-day"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", equalTo("Normal item")));
    }

    @Test
    void should_returnPriceAsJson_when_getPriceForKnownItem() throws Exception {
        Item item = new Item("Aged Brie", 10, 20);
        gildedRose.items = new Item[]{item};

        when(pricingService.priceFor(item)).thenReturn(50);

        // Note: MockMvc passes path variables internally without URL encoding.
        // In a real HTTP request, the space would need to be encoded as %20.
        mockMvc.perform(get("/api/items/Aged Brie/price"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", equalTo(50)));
    }

    @Test
    void should_returnNotFound_when_getPriceForUnknownItem() throws Exception {
        gildedRose.items = new Item[]{};

        mockMvc.perform(get("/api/items/UnknownItem/price"))
                .andExpect(status().isNotFound());
    }
}
