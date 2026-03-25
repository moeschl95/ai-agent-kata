package com.gildedrose.application;

import com.gildedrose.application.dto.ItemDto;
import com.gildedrose.domain.model.Item;
import com.gildedrose.domain.repository.ItemRepositoryPort;
import com.gildedrose.domain.service.PricingService;
import com.gildedrose.domain.service.ProjectionService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Application service that orchestrates shop operations.
 * Coordinates between the API layer and domain/infrastructure layers.
 */
@Service
public class ShopService {

    private final ItemRepositoryPort itemRepository;
    private final PricingService pricingService;
    private final ProjectionService projectionService;
    private final GildedRoseUseCase gildedRose;

    /**
     * Constructs the shop service with required dependencies.
     *
     * @param itemRepository     the repository port for item persistence
     * @param pricingService     the service for computing item prices
     * @param projectionService  the service for projecting future item states
     * @param gildedRose         the domain service for advancing the day
     */
    public ShopService(
            final ItemRepositoryPort itemRepository,
            final PricingService pricingService,
            final ProjectionService projectionService,
            final GildedRoseUseCase gildedRose) {
        this.itemRepository = itemRepository;
        this.pricingService = pricingService;
        this.projectionService = projectionService;
        this.gildedRose = gildedRose;
    }

    /**
     * Returns all items currently in the shop inventory.
     *
     * @return a list of item DTOs
     */
    public List<ItemDto> getAllItems() {
        return mapToDto(itemRepository.findAll());
    }

    /**
     * Returns all items currently in the shop inventory, optionally sorted.
     *
     * @param sortBy the field to sort by (name, sellIn, quality) or null for default order
     * @param sortDir the sort direction (asc, desc) or null for ascending if sortBy is provided
     * @return a list of item DTOs, sorted if sortBy is provided
     */
    public List<ItemDto> getAllItems(final String sortBy, final String sortDir) {
        return mapToDto(itemRepository.findAll(sortBy, sortDir));
    }

    /**
     * Advances the shop by one day, updating all item qualities and persisting the changes.
     * Publishes domain events for item changes.
     *
     * @return the updated inventory as DTOs
     */
    public List<ItemDto> advanceDay() {
        final List<Item> items = itemRepository.findAll();
        gildedRose.updateQuality(items);
        itemRepository.saveAll(items);
        return mapToDto(items);
    }

    /**
     * Returns the current price of a named item.
     *
     * @param itemName the name of the item
     * @return the price, or null if the item is not found
     */
    public Integer getPriceFor(final String itemName) {
        return itemRepository.findAll().stream()
                .filter(item -> item.name.equals(itemName))
                .findFirst()
                .map(pricingService::priceFor)
                .orElse(null);
    }

    /**
     * Projects the state of a named item after the given number of days.
     *
     * @param itemName the name of the item
     * @param days     the number of days to project
     * @return the projected item DTO, or null if the item is not found
     */
    public ItemDto projectItem(final String itemName, final int days) {
        return itemRepository.findAll().stream()
                .filter(item -> item.name.equals(itemName))
                .findFirst()
                .map(item -> projectionService.project(item, days))
                .orElse(null);
    }

    /**
     * Projects the entire inventory after the given number of days.
     *
     * @param days the number of days to project
     * @return a list of projected item DTOs
     */
    public List<ItemDto> projectAllItems(final int days) {
        return projectionService.projectAll(itemRepository.findAll(), days);
    }

    private List<ItemDto> mapToDto(final List<Item> items) {
        return items.stream()
                .map(item -> new ItemDto(item.name, item.sellIn, item.quality))
                .toList();
    }

    /**
     * Interface defining the use case for advancing the shop by one day.
     * Implemented by GildedRose.
     */
    public interface GildedRoseUseCase {
        /**
         * Advances all items by one day, applying quality-update rules.
         *
         * @param items the items to update
         */
        void updateQuality(List<Item> items);
    }
}
