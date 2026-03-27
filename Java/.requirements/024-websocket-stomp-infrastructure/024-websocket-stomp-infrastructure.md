# 024 — WebSocket STOMP Infrastructure

**Status:** done

---

## Description

Set up Spring WebSocket with STOMP message broker infrastructure to enable real-time event publishing. This task creates the foundation for bidirectional communication between the backend and Angular frontend, allowing the server to push alerts to clients without polling. Includes configuration, dependencies, and the `AlertMessage` data transfer object.

---

## Implementation Plan

1. **Add dependency**: Add `spring-boot-starter-websocket` to `build.gradle.kts`.
2. **Create WebSocketConfig**: Create `backend/src/main/java/com/gildedrose/infrastructure/config/WebSocketConfig.java` implementing Spring's `WebSocketMessageBrokerConfigurer`. Enable in-memory STOMP broker for `/topic/*` destinations and register WebSocket endpoint at `/ws/alerts`.
3. **Create AlertMessage DTO**: Create `backend/src/main/java/com/gildedrose/infrastructure/event/AlertMessage.java` as a record with fields: `severity` (String), `title` (String), `message` (String), `timestamp` (long).
4. **Add configuration properties**: Add threshold properties to `backend/src/main/resources/application.properties`:
   - `shop.quality.threshold.warning=25`
   - `shop.quality.threshold.critical=10`
5. **Verify build**: Ensure `gradlew test` passes after changes.

---

## Acceptance Criteria

- [ ] `spring-boot-starter-websocket` dependency is declared in `build.gradle.kts`.
- [ ] `WebSocketConfig` class exists, is annotated with `@Configuration` and `@EnableWebSocketMessageBroker`, and implements the `WebSocketMessageBrokerConfigurer` interface.
- [ ] Message broker is configured to enable simple broker on `/topic/*` with application destination prefix `/app`.
- [ ] WebSocket endpoint `/ws/alerts` is registered with public origin access.
- [ ] `AlertMessage` record exists with `severity`, `title`, `message`, and `timestamp` fields.
- [ ] Configuration properties for `shop.quality.threshold.warning` and `shop.quality.threshold.critical` are set in `application.properties`.
- [ ] All existing tests still pass.

---

## Notes

- Junior developers should follow the Spring documentation pattern for WebSocket configuration.
- The in-memory broker is appropriate for a single-server kata and can be upgraded to RabbitMQ/ActiveMQ later if needed.
- No client-side listener code is needed for this task; the foundation is being laid for downstream tasks.

---

## Changelog

| Date | Status | Note |
|------|--------|------|
| 2026-03-27 | funnel | Task created |
| 2026-03-27 | ready-for-development | Approved for implementation |
| 2026-03-27 | in-progress | Implementation started |
| 2026-03-27 | implemented | Infrastructure complete: WebSocket endpoint, STOMP broker, and AlertMessage DTO; all tests passing |
| 2026-03-27 | done | Accepted by user |
