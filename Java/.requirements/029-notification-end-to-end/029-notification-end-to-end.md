# 029 — Notification End-to-End Integration

**Status:** ready-for-development

---

## Description

Integrate all components of the real-time event notification system end-to-end and verify the system works from event publication through display. This task conducts full-stack testing: trigger ItemExpiredEvent and quality threshold transitions on the backend, verify STOMP messages reach the frontend, and confirm alerts render correctly. Includes unit tests, integration tests, and manual E2E validation.

---

## Implementation Plan

1. **Backend integration tests**: Create `backend/src/test/java/com/gildedrose/infrastructure/event/EventListenerIntegrationTest.java` with:
   - Test that triggering `updateQuality()` on a shop with an item at quality 1 publishes an `ItemExpiredEvent` and the listener sends an alert to `/topic/item.expired`.
   - Test that triggering `updateQuality()` on a shop with an item at quality 26 publishes an `ItemQualityChangedEvent` and the listener sends an alert to `/topic/quality.threshold`.
   - Use a mock `SimpMessagingTemplate` to verify message publication.
   - (Note: These may be integration tests that require Spring test context.)
2. **Frontend Playwright E2E test**: Create or extend `frontend/e2e/alerts.e2e.spec.ts` (new file) with:
   - Test that demonstrates advancing a day with an item at quality 1 triggers an expired item alert on the frontend.
   - Test that advancing a day with an item at quality 26 triggers a quality threshold alert.
   - Test that clicking the close button on an alert removes it from the screen.
   - Test that alerts auto-dismiss after 8 seconds.
   - (Note: E2E tests require both backend and frontend running; may require mock STOMP for testing without real backend.)
3. **Manual verification checklist**: Create a README snippet or test notes documenting:
   - Steps to run both backend and frontend locally.
   - Manual test case: Add an item with quality 1, advance day, verify red alert appears.
   - Manual test case: Add an item with quality 26, advance day, verify yellow alert appears.
   - Manual test case: Verify alert auto-dismisses or click close button to dismiss immediately.
4. **Run full test suite**: Ensure `gradlew test` (backend) and `npm run test` (frontend) both pass with all new tests included.

---

## Acceptance Criteria

- [ ] Backend integration test exists verifying ItemExpiredEvent → STOMP publication.
- [ ] Backend integration test exists verifying ItemQualityChangedEvent → quality threshold alert publication.
- [ ] Frontend E2E test (Playwright) exists covering alert display for expired items.
- [ ] Frontend E2E test (Playwright) exists covering alert display for quality thresholds.
- [ ] Frontend E2E tests cover alert dismissal (both manual close and auto-dismiss).
- [ ] All backend tests pass: `gradlew test`.
- [ ] All frontend tests pass: `npm run test`.
- [ ] Frontend E2E tests pass (or are documented with setup requirements): `npm run test:e2e`.
- [ ] Manual test checklist is documented in task notes or a dedicated test plan.
- [ ] System demo: Running both applications together, triggering events in the UI produces visible alerts on the frontend.

---

## Notes

- This task depends on all prior tasks (024–028).
- E2E tests may require careful setup because they need both backend and frontend running. Using a test harness or Docker Compose is recommended for CI/CD.
- If full-stack E2E testing is too complex in the kata environment, focus on backend integration tests and document manual test steps clearly.
- This is the final task; once approved and completed, the entire notification system is end-to-end functional.

---

## Changelog

| Date | Status | Note |
|------|--------|------|
| 2026-03-27 | funnel | Task created |
| 2026-03-27 | ready-for-development | Approved for implementation |
