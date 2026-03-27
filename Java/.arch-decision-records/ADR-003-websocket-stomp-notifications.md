# ADR-003: WebSocket STOMP for Real-Time Event Notifications

## Status
Accepted (2026-03-27)

## Context

The Gilded Rose shop application publishes domain events (ItemExpiredEvent, ItemQualityChangedEvent, DayAdvancedEvent) via the Spring event bus when inventory changes. Currently, clients have no way to receive these events in real-time without polling the REST API. This creates several problems:

1. **Polling inefficiency** — Frontend must periodically call `/api/items` to detect changes, wasting bandwidth and creating laggy user experience
2. **Stale data** — Polling intervals mean alerts can be delayed by seconds
3. **Scalability concerns** — High polling frequency degrades server performance; low frequency misses events
4. **Poor UX** — No immediate visual feedback when items expire or quality drops
5. **Lost event correlation** — Clients can't associate specific updates with domain events that triggered them

The shop needs a **push-based mechanism** to deliver events to clients in real-time as they occur.

## Decision

We have decided to implement **real-time event notifications using Spring WebSocket with STOMP** (Simple Text Oriented Messaging Protocol):

### Architecture

1. **Backend (Spring)**
   - Enable Spring WebSocket with STOMP message broker
   - Configure in-memory message broker for `/topic/*` destinations
   - Register WebSocket endpoint at `/ws/alerts` for client connections
   - Create event listeners for `ItemExpiredEvent` and `ItemQualityChangedEvent`
   - Listeners publish alert messages to STOMP topics (`/topic/item.expired`, `/topic/quality.threshold`)
   - Support quality threshold alerts with configurable thresholds (critical: 10, warning: 25)

2. **Frontend (Angular)**
   - Inject `@stomp/stompjs` client library
   - Create `NotificationService` to manage WebSocket lifecycle
   - Subscribe to STOMP topics on client initialization
   - Parse incoming alert messages and expose via RxJS `BehaviorSubject`
   - Create `AlertContainerComponent` consuming alert observable
   - Render alerts with Clarity alert component in fixed top-right stack
   - Auto-dismiss alerts after 8 seconds or on user close

### Message Flow

```
GildedRose.updateQuality()
  │
  ├─ publishes ItemExpiredEvent
  │  └─ ItemExpiredEventListener ──┐
  │                                  ├─ SimpMessagingTemplate
  └─ publishes ItemQualityChangedEvent     │
     └─ QualityThresholdListener ────┐    │
                                      │    │
                            convertAndSend()
                                      │    │
                        /topic/item.expired
                        /topic/quality.threshold
                                      │    │
                                      └────┴──→ [In-Memory STOMP Broker]
                                              │
                        ┌───────────────────┬─┘
                        │
                   WebSocket Connection
                        │
                    [Angular Client]
                        │
                   stompClient.subscribe()
                        │
                   NotificationService
                        │
                   alerts$ observable
                        │
                   AlertContainerComponent
                        │
                   [Clarity Alert UI]
```

## Consequences

### Positive
- ✅ **Real-time delivery** — Alerts reach clients within milliseconds of domain events
- ✅ **Server efficient** — No polling; push-based reduces server load, scales better
- ✅ **Excellent UX** — Immediate visual feedback with animated alerts
- ✅ **Loose coupling** — Event listeners are decoupled from client concerns; new clients or subscribers can be added without modifying listeners
- ✅ **Extensible** — New STOMP topics can be added easily (e.g., `/topic/inventory.updated`, `/topic/daily.reports`)
- ✅ **Industry standard** — STOMP is a well-established protocol; many client libraries exist
- ✅ **Framework integrated** — Spring has first-class WebSocket and STOMP support; minimal configuration needed

### Negative
- ⚠️ **Connection stateful** — WebSocket maintains persistent connections; requires proper cleanup and reconnection logic
- ⚠️ **Browser compatibility** — Older browsers need WebSocket fallbacks (Sockjs); mitigated by using established libraries
- ⚠️ **Memory overhead** — In-memory broker consumes memory; not suitable for very large deployments (see mitigation)
- ⚠️ **Single server** — In-memory broker doesn't scale across multiple servers without clustering (see mitigation)
- ⚠️ **Testing complexity** — WebSocket connections make tests more complex; requires careful mocking or embedded brokers

