# Requirements Overview

This file is auto-maintained. It is updated every time a task is created or its status changes.

| ID  | Title                  | Status                | Summary |
|-----|------------------------|-----------------------|---------|
| 001 | Item Pricing Feature   | done | The shop needs a pricing system. Items have a base price; Sulfuras always sells at a fixed premium; expired items receive an automatic discount. |
| 002 | Dual-Type Conjured Items | done | Items named "Conjured <Type>" (e.g. Conjured Aged Brie, Conjured Backstage passes) follow the base type's quality rules with every delta doubled by the Conjured modifier. |
| 003 | Spring Boot Migration  | done | Migrate the project to Spring Boot 3.x, wiring GildedRose and PricingService as managed beans and exposing shop operations via a REST API. |
| 004 | REST API for Shop Operations | ready-for-development | Add a REST API so clients can list inventory, retrieve item prices, and advance the shop by one day. |
