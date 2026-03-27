package com.gildedrose.infrastructure.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class WebSocketConfigTest {

  @Autowired(required = false)
  private WebSocketConfig webSocketConfig;

  @Test
  void should_haveWebSocketConfigBean_when_bootstrapped() {
    assertNotNull(webSocketConfig);
  }

  @Test
  void should_beConfiguredWithAnnotations_when_classDecorated() {
    var configClass = WebSocketConfig.class;
    var hasConfigAnnotation =
        configClass.isAnnotationPresent(org.springframework.context.annotation.Configuration.class);
    var hasEnableWebSocketAnnotation =
        configClass.isAnnotationPresent(
            org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker.class);

    assert hasConfigAnnotation;
    assert hasEnableWebSocketAnnotation;
  }
}
