package com.gildedrose.infrastructure.event;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlertMessageTest {

  @Test
  void should_createAlertMessage_when_allFieldsProvided() {
    var severity = "WARNING";
    var title = "Low Quality";
    var message = "Item quality is below threshold";
    var timestamp = System.currentTimeMillis();

    var alertMessage = new AlertMessage(severity, title, message, timestamp);

    assertEquals(severity, alertMessage.severity());
    assertEquals(title, alertMessage.title());
    assertEquals(message, alertMessage.message());
    assertEquals(timestamp, alertMessage.timestamp());
  }

  @Test
  void should_supportEquality_when_sameFieldValues() {
    var timestamp = System.currentTimeMillis();
    var alert1 = new AlertMessage("WARNING", "Low Quality", "Item quality low", timestamp);
    var alert2 = new AlertMessage("WARNING", "Low Quality", "Item quality low", timestamp);

    assertEquals(alert1, alert2);
  }

  @Test
  void should_supportToString_when_called() {
    var timestamp = System.currentTimeMillis();
    var alert = new AlertMessage("CRITICAL", "Critical Quality", "Item expired", timestamp);

    var stringRepresentation = alert.toString();

    assertTrue(stringRepresentation.contains("CRITICAL"));
    assertTrue(stringRepresentation.contains("Critical Quality"));
  }
}
