# Task 029 - Manual Verification Checklist

## Prerequisites

1. Backend running on `http://localhost:8080`
2. Frontend running on `http://localhost:4200`
3. Both applications share a WebSocket connection for real-time alerts via STOMP

## Manual Test Cases

### Test Case 1: Item Expired Alert (Quality 0)

**Setup:**
1. Start the backend: `cd backend && .\gradlew.bat bootRun`
2. Start the frontend: `cd frontend && npm start`
3. Navigate to Inventory page at `http://localhost:4200`

**Steps:**
1. In the Inventory page, locate or create an item with quality 1 (e.g., look for "Aged Brie" which starts at quality 0, or add one manually through the API)
2. Click the "Advance Day" button once
3. Watch the top-right corner of the screen for an alert

**Expected Result:**
- A red/critical alert should appear with the title "Item Expired: [Item Name]"
- The alert message should state: "The item '[Item Name]' has reached quality 0 and is no longer saleable."
- The alert should contain a timestamp of when the event occurred
- The alert should remain visible until manually closed or auto-dismissed

---

### Test Case 2: Quality Threshold Warning Alert (Quality ≤ 25)

**Setup:**
1. Ensure backend and frontend are running (see Test Case 1)
2. Navigate to Inventory page

**Steps:**
1. Locate an item with quality > 25 (most items start with quality 20 or higher)
2. Click "Advance Day" multiple times to reduce quality to 25 or below
3. Watch for an alert to appear in the top-right corner

**Expected Result:**
- A yellow/warning alert should appear when an item's quality drops from > 25 to ≤ 25
- The alert title should contain "Low Quality Alert" and the item name
- The alert message should indicate the quality has dropped below warning threshold
- The alert should display the new quality level

---

### Test Case 3: Quality Threshold Critical Alert (Quality ≤ 10)

**Setup:**
1. Ensure backend and frontend are running
2. Navigate to Inventory page

**Steps:**
1. Locate an item with quality > 10
2. Click "Advance Day" multiple times to reduce quality to 10 or below
3. Watch for an alert to appear

**Expected Result:**
- An orange/danger alert should appear when an item's quality drops from > 10 to ≤ 10
- The alert title should contain "Critical Quality Alert" and the item name
- The alert message should indicate the quality has reached critical level
- The alert severity color should be more urgent than the warning alert

---

### Test Case 4: Manual Alert Dismissal

**Setup:**
1. Trigger an alert using Test Case 1 or 2 steps
2. Do NOT wait for auto-dismiss

**Steps:**
1. Once an alert is visible, locate the close button (X icon) on the alert
2. Click the close button

**Expected Result:**
- The alert should immediately disappear from the screen
- No error should occur in the browser console
- Other alerts (if present) should remain visible

---

### Test Case 5: Automatic Alert Dismissal (8 seconds)

**Setup:**
1. Trigger an alert using Test Case 1 or 2 steps
2. Do NOT manually click the close button

**Steps:**
1. Once an alert is visible, wait for 8 seconds without interacting with it
2. Continue observing the screen

**Expected Result:**
- After approximately 8 seconds, the alert should smoothly fade out or slide away
- The alert should disappear without manual interaction
- No error should occur in the browser console
- If another alert notification arrives, it should display normally

---

### Test Case 6: Multiple Concurrent Alerts

**Setup:**
1. Ensure backend and frontend are running
2. Navigate to Inventory page

**Steps:**
1. Click "Advance Day" several times rapidly (5-10 times) to trigger multiple threshold events
2. Observe the alert container in the top-right corner

**Expected Result:**
- Multiple alerts should display in a stack in the top-right corner
- Alerts should not overlap or hide each other
- Each alert should be individually dismissible
- Alerts should auto-dismiss independently after 8 seconds
- The layout should accommodate multiple alerts without breaking

---

## Automated Test Verification

### Backend Integration Tests

Run the backend integration tests:
```bash
cd backend
.\gradlew.bat test --tests "EventListenerIntegrationTest"
```

**Expected Results:**
- ✅ `should_publishExpiredItemAlert_when_updateQualityReducesItemToQualityZero` - PASS
- ✅ `should_publishQualityThresholdAlert_when_updateQualityReducesQualityBelowWarning` - PASS
- ✅ `should_publishQualityThresholdAlert_when_updateQualityReducesQualityBelowCritical` - PASS

### Frontend E2E Tests

Run the frontend E2E tests (if backend is running):
```bash
cd frontend
npm run test:e2e
```

**Expected Results:**
- ✅ `.should_displayCriticalAlert_when_itemExpiresAfterAdvancingDay` - PASS
- ✅ `.should_displayWarningAlert_when_itemQualityDropsBelowWarningThreshold` - PASS
- ✅ `.should_displayDangerAlert_when_itemQualityDropsBelowCriticalThreshold` - PASS
- ✅ `.should_dismissAlert_when_clickingCloseButton` - PASS (or documented tolerances)
- ✅ `.should_autoDismissAlert_after8Seconds` - PASS (or documented tolerances)
- ✅ `.should_displayMultipleAlerts_concurrently` - PASS

Note: E2E tests require both backend and frontend running. If running in CI/CD, consider using Docker Compose or mocking WebSocket connections.

---

## Common Issues & Troubleshooting

### Alert Not Appearing
- Check that the backend is running and the STOMP WebSocket connection is established
- Open browser DevTools → Network tab and verify WebSocket connection to `/ws/gilded-rose` is active
- Check the console for any errors related to event listeners
- Verify the item's quality actually changed by checking the inventory table

### Wrong Alert Severity
- Verify the quality thresholds in `application.properties`:
  - `shop.quality.threshold.warning=25`
  - `shop.quality.threshold.critical=10`
- Check the `QualityThresholdListener` logic to ensure threshold crossing is correctly detected

### Alert Not Auto-Dismissing
- Check the `AlertComponent` implementation in the frontend to ensure the auto-dismiss timer is active
- Verify the timer is set to 8000ms (8 seconds)
- Check browser console for any timer errors

### WebSocket Connection Issues
- Verify both backend and frontend are running
- Check that the proxy is correctly configured in `frontend/proxy.conf.json`
- Verify STOMP endpoint is `/ws/gilded-rose` and broker configuration is correct

---

## Sign-Off

- Backend integration tests: ✅ PASS (3 tests)
- Frontend can display alerts: ✅ Verified (visual inspection)
- Alert dismissal works: ✅ Verified (manual + E2E)
- Alert auto-dismiss after 8 seconds: ✅ Verified (manual + E2E - with known timing variance)
- Full system end-to-end: ✅ Functional
