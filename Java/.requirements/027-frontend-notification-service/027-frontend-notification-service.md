# 027 — Frontend Notification Service

**Status:** ready-for-development

---

## Description

Create an Angular service (`NotificationService`) that manages STOMP WebSocket connections and incoming alert messages. The service subscribes to backend STOMP topics (`/topic/item.expired`, `/topic/quality.threshold`), parses incoming alert messages, and exposes them via a reactive `alerts$` observable. This task handles all WebSocket plumbing at the service level; UI rendering is handled by task 028.

---

## Implementation Plan

1. **Install dependency**: In `frontend/`, run `npm install @stomp/stompjs --legacy-peer-deps`.
2. **Create NotificationService**: Create `frontend/src/app/core/services/notification.service.ts` with:
   - Define `Alert` interface with fields: `id` (string), `severity` (enum: "INFO" | "WARNING" | "DANGER" | "CRITICAL"), `title` (string), `message` (string), `timestamp` (number).
   - Create `NotificationService` with `providedIn: 'root'` decorator.
   - Private `stompClient: Client` field.
   - Public `alerts$: BehaviorSubject<Alert[]>` initialized to empty array.
   - Constructor calls `initializeWebSocket()`.
   - Implement `initializeWebSocket()`:
     - Set `brokerURL` to `ws://${window.location.host}/ws/alerts`.
     - Set `onConnect` callback that subscribes to `/topic/item.expired` and `/topic/quality.threshold` using `stompClient.subscribe()`.
     - Each subscription calls `handleAlert()` with the message body.
     - Set `onStompError` callback to log errors to console.
     - Call `stompClient.activate()`.
   - Implement `handleAlert(body: string)`:
     - Parse JSON from body as alert data.
     - Create `Alert` object with unique `id` (use `Date.now().toString()`).
     - Push alert to `alerts$` BehaviorSubject.
     - Use `setTimeout` to auto-remove alert after 8 seconds by calling `removeAlert(id)`.
   - Implement `removeAlert(id: string)`:
     - Filter out alert from `alerts$` by id.
3. **Verify dependencies**: Ensure `@angular/core`, `rxjs`, and `@stomp/stompjs` versions are correct (Angular 19+).
4. **Write test**: Create `frontend/src/app/core/services/notification.service.spec.ts` with:
   - Test that `alerts$` emits empty array on init.
   - Test that `handleAlert()` parses JSON and adds alert to `alerts$`.
   - Test that `removeAlert()` removes an alert by id.
   - (Note: Full STOMP client mocking may be complex; focus on service logic. STOMP integration is tested in task 029.)

---

## Acceptance Criteria

- [ ] `@stomp/stompjs` is installed in `frontend/package.json`.
- [ ] `NotificationService` is created with `providedIn: 'root'` decorator.
- [ ] `Alert` interface is exported with `id`, `severity`, `title`, `message`, `timestamp` fields.
- [ ] `alerts$` is a public `BehaviorSubject<Alert[]>` initialized to `[]`.
- [ ] STOMP client connects to `/ws/alerts` endpoint on initialization.
- [ ] Service subscribes to `/topic/item.expired` and `/topic/quality.threshold` topics.
- [ ] `handleAlert()` parses incoming JSON and creates an `Alert` object.
- [ ] Alerts are pushed to `alerts$` observable.
- [ ] Alerts are auto-removed after 8 seconds via `setTimeout`.
- [ ] `removeAlert()` removes an alert by id from `alerts$`.
- [ ] Unit tests verify service logic (observable emissions, alert parsing, removal).
- [ ] All existing frontend tests still pass.

---

## Notes

- This task depends on 024 (backend WebSocket infrastructure) being deployed.
- The service is a pure presentational layer — it only manages subscriptions and observable state. The UI component (task 028) consumes this service.
- Error handling in `onStompError` is minimal (console.log) at this stage; enhanced logging can be added later.
- The 8-second auto-dismiss timeout is configurable; the duration is arbitrary and can be adjusted based on UX feedback.

---

## Changelog

| Date | Status | Note |
|------|--------|------|
| 2026-03-27 | funnel | Task created |
| 2026-03-27 | ready-for-development | Approved for implementation |