### Mitigation Strategies
- Use robust client library (`@stomp/stompjs`) with built-in reconnection and heartbeat
- Implement heartbeat pings to detect stale connections
- Log connection errors and provide clear error messages in console
- For future multi-server deployment: switch to RabbitMQ or ActiveMQ broker (configuration-only change, no code changes)
- Unit test event listeners with mocked `SimpMessagingTemplate`; E2E tests verify full pipeline with real connections

## Alternatives Considered

### 1. Server-Sent Events (SSE)
- Unidirectional push from server to client over HTTP
- **Advantages:** Simpler than WebSocket, no persistent TCP connection, built on HTTP
- **Disadvantages:** Can't send messages from client; harder to distribute across multiple servers; less efficient for high-frequency updates; no widespread server framework support for SSE + message broker
- **Rejected because:** We may need bidirectional communication in future (e.g., client filtering preferences); STOMP is more future-proof and widely used

### 2. Long Polling with REST API
- Client polls `/api/alerts/new-since?timestamp=X` at intervals
- **Advantages:** Simple to implement; works everywhere
- **Disadvantages:** Inefficient for real-time (high latency, wasted requests); poor UX; doesn't scale (creates connection thrashing at high polling frequencies)
- **Rejected because:** Misses the whole point of real-time notifications; explicitly rejected in user requirements ("Polling is not cool")

### 3. WebSocket without STOMP
- Use raw WebSocket API at `/ws` and implement custom message protocol
- **Advantages:** Minimal overhead; extremely efficient
- **Disadvantages:** Must invent and maintain custom message format; no standard client libraries; testing is harder; team must implement heartbeat, reconnection, acking
- **Rejected because:** Reinventing STOMP poorly is worse than using the standard; STOMP overhead is negligible; complexity is higher, not lower

### 4. gRPC Server Push
- Use gRPC with server streaming for push notifications
- **Advantages:** Efficient binary protocol; integrated with Go/Java ecosystems
- **Disadvantages:** Complex browser client setup; requires gRPC-Web proxy; harder to integrate with existing REST API; overkill for this use case
- **Rejected because:** Browser clients have poor gRPC support; STOMP over WebSocket is more natural for web applications

## Decision Record

- **Proposed by:** Architecture team
- **Decided on:** 2026-03-27
- **Implementation tasks:** 024–029 (WebSocket infrastructure, event listeners, frontend service, UI component, integration testing)
- **Status:** Ready for implementation

## Implementation Details

### Backend Components
- `WebSocketConfig` — Spring configuration class enabling STOMP broker and WebSocket endpoint
- `AlertMessage` — DTO record carrying alert severity, title, message, timestamp
- `ItemExpiredEventListener` — Event listener publishing ItemExpiredEvent → STOMP `/topic/item.expired`
- `QualityThresholdListener` — Event listener publishing ItemQualityChangedEvent → STOMP `/topic/quality.threshold`
- `application.properties` — Configurable thresholds for quality monitoring

### Frontend Components
- `NotificationService` — Angular service managing STOMP client lifecycle and alert state
- `AlertContainerComponent` — Angular component rendering alerts with Clarity styling
- `Alert` interface — TypeScript model for alert data structure

### Message Formats
```json
{
  "severity": "CRITICAL|DANGER|WARNING|INFO",
  "title": "Item Expired: Aged Brie",
  "message": "The item 'Aged Brie' has reached quality 0 and is no longer saleable.",
  "timestamp": 1711549200000
}
```

### Topics
- `/topic/item.expired` — ItemExpiredEvent listener publishes here
- `/topic/quality.threshold` — QualityThresholdListener publishes here
- WebSocket endpoint: `/ws/alerts`

## Related Decisions

- Depends on ADR-001 (Clean Architecture) — event listeners are in infrastructure layer
- Uses domain events from task 013 (Spring Event Bus) — listeners consume these events
- Integrates with task 010 (Advance Day Action) — advancing day triggers events that trigger notifications

## References

- [Spring WebSocket Documentation](https://spring.io/guides/gs/messaging-stomp-websocket/)
- [STOMP Protocol Specification](https://stomp.github.io/stomp-specification-1.2.html)
- [RxJS BehaviorSubject](https://rxjs.dev/api/index/class/BehaviorSubject)
- [Clarity Alerts Component](https://clarity.design/components/alerts)
- [Understanding WebSocket Lifecycle and Reconnection](https://developer.mozilla.org/en-US/docs/Web/API/WebSocket)
