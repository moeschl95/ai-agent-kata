package com.gildedrose.infrastructure.event;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import com.gildedrose.domain.event.ItemExpiredEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@SpringBootTest
class ItemExpiredEventListenerTest {

  @Autowired private ItemExpiredEventListener listener;

  @MockBean private SimpMessagingTemplate messagingTemplate;

  private ArgumentCaptor<AlertMessage> captor;

  @BeforeEach
  void setUp() {
    captor = ArgumentCaptor.forClass(AlertMessage.class);
  }

  @Test
  void should_publishCriticalAlert_when_itemExpiredEventFires() {
    // Arrange
    var itemName = "Test Item";
    var event = new ItemExpiredEvent(itemName);

    // Act
    listener.onItemExpired(event);

    // Assert
    verify(messagingTemplate).convertAndSend(eq("/topic/item.expired"), captor.capture());
    var alert = captor.getValue();
    assertEquals("CRITICAL", alert.severity());
    assertEquals("Item Expired: " + itemName, alert.title());
    assertTrue(alert.message().contains(itemName));
    assertTrue(alert.message().contains("quality 0"));
  }

  @Test
  void should_includeTimestamp_when_publishingAlert() {
    // Arrange
    var itemName = "Aged Brie";
    var event = new ItemExpiredEvent(itemName);
    var beforeTime = System.currentTimeMillis();

    // Act
    listener.onItemExpired(event);

    // Assert
    verify(messagingTemplate).convertAndSend(eq("/topic/item.expired"), captor.capture());
    var alert = captor.getValue();
    assertTrue(alert.timestamp() >= beforeTime);
    assertTrue(alert.timestamp() <= System.currentTimeMillis() + 1000);
  }
}
