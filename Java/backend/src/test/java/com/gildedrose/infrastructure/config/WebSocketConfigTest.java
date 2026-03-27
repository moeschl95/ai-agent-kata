package com.gildedrose.infrastructure.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import static org.junit.jupiter.api.Assertions.assertNotNull;

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
        configClass.isAnnotationPresent(
            org.springframework.context.annotation.Configuration.class);
    var hasEnableWebSocketAnnotation =
        configClass.isAnnotationPresent(
            org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker.class);

    assert hasConfigAnnotation;
    assert hasEnableWebSocketAnnotation;
  }
}
