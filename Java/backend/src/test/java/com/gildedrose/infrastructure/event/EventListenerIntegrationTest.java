package com.gildedrose.infrastructure.event;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import com.gildedrose.domain.model.Item;
import com.gildedrose.domain.service.GildedRose;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.simp.SimpMessagingTemplate;

/**
 * Integration tests verifying that domain events from updateQuality() are properly published to
 * STOMP topics through event listeners.
 */
@SpringBootTest
class EventListenerIntegrationTest {

  @Autowired private ApplicationEventPublisher eventPublisher;

  @MockBean private SimpMessagingTemplate messagingTemplate;

  private GildedRose gildedRose;
  private ArgumentCaptor<AlertMessage> captor;

  @BeforeEach
  void setUp() {
    captor = ArgumentCaptor.forClass(AlertMessage.class);
  }

  @Test
  void should_publishExpiredItemAlert_when_updateQualityReducesItemToQualityZero() {
    // Arrange
    List<Item> items = List.of(new Item("Test Item", 5, 1));
    gildedRose = new GildedRose(items, eventPublisher);

    // Act
    gildedRose.updateQuality();

    // Assert
    verify(messagingTemplate).convertAndSend(eq("/topic/item.expired"), captor.capture());
    var alert = captor.getValue();
    assertEquals("CRITICAL", alert.severity());
    assertEquals("Item Expired: Test Item", alert.title());
    assertTrue(alert.message().contains("Test Item"));
    assertTrue(alert.message().contains("quality 0"));
  }

  @Test
  void should_publishQualityThresholdAlert_when_updateQualityReducesQualityBelowWarning() {
    // Arrange - Dexterity Vest decreases by 1, so quality 26 becomes 25 (hits warning threshold)
    List<Item> items = List.of(new Item("+5 Dexterity Vest", 5, 26));
    gildedRose = new GildedRose(items, eventPublisher);

    // Act
    gildedRose.updateQuality();

    // Assert
    verify(messagingTemplate).convertAndSend(eq("/topic/quality.threshold"), captor.capture());
    var alert = captor.getValue();
    assertEquals("WARNING", alert.severity());
    assertTrue(alert.title().contains("+5 Dexterity Vest"));
    assertTrue(alert.message().contains("+5 Dexterity Vest"));
  }

  @Test
  void should_publishQualityThresholdAlert_when_updateQualityReducesQualityBelowCritical() {
    // Arrange - Normal item at quality 11 becomes 10 (hits critical threshold)
    // Normal items decrease by 1 per day while sellIn is positive
    List<Item> items = List.of(new Item("+5 Dexterity Vest", 5, 11));
    gildedRose = new GildedRose(items, eventPublisher);

    // Act
    gildedRose.updateQuality();

    // Assert
    verify(messagingTemplate).convertAndSend(eq("/topic/quality.threshold"), captor.capture());
    var alert = captor.getValue();
    assertEquals("DANGER", alert.severity());
    assertTrue(alert.title().contains("Dexterity Vest"));
    assertTrue(alert.message().contains("critical"));
  }
}
