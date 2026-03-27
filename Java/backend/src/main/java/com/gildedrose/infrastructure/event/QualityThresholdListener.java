package com.gildedrose.infrastructure.event;

import com.gildedrose.domain.event.ItemQualityChangedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/** Listener that publishes threshold alerts when item quality changes. */
@Component
public class QualityThresholdListener {

  private final SimpMessagingTemplate messagingTemplate;
  private final int criticalThreshold;
  private final int warningThreshold;

  public QualityThresholdListener(
      SimpMessagingTemplate messagingTemplate,
      @Value("${shop.quality.threshold.critical}") int criticalThreshold,
      @Value("${shop.quality.threshold.warning}") int warningThreshold) {
    this.messagingTemplate = messagingTemplate;
    this.criticalThreshold = criticalThreshold;
    this.warningThreshold = warningThreshold;
  }

  @EventListener(ItemQualityChangedEvent.class)
  public void onItemQualityChanged(ItemQualityChangedEvent event) {
    // Check if quality crossed the critical threshold (going down)
    if (event.previousQuality() > criticalThreshold && event.newQuality() <= criticalThreshold) {
      publishAlert(
          event.itemName(),
          "DANGER",
          "Critical Quality Alert: " + event.itemName(),
          "The item '"
              + event.itemName()
              + "' has reached critical quality level ("
              + event.newQuality()
              + ").");
      return;
    }

    // Check if quality crossed the warning threshold (going down)
    if (event.previousQuality() > warningThreshold && event.newQuality() <= warningThreshold) {
      publishAlert(
          event.itemName(),
          "WARNING",
          "Low Quality Alert: " + event.itemName(),
          "The item '"
              + event.itemName()
              + "' quality has dropped below warning threshold ("
              + event.newQuality()
              + ").");
    }
  }

  private void publishAlert(String itemName, String severity, String title, String message) {
    var alertMessage = new AlertMessage(severity, title, message, System.currentTimeMillis());
    messagingTemplate.convertAndSend("/topic/quality.threshold", alertMessage);
  }
}
