# 026 — Quality Threshold Event Listener

**Status:** done

---

## Description

Create an event listener that subscribes to `ItemQualityChangedEvent` and publishes quality-based threshold alerts via the STOMP message broker to `/topic/quality.threshold`. The listener monitors when item quality drops below configured thresholds (critical at 10, warning at 25) and sends appropriate severity alerts to connected clients. This task is independent of the item expiration listener (task 025) but depends on the WebSocket infrastructure from task 024.

---

## Implementation Plan

1. **Create QualityThresholdListener**: Create `backend/src/main/java/com/gildedrose/infrastructure/event/QualityThresholdListener.java` with:
   - Inject `SimpMessagingTemplate` via constructor.
   - Use `@Value` to inject `shop.quality.threshold.critical` and `shop.quality.threshold.warning` properties.
   - Annotate with `@Component` and `@EventListener(ItemQualityChangedEvent.class)`.
   - In the event handler method:
     - Check if quality crossed the warning threshold (new quality ≤ 25 AND previous quality > 25) → publish WARNING alert.
     - Check if quality crossed the critical threshold (new quality ≤ 10 AND previous quality > 10) → publish DANGER alert.
   - Construct `AlertMessage` with:
     - `severity` based on threshold crossed ("DANGER" for critical, "WARNING" for warning).
     - `title` including item name and alert type.
     - `message` indicating the threshold and current quality.
     - `timestamp = System.currentTimeMillis()`.
   - Use `messagingTemplate.convertAndSend("/topic/quality.threshold", alertMessage)` to publish.
2. **Write test**: Create `backend/src/test/java/com/gildedrose/infrastructure/event/QualityThresholdListenerTest.java` with:
   - Mock `SimpMessagingTemplate`.
   - Test that when quality drops from 26 to 25, a WARNING alert is sent.
   - Test that when quality drops from 11 to 10, a DANGER alert is sent.
   - Test that when quality drops from 24 to 23 (still in warning zone), no alert is sent.
   - Test that event is ignored if quality does not change.
   - Verify alert messages contain correct severity, item name, and quality values.
3. **Verify build**: Ensure `gradlew test` passes.

---

## Acceptance Criteria

- [ ] `QualityThresholdListener` class exists with `@Component` and `@EventListener(ItemQualityChangedEvent.class)` annotations.
- [ ] `SimpMessagingTemplate` is injected via constructor.
- [ ] `shop.quality.threshold.critical` and `shop.quality.threshold.warning` are injected via `@Value` annotations.
- [ ] When quality crosses warning threshold (≤25), a WARNING alert is published to `/topic/quality.threshold`.
- [ ] When quality crosses critical threshold (≤10), a DANGER alert is published to `/topic/quality.threshold`.
- [ ] No alert is published if quality stays within an existing zone (e.g., dropping from 24 to 23).
- [ ] `AlertMessage.title()` includes the item name and alert type.
- [ ] `AlertMessage.message()` includes the threshold value and current quality.
- [ ] Unit tests verify both threshold crossings and edge cases with mocked `SimpMessagingTemplate`.
- [ ] All existing tests still pass.

---

## Notes

- This task depends on 024 (WebSocket infrastructure).
- The listener is independent of task 025 (item expiration listener); both can be developed in parallel.
- Thresholds are configurable via `application.properties` (set in task 024).
- Only publish alerts when *crossing* thresholds, not on every quality change within a zone.

---

## Changelog

| Date | Status | Note |
|------|--------|------|
| 2026-03-27 | funnel | Task created |
| 2026-03-27 | ready-for-development | Approved for implementation |
| 2026-03-27 | in-progress | Implementation started |
| 2026-03-27 | implemented | QualityThresholdListener complete with threshold monitoring; 5 tests passing || 2026-03-27 | done | Accepted by user |