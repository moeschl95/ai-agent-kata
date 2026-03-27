# 028 — Clarity Alert Container Component

**Status:** done

---

## Description

Create an Angular component (`AlertContainerComponent`) that renders real-time alerts using Clarity's native alert component. The component subscribes to the `NotificationService.alerts$` observable and displays incoming alerts in a fixed stack at the top-right corner of the browser with smooth slide-in animations. This is the final UI layer that users see; it consumes the notification service from task 027.

---

## Implementation Plan

1. **Generate component**: Using Angular CLI, create a new component:
   ```
   ng generate component shared/components/alert-container
   ```
   (Or manually create the files if CLI is unavailable.)
2. **Implement AlertContainerComponent**:
   - Inject `NotificationService` via constructor.
   - In `ngOnInit()`, subscribe to `notificationService.alerts$` and assign to component property `alerts`.
   - In template, use `*ngFor="let alert of alerts"` to iterate.
   - For each alert, render `<clr-alert>` with:
     - `[clrAlertType]` bound to result of `getAlertType(alert.severity)` method.
     - `[closable]="true"` to show close button.
     - `(clrAlertClosed)` event binding to `dismissAlert(alert.id)` method.
   - Inside `<clr-alert-item>`, render:
     - `<strong>{{ alert.title }}</strong>` followed by newline.
     - `{{ alert.message }}`.
   - Implement `getAlertType(severity: string): string`:
     - Map "CRITICAL" and "DANGER" to "error" (red alert).
     - Map "WARNING" to "warning" (yellow alert).
     - Map "INFO" to "info" (blue alert).
   - Implement `dismissAlert(id: string)`:
     - Call `notificationService.removeAlert(id)`.
3. **Add styles**: In component's SCSS file:
   - `.alert-stack`: Position fixed at top-right (20px from top and right), max-width 400px, z-index 9999.
   - `clr-alert`: Flex column, gap 10px, add CSS `@keyframes slideIn` animation (translateX from 400px to 0, opacity 0 to 1, 0.3s ease-out).
4. **Add to app layout**: In `frontend/src/app/app.component.html`, add `<app-alert-container></app-alert-container>` before `<router-outlet>` so alerts appear above all pages.
5. **Verify build**: Ensure `npm run build` and `npm run test` both pass.

---

## Acceptance Criteria

- [ ] `AlertContainerComponent` exists in `frontend/src/app/shared/components/alert-container/`.
- [ ] Component imports `NotificationService` and subscribes to `alerts$` in `ngOnInit()`.
- [ ] Template uses `*ngFor` to iterate over alerts.
- [ ] Each alert is rendered as a `<clr-alert>` with `[clrAlertType]` binding.
- [ ] `getAlertType()` correctly maps severity to Clarity alert types (error/warning/info).
- [ ] Alert close button triggers `dismissAlert()` via `(clrAlertClosed)` event.
- [ ] Alert title and message are displayed in the template.
- [ ] Styles position alerts in a fixed stack at top-right (20px from edges).
- [ ] CSS animation slides alerts in from the right over 0.3s.
- [ ] Component is added to `app.component.html` at the root level.
- [ ] Unit tests verify template rendering and event bindings.
- [ ] All existing frontend tests still pass.

---

## Notes

- This task depends on 027 (NotificationService).
- Uses Clarity's `clr-alert` component which is already available in the frontend (installed in earlier tasks).
- The component is stateless relative to backend; all state is managed by `NotificationService`.
- The fixed positioning ensures alerts are always visible regardless of page scroll position.
- Animation is CSS-based (performant); no JavaScript animation libraries needed.

---

## Changelog

| Date | Status | Note |
|------|--------|------|
| 2026-03-27 | funnel | Task created |
| 2026-03-27 | ready-for-development | Approved for implementation |
| 2026-03-27 | in-progress | Implementation started |
| 2026-03-27 | implemented | AlertContainerComponent complete with 10 unit tests; all 58 frontend tests passing; npm build successful |
| 2026-03-27 | done | Approved by user; SUMMARY.md created |
