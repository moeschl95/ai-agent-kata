# 025 — Item Expired Event Listener

**Status:** ready-for-development

---

## Description

Create an event listener that subscribes to `ItemExpiredEvent` and publishes a critical alert via the STOMP message broker to `/topic/item.expired`. When an item's quality reaches zero, a red alert is sent to all connected frontend clients with the item name. This task is independent of the quality threshold listener (task 026) but depends on the WebSocket infrastructure from task 024.

---

## Implementation Plan

1. **Create ItemExpiredEventListener**: Create `backend/src/main/java/com/gildedrose/infrastructure/event/ItemExpiredEventListener.java` with:
   - Inject `SimpMessagingTemplate` via constructor.
   - Annotate with `@Component` and `@EventListener(ItemExpiredEvent.class)`.
   - In the event handler method, construct an `AlertMessage` with:
     - `severity = "CRITICAL"`
     - `title = "Item Expired: " + event.itemName()`
     - `message = "The item '" + event.itemName() + "' has reached quality 0 and is no longer saleable."`
     - `timestamp = System.currentTimeMillis()`
   - Use `messagingTemplate.convertAndSend("/topic/item.expired", alertMessage)` to publish.
2. **Write test**: Create `backend/src/test/java/com/gildedrose/infrastructure/event/ItemExpiredEventListenerTest.java` with:
   - Mock `SimpMessagingTemplate`.
   - Test that when `ItemExpiredEvent` is fired, `convertAndSend` is called with `/topic/item.expired` and an `AlertMessage` with severity "CRITICAL".
   - Verify the message contains the correct item name and timestamp is set.
3. **Verify build**: Ensure `gradlew test` passes.

---

## Acceptance Criteria

- [ ] `ItemExpiredEventListener` class exists with `@Component` and `@EventListener(ItemExpiredEvent.class)` annotations.
- [ ] `SimpMessagingTemplate` is injected via constructor.
- [ ] When `ItemExpiredEvent` fires, the listener publishes an `AlertMessage` to `/topic/item.expired`.
- [ ] `AlertMessage.severity()` is "CRITICAL".
- [ ] `AlertMessage.title()` includes the item name.
- [ ] `AlertMessage.message()` is descriptive and mentions quality 0.
- [ ] `AlertMessage.timestamp()` is set to current time.
- [ ] Unit tests verify the listener behavior with a mocked `SimpMessagingTemplate`.
- [ ] All existing tests still pass.

---

## Notes

- This task depends on 024 (WebSocket infrastructure).
- The listener is independent of task 026 (quality threshold listener); both can be developed in parallel.
- Do not worry about STOMP subscription confirmation in this task — that is frontend responsibility.

---

## Changelog

| Date | Status | Note |
|------|--------|------|
| 2026-03-27 | funnel | Task created |
| 2026-03-27 | ready-for-development | Approved for implementation |
