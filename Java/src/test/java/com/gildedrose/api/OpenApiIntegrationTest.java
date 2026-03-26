package com.gildedrose.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for OpenAPI specification endpoint and Swagger UI.
 *
 * Verifies that the /v3/openapi.json endpoint returns a valid OpenAPI specification
 * and confirms that all ShopController endpoints are documented.
 */
@SpringBootTest
@AutoConfigureMockMvc
class OpenApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Verifies that the OpenAPI spec endpoint returns HTTP 200 with a valid JSON response.
     */
    @Test
    void should_returnOpenApiJson_when_v3OpenApiJsonEndpointIsCalled() throws Exception {
        mockMvc.perform(get("/v3/openapi.json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.openapi", notNullValue()));
    }

    /**
     * Verifies that the OpenAPI spec contains the correct API version.
     */
    @Test
    void should_containCorrectApiVersion_when_openApiSpecIsGenerated() throws Exception {
        mockMvc.perform(get("/v3/openapi.json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.version", equalTo("1.0.0")));
    }

    /**
     * Verifies that the OpenAPI spec contains the correct API title.
     */
    @Test
    void should_containApiTitle_when_openApiSpecIsGenerated() throws Exception {
        mockMvc.perform(get("/v3/openapi.json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title", equalTo("Gilded Rose Shop API")));
    }

    /**
     * Verifies that the OpenAPI spec contains API description.
     */
    @Test
    void should_containApiDescription_when_openApiSpecIsGenerated() throws Exception {
        mockMvc.perform(get("/v3/openapi.json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.description", notNullValue()))
                .andExpect(jsonPath("$.info.description", containsString("REST API for managing Gilded Rose")));
    }

    /**
     * Verifies that the OpenAPI spec includes contact information.
     */
    @Test
    void should_includeContactInfo_when_openApiSpecIsGenerated() throws Exception {
        mockMvc.perform(get("/v3/openapi.json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.contact", notNullValue()))
                .andExpect(jsonPath("$.info.contact.name", equalTo("Gilded Rose Shop")));
    }

    /**
     * Verifies that the OpenAPI spec includes license information.
     */
    @Test
    void should_includeLicenseInfo_when_openApiSpecIsGenerated() throws Exception {
        mockMvc.perform(get("/v3/openapi.json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.license", notNullValue()))
                .andExpect(jsonPath("$.info.license.name", equalTo("MIT")));
    }

    /**
     * Verifies that the OpenAPI spec documents the GET /api/items endpoint.
     */
    @Test
    void should_documentGetItemsEndpoint_when_openApiSpecIsGenerated() throws Exception {
        mockMvc.perform(get("/v3/openapi.json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/items']", notNullValue()))
                .andExpect(jsonPath("$.paths['/api/items'].get", notNullValue()));
    }

    /**
     * Verifies that the OpenAPI spec documents the POST /api/items/advance-day endpoint.
     */
    @Test
    void should_documentAdvanceDayEndpoint_when_openApiSpecIsGenerated() throws Exception {
        mockMvc.perform(get("/v3/openapi.json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/items/advance-day']", notNullValue()))
                .andExpect(jsonPath("$.paths['/api/items/advance-day'].post", notNullValue()));
    }

    /**
     * Verifies that the OpenAPI spec documents the GET /api/items/{name}/price endpoint.
     */
    @Test
    void should_documentGetPriceEndpoint_when_openApiSpecIsGenerated() throws Exception {
        mockMvc.perform(get("/v3/openapi.json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/items/{name}/price']", notNullValue()))
                .andExpect(jsonPath("$.paths['/api/items/{name}/price'].get", notNullValue()));
    }

    /**
     * Verifies that the OpenAPI spec documents the GET /api/items/{name}/projection endpoint.
     */
    @Test
    void should_documentGetProjectionEndpoint_when_openApiSpecIsGenerated() throws Exception {
        mockMvc.perform(get("/v3/openapi.json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/items/{name}/projection']", notNullValue()))
                .andExpect(jsonPath("$.paths['/api/items/{name}/projection'].get", notNullValue()));
    }

    /**
     * Verifies that the OpenAPI spec documents the GET /api/items/projection endpoint.
     */
    @Test
    void should_documentBulkProjectionEndpoint_when_openApiSpecIsGenerated() throws Exception {
        mockMvc.perform(get("/v3/openapi.json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/items/projection']", notNullValue()))
                .andExpect(jsonPath("$.paths['/api/items/projection'].get", notNullValue()));
    }

    /**
     * Verifies that the GET /api/items endpoint has operation summary and description.
     */
    @Test
    void should_documentGetItemsOperationDetails_when_openApiSpecIsGenerated() throws Exception {
        mockMvc.perform(get("/v3/openapi.json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/items'].get.summary", notNullValue()))
                .andExpect(jsonPath("$.paths['/api/items'].get.description", notNullValue()));
    }

    /**
     * Verifies that the OpenAPI spec marks endpoints with a tag to organize them.
     */
    @Test
    void should_tagEndpointUnderItemsTag_when_openApiSpecIsGenerated() throws Exception {
        mockMvc.perform(get("/v3/openapi.json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/items'].get.tags", notNullValue()))
                .andExpect(jsonPath("$.paths['/api/items'].get.tags[0]", equalTo("Items")));
    }


}
