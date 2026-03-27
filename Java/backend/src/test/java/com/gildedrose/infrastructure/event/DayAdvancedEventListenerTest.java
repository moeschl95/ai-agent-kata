package com.gildedrose.infrastructure.event;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import com.gildedrose.domain.event.DayAdvancedEvent;
import com.gildedrose.domain.model.Item;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@SpringBootTest
class DayAdvancedEventListenerTest {

  @Autowired private DayAdvancedEventListener listener;

  @MockBean private SimpMessagingTemplate messagingTemplate;

  private ArgumentCaptor<AlertMessage> captor;

  @BeforeEach
  void setUp() {
    captor = ArgumentCaptor.forClass(AlertMessage.class);
  }

  @Test
  void should_publishSuccessAlert_when_dayAdvancedEventFires() {
    // Arrange
    List<Item> items = List.of(new Item("Test Item", 5, 10));
    var event = new DayAdvancedEvent(items);

    // Act
    listener.onDayAdvanced(event);

    // Assert
    verify(messagingTemplate).convertAndSend(eq("/topic/day.advanced"), captor.capture());
    var alert = captor.getValue();
    assertEquals("SUCCESS", alert.severity());
    assertTrue(alert.title().contains("Day Advanced"));
    assertTrue(alert.message().contains("1 item"));
  }

  @Test
  void should_includeItemCount_when_publishingDayAdvancedAlert() {
    // Arrange
    List<Item> items =
        List.of(new Item("Item 1", 5, 10), new Item("Item 2", 3, 20), new Item("Item 3", 10, 15));
    var event = new DayAdvancedEvent(items);

    // Act
    listener.onDayAdvanced(event);

    // Assert
    verify(messagingTemplate).convertAndSend(eq("/topic/day.advanced"), captor.capture());
    var alert = captor.getValue();
    assertTrue(alert.message().contains("3 items"));
  }

  @Test
  void should_includeTimestamp_when_publishingDayAdvancedAlert() {
    // Arrange
    List<Item> items = List.of(new Item("Test Item", 5, 10));
    var event = new DayAdvancedEvent(items);
    var beforeTime = System.currentTimeMillis();

    // Act
    listener.onDayAdvanced(event);

    // Assert
    verify(messagingTemplate).convertAndSend(eq("/topic/day.advanced"), captor.capture());
    var alert = captor.getValue();
    assertTrue(alert.timestamp() >= beforeTime);
    assertTrue(alert.timestamp() <= System.currentTimeMillis() + 1000);
  }
}
