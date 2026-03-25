# 016 — OpenAPI REST Specification

**Status:** ready-for-development

---

## Description

The Gilded Rose shop REST API (built in task 004) currently lacks formal API documentation. Clients must read the Spring Controller source code to understand available endpoints, parameter names, response schemas, and HTTP status codes. This task adds an OpenAPI 3.0 specification that auto-generates from the Spring Boot application, including interactive Swagger UI for easy exploration and code generation support.

---

## Implementation Plan

1. **Add springdoc-openapi dependency** to `build.gradle.kts`:
   - Add `org.springdoc:springdoc-openapi-starter-webmvc-ui:2.x` (or latest stable) to enable automatic OpenAPI schema generation and Swagger UI.
   - Run `./gradlew dependencies` to verify the dependency resolves.

2. **Configure OpenAPI bean and metadata** in a new `OpenApiConfig.java` (in the `api` package):
   - Create a `@Configuration` class with a `@Bean` method returning `OpenAPI` (from `io.swagger.v3.oas.models`).
   - Set API title: "Gilded Rose Shop API".
   - Set version to match the app version (e.g. "1.0.0").
   - Add a description: "REST API for managing Gilded Rose shop inventory, prices, and daily updates."
   - Define contact info (optional: name, email, url).
   - Define license (optional: e.g. MIT).
   - Optionally add a base server URL if needed (though Spring auto-detects localhost).

3. **Annotate `ShopController.java`** with OpenAPI / Swagger decorators:
   - Add `@Tag(name = "Items", description = "Shop inventory operations")` to the class.
   - Add `@Operation` and `@ApiResponse` annotations to each method:
     - `GET /items` — `@Operation(summary = "List all shop items")`, `@ApiResponse(responseCode = "200", ...)`.
     - `POST /items/advance-day` — `@Operation(summary = "Advance shop by one day")`, `@ApiResponse(responseCode = "200", ...)`.
     - `GET /items/{name}/price` — `@Operation(summary = "Get price for item")`, `@ApiResponse(responseCode = "200", ..., `@ApiResponse(responseCode = "404", description = "Item not found")`.
   - Add `@Parameter` annotations to method parameters (e.g. `@Parameter(description = "Item name")` for `@PathVariable String name`).

4. **Verify Swagger UI endpoint** — once deployed, the OpenAPI spec is served at `/v3/openapi.json` and Swagger UI is available at `/swagger-ui.html`.

5. **Write a small integration test** (in `ShopControllerTest` or a new `OpenApiIntegrationTest`):
   - `GET /v3/openapi.json` returns HTTP 200 with a valid OpenAPI JSON document.
   - The JSON contains the expected schema version (`3.0.x` or `3.1.x`).
   - Verify that all endpoints in `ShopController` appear in the spec (basic validation — no need to parse every detail).

6. **Run all tests (Green)** — ensure `./gradlew test` passes with the new integration test included.

7. **Refactor if needed** — ensure annotations are descriptive but not verbose; extract common response documentation if multiple endpoints share patterns.

---

## Acceptance Criteria

- [ ] `org.springdoc:springdoc-openapi-starter-webmvc-ui` dependency is added to `build.gradle.kts`.
- [ ] `OpenApiConfig.java` exists and defines the API title, version, description, and contact info.
- [ ] `ShopController` is annotated with `@Tag`, `@Operation`, `@ApiResponse`, and `@Parameter` decorators describing all endpoints.
- [ ] `GET /v3/openapi.json` returns a valid OpenAPI 3.0.x JSON document with all shop endpoints documented.
- [ ] Swagger UI is accessible at `http://localhost:8080/swagger-ui.html` (when the app runs).
- [ ] An integration test verifies the OpenAPI endpoint returns HTTP 200 with a valid spec.
- [ ] All existing unit and integration tests continue to pass.
- [ ] `Item.java` and the core domain logic remain unchanged.
- [ ] The OpenAPI spec is human-readable and includes endpoint descriptions, parameter names, and response examples.

---

## Notes

- **Swagger UI and Actuator**: Springdoc automatically integrates with Spring Boot's `/actuator` endpoint structure; no additional Actuator configuration is required unless custom metrics are desired.
- **Response schemas**: Springdoc infers JSON response schemas from `ItemDto` fields at compile-time, so explicit `@Schema` annotations on `ItemDto` (e.g. `@Schema(description = "Item name")`) are optional but recommended for clarity.
- **API versioning**: The current implementation uses no `/v1/` prefix (e.g. endpoints are `/items`, not `/api/v1/items`). If versioning is added in future tasks, the OpenAPI spec will automatically reflect it via Spring's context path configuration.
- **Code generation**: Once the OpenAPI spec is live, clients can generate SDK code (Python, JavaScript, TypeScript, etc.) using tools like OpenAPI Generator, which reads `/v3/openapi.json` directly.

---

## Changelog

| Date | Status | Note |
|------|--------|------|
| 2026-03-25 | funnel | Task created |
| 2026-03-25 | ready-for-development | Approved by user |
