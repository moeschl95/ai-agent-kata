package com.gildedrose.infrastructure.event;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.gildedrose.domain.event.ItemQualityChangedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@SpringBootTest
class QualityThresholdListenerTest {

  @Autowired private QualityThresholdListener listener;

  @MockBean private SimpMessagingTemplate messagingTemplate;

  private ArgumentCaptor<AlertMessage> captor;

  @BeforeEach
  void setUp() {
    captor = ArgumentCaptor.forClass(AlertMessage.class);
  }

  @Test
  void should_publishWarningAlert_when_qualityCrossesWarningThreshold() {
    // Arrange
    var itemName = "Aged Brie";
    var event = new ItemQualityChangedEvent(itemName, 26, 25);

    // Act
    listener.onItemQualityChanged(event);

    // Assert
    verify(messagingTemplate).convertAndSend(eq("/topic/quality.threshold"), captor.capture());
    var alert = captor.getValue();
    assertEquals("WARNING", alert.severity());
    assertTrue(alert.title().contains(itemName));
    assertTrue(alert.message().contains("25"));
  }

  @Test
  void should_publishDangerAlert_when_qualityCrossesCriticalThreshold() {
    // Arrange
    var itemName = "Backstage pass";
    var event = new ItemQualityChangedEvent(itemName, 11, 10);

    // Act
    listener.onItemQualityChanged(event);

    // Assert
    verify(messagingTemplate).convertAndSend(eq("/topic/quality.threshold"), captor.capture());
    var alert = captor.getValue();
    assertEquals("DANGER", alert.severity());
    assertTrue(alert.title().contains(itemName));
    assertTrue(alert.message().contains("10"));
  }

  @Test
  void should_notPublishAlert_when_qualityStaysWithinWarningZone() {
    // Arrange — quality drops from 24 to 23, both in warning zone
    var itemName = "Normal Item";
    var event = new ItemQualityChangedEvent(itemName, 24, 23);

    // Act
    listener.onItemQualityChanged(event);

    // Assert
    verify(messagingTemplate, never())
        .convertAndSend(eq("/topic/quality.threshold"), any(AlertMessage.class));
  }

  @Test
  void should_notPublishAlert_when_qualityStaysBelowCriticalThreshold() {
    // Arrange — quality drops from 8 to 7, both below critical threshold
    var itemName = "Old Item";
    var event = new ItemQualityChangedEvent(itemName, 8, 7);

    // Act
    listener.onItemQualityChanged(event);

    // Assert
    verify(messagingTemplate, never())
        .convertAndSend(eq("/topic/quality.threshold"), any(AlertMessage.class));
  }

  @Test
  void should_includeTimestamp_when_publishingAlert() {
    // Arrange
    var itemName = "Test Item";
    var event = new ItemQualityChangedEvent(itemName, 26, 25);
    var beforeTime = System.currentTimeMillis();

    // Act
    listener.onItemQualityChanged(event);

    // Assert
    verify(messagingTemplate).convertAndSend(eq("/topic/quality.threshold"), captor.capture());
    var alert = captor.getValue();
    assertTrue(alert.timestamp() >= beforeTime);
    assertTrue(alert.timestamp() <= System.currentTimeMillis() + 1000);
  }

  @Test
  void should_publishWarningAlert_when_qualityIncreasesAndCrossesThreshold() {
    // Arrange — quality increases from 24 to 26 (crosses warning threshold going up)
    // This should trigger warning since it crosses the threshold
    var itemName = "Aged Brie";
    var event = new ItemQualityChangedEvent(itemName, 24, 26);

    // Act
    listener.onItemQualityChanged(event);

    // Assert — should NOT publish (only crossing down triggers alert)
    verify(messagingTemplate, never())
        .convertAndSend(eq("/topic/quality.threshold"), any(AlertMessage.class));
  }
}
