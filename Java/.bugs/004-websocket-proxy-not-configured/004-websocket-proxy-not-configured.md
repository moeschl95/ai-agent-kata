# 004 — WebSocket Alerts Not Received by Frontend

**Status:** fixed

---

## Description

After implementing the day advanced event listener and success notification feature, WebSocket messages were not being received by the frontend notification service. When clicking the "Advance Day" button, no success alert appeared. Checking Chrome DevTools showed the WebSocket connected to `ws://localhost:4200/ws/alerts`, but the console showed no "STOMP Connected" message and no incoming messages on the WebSocket.

---

## Root Cause

The Angular development server's proxy configuration (`proxy.conf.json`) was missing the `/ws` endpoint configuration with WebSocket protocol support enabled. The proxy had only configured the `/api` endpoint, which routed REST requests correctly, but the WebSocket upgrade request to `/ws/alerts` was not being proxied to the backend WebSocket endpoint on `http://localhost:8080/ws/alerts`.

Without the `"ws": true` setting in the proxy configuration, the HTTP GET request to upgrade the connection to WebSocket was failing silently, preventing the STOMP protocol from establishing a connection.

---

## Fix

Updated `frontend/proxy.conf.json` to add the `/ws` endpoint configuration:

```json
{
  "/api": {
    "target": "http://localhost:8080",
    "secure": false
  },
  "/ws": {
    "target": "http://localhost:8080",
    "secure": false,
    "ws": true,
    "logLevel": "debug"
  }
}
```

The key addition is:
- **`"/ws"` object** — routes all `/ws/*` requests to the backend
- **`"ws": true`** — enables WebSocket protocol upgrade support
- **`"logLevel": "debug"`** — optional, helps diagnose proxy issues

After restarting `npm start`, the frontend successfully connects to the backend's STOMP WebSocket endpoint, and all alert messages are received correctly.

---

## Acceptance Criteria

- [x] Frontend successfully connects to WebSocket (logs "STOMP Connected")
- [x] Day advanced event messages are received on `/topic/day.advanced`
- [x] Success alerts are displayed when advancing the day
- [x] Other alert topics still work (item expired, quality threshold)

---

## Affected Files

| File | Change |
|------|--------|
| `frontend/proxy.conf.json` | Added `/ws` endpoint with `"ws": true` for WebSocket protocol upgrade support |

---

## Changelog

| Date | Status | Note |
|------|--------|------|
| 2026-03-27 | reported | WebSocket notifications not received despite backend event publishing correctly |
| 2026-03-27 | implemented | Root cause identified: missing WebSocket configuration in proxy. Fix applied to proxy.conf.json. Frontend now receives all notification events. |
| 2026-03-27 | fixed | User confirmed success alerts now display after clicking "Advance Day" button. |
