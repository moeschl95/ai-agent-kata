package com.gildedrose.infrastructure.event;

import com.gildedrose.domain.event.DayAdvancedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/** Listener that publishes success alerts when a day is advanced. */
@Component
public class DayAdvancedEventListener {

  private final SimpMessagingTemplate messagingTemplate;

  public DayAdvancedEventListener(SimpMessagingTemplate messagingTemplate) {
    this.messagingTemplate = messagingTemplate;
  }

  @EventListener(DayAdvancedEvent.class)
  public void onDayAdvanced(DayAdvancedEvent event) {
    int itemCount = event.updatedItems().size();
    String itemText = itemCount == 1 ? "1 item" : itemCount + " items";

    var alertMessage =
        new AlertMessage(
            "SUCCESS",
            "Day Advanced",
            "The shop advanced by one day. Updated " + itemText + ".",
            System.currentTimeMillis());

    messagingTemplate.convertAndSend("/topic/day.advanced", alertMessage);
  }
}
