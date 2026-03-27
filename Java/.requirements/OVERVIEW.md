# Requirements Overview

This file is auto-maintained. It is updated every time a task is created or its status changes.

| ID  | Title                  | Status                | Summary |
|-----|------------------------|-----------------------|---------|
| 001 | Item Pricing Feature   | done | The shop needs a pricing system. Items have a base price; Sulfuras always sells at a fixed premium; expired items receive an automatic discount. |
| 002 | Dual-Type Conjured Items | done | Items named "Conjured <Type>" (e.g. Conjured Aged Brie, Conjured Backstage passes) follow the base type's quality rules with every delta doubled by the Conjured modifier. |
| 003 | Spring Boot Migration  | done | Migrate the project to Spring Boot 3.x, wiring GildedRose and PricingService as managed beans and exposing shop operations via a REST API. |
| 004 | REST API for Shop Operations | done | Add a REST API so clients can list inventory, retrieve item prices, and advance the shop by one day. |
| 005 | Item State Projection | done | Simulate the state an item will have after n days without mutating the live inventory. |
| 006 | In-Memory Item Persistence | done | Persist items using Spring Data JPA with H2 in-memory; architecture makes swapping to MariaDB a config-only change. |
| 007 | Remove Maven Build | done | Remove all Maven files (pom.xml, mvnw, .mvn/, target/) so Gradle is the sole build tool. |
| 008 | Angular Frontend Bootstrap | done | Scaffold an Angular 15 + Clarity app in `frontend/`, set up the proxy, shared models, and `ShopService`. |
| 009 | Inventory List Page | done | Display all shop items in a Clarity datagrid; show loading and error states. |
| 010 | Advance Day Action | done | Add an "Advance Day" button on the inventory page that calls the backend and refreshes the table. |
| 011 | Item Projection Panel | done | Per-item projection form for forecasting single item state; integrated into the dedicated Projection page with unified projection interface. |
| 012 | Shop Bulk Projection View | done | Dedicated Projection page with bulk inventory projection (all items after N days) and per-item projection form; angular/reactive forms with validation; all 35 tests passing. |
| 013 | Spring Event Bus | done | Publish domain events (DayAdvancedEvent, ItemExpiredEvent, ItemQualityChangedEvent) via the Spring event bus when the shop advances a day. |
| 014 | Clean Architecture & DDD Refactor | done | Reorganise the backend into explicit Clean Architecture layers (domain, application, infrastructure, api) so that dependencies always point inward and each layer is independently testable. |
| 015 | Inventory Table Sorting | done | Server-side sorting for the inventory table: `GET /api/items` gains `sortBy` and `sortDir` query params; the Angular frontend sends sort state from Clarity column header clicks to the backend. |
| 016 | OpenAPI REST Specification | done | Auto-generate OpenAPI 3.0 specification from Spring Boot REST API using springdoc-openapi; expose spec at `/v3/openapi.json` and Swagger UI at `/swagger-ui.html`. |
| 017 | Inventory Price Column | done | Add a price column to the inventory datagrid showing the cost of each item; column is read-only and not sortable. |
| 018 | Table Descending Sort Support | done | Enable toggle between ascending and descending sort orders in the inventory table; users can click a column header to sort ascending, then click again to sort descending. |
| 019 | Single Projection Item Dropdown | done | Replace the plain text input in Single Item Projection with a dropdown select list of available inventory items. |
| 020 | Playwright E2E Tests | done | Add Playwright end-to-end tests to the Angular frontend covering key user journeys; tests live under `frontend/e2e/`. |
| 021 | Angular 19 Upgrade | done | Upgrade the Angular frontend from version 15 to version 19, updating all peer dependencies (TypeScript, zone.js, CDK, Clarity) in four one-major-version steps via `ng update`. |
| 022 | Spotless Code Formatting | done | Add the Spotless Gradle plugin to enforce Google Java Format, remove unused imports, and trim trailing whitespace across all backend Java source files. |
| 023 | Backend Folder Restructure | done | Reorganize into a monorepo with `/backend` and `/frontend` at the same level; move `src/`, `build/`, `gradle/`, `gradlew` files under `backend/`; keep build config at root. |
| 024 | WebSocket STOMP Infrastructure | done | Set up Spring WebSocket with STOMP message broker for real-time communication; includes broker config, WebSocket endpoint, and AlertMessage DTO. |
| 025 | Item Expired Event Listener | done | Create backend listener for ItemExpiredEvent that publishes critical alerts to `/topic/item.expired` when items reach quality 0. |
| 026 | Quality Threshold Event Listener | ready-for-development | Create backend listener for ItemQualityChangedEvent that monitors quality thresholds and publishes WARNING/DANGER alerts when items drop below configured values. |
| 027 | Frontend Notification Service | done | Create Angular service managing STOMP WebSocket connections, alert subscriptions, and reactive alert state; exposes alerts$ observable to UI. |
| 028 | Clarity Alert Container | done | Create Angular component rendering real-time alerts using Clarity's alert component with slide-in animations; shows fixed stack in top-right corner. |
| 029 | Notification End-to-End Integration | ready-for-development | Integration and E2E testing of the complete notification system; verifies events flow from backend through STOMP to frontend display. |
