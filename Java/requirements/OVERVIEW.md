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
| 011 | Item Projection Panel | ready-for-development | Per-row "Project" button opens a modal form to forecast a single item's state after N days. |
| 012 | Shop Bulk Projection View | ready-for-development | Dedicated Projection page: enter days, view entire inventory projected forward without mutating live data. |
| 013 | Spring Event Bus | done | Publish domain events (DayAdvancedEvent, ItemExpiredEvent, ItemQualityChangedEvent) via the Spring event bus when the shop advances a day. |
| 014 | Clean Architecture & DDD Refactor | done | Reorganise the backend into explicit Clean Architecture layers (domain, application, infrastructure, api) so that dependencies always point inward and each layer is independently testable. |
| 015 | Inventory Table Sorting | done | Server-side sorting for the inventory table: `GET /api/items` gains `sortBy` and `sortDir` query params; the Angular frontend sends sort state from Clarity column header clicks to the backend. |
| 016 | OpenAPI REST Specification | done | Auto-generate OpenAPI 3.0 specification from Spring Boot REST API using springdoc-openapi; expose spec at `/v3/openapi.json` and Swagger UI at `/swagger-ui.html`. |
| 017 | Inventory Price Column | ready-for-development | Add a price column to the inventory datagrid showing the cost of each item; column is read-only and not sortable. |
| 018 | Table Descending Sort Support | ready-for-development | Enable toggle between ascending and descending sort orders in the inventory table; users can click a column header to sort ascending, then click again to sort descending. |
