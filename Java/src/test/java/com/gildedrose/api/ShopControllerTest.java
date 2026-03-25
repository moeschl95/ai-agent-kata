package com.gildedrose.api;

import com.gildedrose.application.ShopService;
import com.gildedrose.application.dto.ItemDto;
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

    @Mock
    private ShopService shopService;

    private ShopController shopController;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        shopController = new ShopController(shopService);
        mockMvc = MockMvcBuilders.standaloneSetup(shopController).build();
    }

    @Test
    void should_returnAllItems_when_getItemsEndpointIsCalled() throws Exception {
        List<ItemDto> items = List.of(
                new ItemDto("Aged Brie", 10, 20),
                new ItemDto("Normal item", 5, 15)
        );
        when(shopService.getAllItems()).thenReturn(items);

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
        when(shopService.getAllItems()).thenReturn(List.of());

        mockMvc.perform(get("/api/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void should_updateQualityAndReturnUpdatedItems_when_advanceDayEndpointIsCalled() throws Exception {
        List<ItemDto> items = List.of(
                new ItemDto("Normal item", 5, 10)
        );
        when(shopService.advanceDay()).thenReturn(items);

        mockMvc.perform(post("/api/items/advance-day"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", equalTo("Normal item")));
    }

    @Test
    void should_returnPriceAsJson_when_getPriceForKnownItem() throws Exception {
        when(shopService.getPriceFor("Aged Brie")).thenReturn(50);

        mockMvc.perform(get("/api/items/Aged Brie/price"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", equalTo(50)));
    }

    @Test
    void should_returnNotFound_when_getPriceForUnknownItem() throws Exception {
        when(shopService.getPriceFor("UnknownItem")).thenReturn(null);

        mockMvc.perform(get("/api/items/UnknownItem/price"))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_returnProjectedItem_when_projectionEndpointIsCalled() throws Exception {
        ItemDto projectedItem = new ItemDto("Normal item", 9, 19);
        when(shopService.projectItem("Normal item", 1)).thenReturn(projectedItem);

        mockMvc.perform(get("/api/items/Normal item/projection?days=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", equalTo("Normal item")))
                .andExpect(jsonPath("$.sellIn", equalTo(9)))
                .andExpect(jsonPath("$.quality", equalTo(19)));
    }

    @Test
    void should_returnNotFound_when_projectionForUnknownItem() throws Exception {
        when(shopService.projectItem("UnknownItem", 1)).thenReturn(null);

        mockMvc.perform(get("/api/items/UnknownItem/projection?days=1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void should_returnBadRequest_when_projectionWithNegativeDays() throws Exception {
        mockMvc.perform(get("/api/items/Normal item/projection?days=-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_returnProjectedItems_when_bulkProjectionEndpointIsCalled() throws Exception {
        List<ItemDto> projectedItems = List.of(
                new ItemDto("Normal item", 9, 19),
                new ItemDto("Aged Brie", 4, 11)
        );
        when(shopService.projectAllItems(1)).thenReturn(projectedItems);

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
        mockMvc.perform(get("/api/items/projection?days=-1"))
                .andExpect(status().isBadRequest());
    }
}
