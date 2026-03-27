package com.gildedrose;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.gildedrose.domain.service.DefaultPricingService;
import com.gildedrose.domain.service.GildedRose;
import com.gildedrose.domain.service.PricingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class GildedRoseApplicationTest {

  @Autowired private ApplicationContext applicationContext;

  @Test
  void should_loadApplicationContext_when_springApplicationStarts() {
    assertNotNull(applicationContext);
  }

  @Test
  void should_havGildedRoseBean_when_applicationContextLoads() {
    GildedRose gildedRose = applicationContext.getBean(GildedRose.class);
    assertNotNull(gildedRose);
  }

  @Test
  void should_havePricingServiceBean_when_applicationContextLoads() {
    PricingService pricingService = applicationContext.getBean(PricingService.class);
    assertNotNull(pricingService);
  }

  @Test
  void should_configureDefaultPricingService_when_applicationContextLoads() {
    DefaultPricingService pricingService = applicationContext.getBean(DefaultPricingService.class);
    assertNotNull(pricingService);
    // Verify the bean has been instantiated with configured properties
    assertEquals(999, pricingService.getLegendaryPrice());
    assertEquals(0.5, pricingService.getExpiredDiscountRate());
  }
}
