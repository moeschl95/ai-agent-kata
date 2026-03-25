package com.gildedrose;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Tests that {@link GildedRose#updateQuality()} publishes the correct domain events
 * via the Spring event bus when an {@link ApplicationEventPublisher} is wired.
 */
class GildedRoseEventTest {

    private ApplicationEventPublisher publisher;

    @BeforeEach
    void setUp() {
        publisher = Mockito.mock(ApplicationEventPublisher.class);
    }

    // -------------------------------------------------------------------------
    // DayAdvancedEvent
    // -------------------------------------------------------------------------

    @Test
    void should_publishDayAdvancedEvent_when_updateQualityIsCalled() {
        // Arrange
        final GildedRose shop = new GildedRose(List.of(new Item("Normal Item", 5, 10)), publisher);

        // Act
        shop.updateQuality();

        // Assert
        final ArgumentCaptor<DayAdvancedEvent> captor = ArgumentCaptor.forClass(DayAdvancedEvent.class);
        verify(publisher).publishEvent(captor.capture());
        assertThat(captor.getValue()).isInstanceOf(DayAdvancedEvent.class);
    }

    @Test
    void should_publishExactlyOneDayAdvancedEvent_when_updateQualityIsCalled() {
        // Arrange
        final GildedRose shop = new GildedRose(
                List.of(new Item("Normal Item", 5, 10), new Item("Aged Brie", 3, 20)),
                publisher
        );

        // Act
        shop.updateQuality();

        // Assert — one DayAdvancedEvent, two ItemQualityChangedEvents (total 3)
        final ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(publisher, times(3)).publishEvent(captor.capture());
        final long dayEventCount = captor.getAllValues().stream()
                .filter(e -> e instanceof DayAdvancedEvent)
                .count();
        assertThat(dayEventCount).isEqualTo(1);
    }

    @Test
    void should_includeFinalItemsInDayAdvancedEvent_when_updateQualityIsCalled() {
        // Arrange
        final Item item = new Item("Normal Item", 5, 10);
        final GildedRose shop = new GildedRose(List.of(item), publisher);

        // Act
        shop.updateQuality();

        // Assert — DayAdvancedEvent should carry post-update state
        final ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(publisher, atLeastOnce()).publishEvent(captor.capture());
        final DayAdvancedEvent dayEvent = captor.getAllValues().stream()
                .filter(e -> e instanceof DayAdvancedEvent)
                .map(e -> (DayAdvancedEvent) e)
                .findFirst()
                .orElseThrow();
        assertThat(dayEvent.updatedItems()).containsExactly(item);
    }

    // -------------------------------------------------------------------------
    // ItemExpiredEvent
    // -------------------------------------------------------------------------

    @Test
    void should_publishItemExpiredEvent_when_itemQualityReachesZero() {
        // Arrange — quality 1 will drop to 0 after one day
        final GildedRose shop = new GildedRose(List.of(new Item("Normal Item", 5, 1)), publisher);

        // Act
        shop.updateQuality();

        // Assert
        final ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(publisher, atLeastOnce()).publishEvent(captor.capture());
        final List<ItemExpiredEvent> expiredEvents = captor.getAllValues().stream()
                .filter(e -> e instanceof ItemExpiredEvent)
                .map(e -> (ItemExpiredEvent) e)
                .toList();
        assertThat(expiredEvents).hasSize(1);
        assertThat(expiredEvents.get(0).itemName()).isEqualTo("Normal Item");
    }

    @Test
    void should_notPublishItemExpiredEvent_when_qualityWasAlreadyZero() {
        // Arrange — quality already 0, cannot drop further
        final GildedRose shop = new GildedRose(List.of(new Item("Normal Item", 5, 0)), publisher);

        // Act
        shop.updateQuality();

        // Assert — only DayAdvancedEvent, no ItemExpiredEvent and no ItemQualityChangedEvent
        final ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(publisher, times(1)).publishEvent(captor.capture());
        assertThat(captor.getValue()).isInstanceOf(DayAdvancedEvent.class);
    }

    @Test
    void should_publishItemExpiredEvent_for_eachItemThatExpires() {
        // Arrange — two items with quality 1
        final GildedRose shop = new GildedRose(
                List.of(new Item("Item A", 5, 1), new Item("Item B", 3, 1)),
                publisher
        );

        // Act
        shop.updateQuality();

        // Assert
        final ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(publisher, atLeastOnce()).publishEvent(captor.capture());
        final List<String> expiredNames = captor.getAllValues().stream()
                .filter(e -> e instanceof ItemExpiredEvent)
                .map(e -> ((ItemExpiredEvent) e).itemName())
                .toList();
        assertThat(expiredNames).containsExactlyInAnyOrder("Item A", "Item B");
    }

    // -------------------------------------------------------------------------
    // ItemQualityChangedEvent
    // -------------------------------------------------------------------------

    @Test
    void should_publishItemQualityChangedEvent_when_qualityChangesButNotToZero() {
        // Arrange — quality 10, will drop to 9
        final GildedRose shop = new GildedRose(List.of(new Item("Normal Item", 5, 10)), publisher);

        // Act
        shop.updateQuality();

        // Assert
        final ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(publisher, atLeastOnce()).publishEvent(captor.capture());
        final List<ItemQualityChangedEvent> changedEvents = captor.getAllValues().stream()
                .filter(e -> e instanceof ItemQualityChangedEvent)
                .map(e -> (ItemQualityChangedEvent) e)
                .toList();
        assertThat(changedEvents).hasSize(1);
        assertThat(changedEvents.get(0).itemName()).isEqualTo("Normal Item");
        assertThat(changedEvents.get(0).previousQuality()).isEqualTo(10);
        assertThat(changedEvents.get(0).newQuality()).isEqualTo(9);
    }

    @Test
    void should_notPublishItemQualityChangedEvent_when_qualityDoesNotChange() {
        // Arrange — Sulfuras never changes quality
        final GildedRose shop = new GildedRose(
                List.of(new Item("Sulfuras, Hand of Ragnaros", 0, 80)),
                publisher
        );

        // Act
        shop.updateQuality();

        // Assert — only DayAdvancedEvent
        final ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(publisher, times(1)).publishEvent(captor.capture());
        assertThat(captor.getValue()).isInstanceOf(DayAdvancedEvent.class);
    }

    @Test
    void should_notPublishItemQualityChangedEvent_when_qualityChangesToZero() {
        // Arrange — ItemExpiredEvent should be published instead
        final GildedRose shop = new GildedRose(List.of(new Item("Normal Item", 5, 1)), publisher);

        // Act
        shop.updateQuality();

        // Assert — no ItemQualityChangedEvent, only ItemExpiredEvent + DayAdvancedEvent
        final ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(publisher, atLeastOnce()).publishEvent(captor.capture());
        final long changedCount = captor.getAllValues().stream()
                .filter(e -> e instanceof ItemQualityChangedEvent)
                .count();
        assertThat(changedCount).isZero();
    }

    // -------------------------------------------------------------------------
    // No-publisher path (in-memory constructor)
    // -------------------------------------------------------------------------

    @Test
    void should_notPublishAnyEvents_when_noPublisherIsWired() {
        // Arrange — use the in-memory constructor that has no publisher
        final GildedRose shop = new GildedRose(List.of(new Item("Normal Item", 5, 10)));

        // Act + Assert — must not throw; verifying by the fact that no publisher is called
        shop.updateQuality();
        // No exception means the null-publisher guard is working
    }
}
