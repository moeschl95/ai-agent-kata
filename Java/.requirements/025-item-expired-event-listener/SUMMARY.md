# 025 — Item Expired Event Listener — Implementation Summary

**Date:** 2026-03-27
**Model:** Claude Haiku 4.5

---

## What Was Implemented

An event-driven listener was created to subscribe to `ItemExpiredEvent` domain events and publish critical alerts via the STOMP message broker to `/topic/item.expired`. The `ItemExpiredEventListener` class uses Spring's `@EventListener` annotation to react to item expiration events and sends real-time alerts to all connected WebSocket clients when items reach quality zero.

---

## Problems Addressed During Development

- **Mockito matcher syntax**: Initial test implementation used complexity with `ArgumentMatchers.argThat()` which caused misusing matcher exceptions. Simplified by using `@Captor` field annotation with `BeforeEach` setup to properly capture and assert on published alerts.
- **Table formatting**: Initial Changelog update had malformed table syntax due to line concatenation. Fixed by rewriting the entire Changelog table with proper column alignment.
- **Clean separation of concerns**: The listener receives domain events and translates them to infrastructure messaging without coupling the domain layer to WebSocket concerns; follows Dependency Inversion Principle.

---

## Files Changed

- `backend/src/main/java/com/gildedrose/infrastructure/event/ItemExpiredEventListener.java` — Created new listener component that publishes critical alerts to `/topic/item.expired`
- `backend/src/test/java/com/gildedrose/infrastructure/event/ItemExpiredEventListenerTest.java` — Created unit tests with mocked `SimpMessagingTemplate` using `ArgumentCaptor` to verify alert publishing behavior
- `build.gradle.kts` — No changes (WebSocket dependency already added in task 024)
- `backend/src/main/resources/application.properties` — No changes (threshold properties already added in task 024)
