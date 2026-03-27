package com.gildedrose.infrastructure.event;

import com.gildedrose.domain.event.ItemExpiredEvent;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/** Listener that publishes critical alerts when items expire. */
@Component
public class ItemExpiredEventListener {

  private final SimpMessagingTemplate messagingTemplate;

  public ItemExpiredEventListener(SimpMessagingTemplate messagingTemplate) {
    this.messagingTemplate = messagingTemplate;
  }

  @EventListener(ItemExpiredEvent.class)
  public void onItemExpired(ItemExpiredEvent event) {
    var alertMessage =
        new AlertMessage(
            "CRITICAL",
            "Item Expired: " + event.itemName(),
            "The item '" + event.itemName() + "' has reached quality 0 and is no longer saleable.",
            System.currentTimeMillis());

    messagingTemplate.convertAndSend("/topic/item.expired", alertMessage);
  }
}
