package com.gildedrose.api;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI configuration for the Gilded Rose REST API.
 *
 * <p>Provides OpenAPI 3.0 specification metadata, enabling automatic Swagger UI generation and
 * external API documentation generation via tools like OpenAPI Generator.
 */
@Configuration
public class OpenApiConfig {

  /**
   * Defines the OpenAPI bean with API metadata.
   *
   * @return an OpenAPI instance configured with title, version, description, contact, and license
   */
  @Bean
  public OpenAPI gilderRoseOpenApi() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Gilded Rose Shop API")
                .version("1.0.0")
                .description(
                    "REST API for managing Gilded Rose shop inventory, prices, and daily updates. "
                        + "Provides endpoints to list items, update item qualities by advancing days, "
                        + "retrieve item prices, and simulate future item states without mutation.")
                .contact(new Contact().name("Gilded Rose Shop").url("https://example.com"))
                .license(new License().name("MIT").url("https://opensource.org/licenses/MIT")));
  }
}
