# ADR-001: Reorganize Backend into Clean Architecture with Domain-Driven Design

## Status
Accepted (2026-03-25)

## Context

The Gilded Rose backend evolved organically with code scattered across a single flat package (`com.gildedrose`) with no enforced architectural boundaries. Classes directly reference persistence details, business logic mixes with infrastructure concerns, and the `GildedRose` class has accumulated multiple responsibilities. This architecture creates several problems:

1. **Unclear boundaries** — Controllers can reach directly into persistence types; services can reference persistence details
2. **Difficult testing** — Complex to test components in isolation without Spring context dependencies
3. **Scaling friction** — Hard to add new item types, features, or behaviors without creating circular dependencies or deeply nested if/else chains
4. **Maintenance burden** — Changes to infrastructure (e.g., switching databases) require touching business logic code
5. **Unclear intent** — Newcomers struggle to understand which code is domain logic, which is infrastructure, and which is API adaptation

## Decision

We have decided to reorganize the backend into explicit **Clean Architecture layers** with **Domain-Driven Design (DDD)** principles:

### Architectural Layers

**Domain Layer** (`com.gildedrose.domain.*`)
- Pure business logic independent of frameworks and infrastructure
- Packages: `model` (entities), `service` (business rules), `event` (domain events), `repository` (ports)
- Owns quality update rules, pricing logic, item projection rules
- No dependencies on Spring, JPA, or infrastructure concerns

**Application Layer** (`com.gildedrose.application.*`)
- Orchestrates domain logic to implement use cases
- Contains `ShopService` for coordinating operations (advance day, get inventory, price lookup, projections)
- Uses Data Transfer Objects (DTOs) for external contracts
- Depends on: domain layer only
- No dependencies on controllers or infrastructure details

**Infrastructure Layer** (`com.gildedrose.infrastructure.*`)
- Implements technical concerns: persistence, configuration, data initialization
- Packages: `persistence` (JPA, repositories, mappers), `config` (Spring beans, initialization)
- Adapts domain ports (e.g., `ItemRepositoryPort`) to concrete implementations (e.g., `JpaItemRepositoryAdapter`)
- Depends on: domain layer only

**API Layer** (`com.gildedrose.api.*`)
- REST controllers and HTTP endpoint handling
- Depends on: application, domain layers
- No direct dependency on infrastructure details

### Key Design Principles

1. **Dependency Rule** — Dependencies only flow inward; nothing in the domain layer knows about outer layers
2. **Ports & Adapters** — Domain defines ports (e.g., `ItemRepositoryPort`); infrastructure provides adapters
3. **Entity Autonomy** — Domain entities and business rules are zero-dependency and testable without frameworks
4. **Separation of Concerns** — Each layer has a single, clear responsibility

## Consequences

### Positive
- ✅ **Clear architectural boundaries** — Developers immediately understand where code belongs
- ✅ **Independent testability** — Domain logic and application services are testable without Spring context
- ✅ **Infrastructure flexibility** — Can swap database, logging, or event bus without touching business logic
- ✅ **Scalability** — Adding new item types now uses polymorphism (Strategy pattern) instead of if/else chains
- ✅ **Team scalability** — Multiple teams can work on different layers with minimal coupling
- ✅ **DDD vocabulary** — Ubiquitous language becomes explicit through aggregate roots, value objects, domain services, events

### Negative
- ⚠️ **Initial complexity** — More packages and abstractions to navigate initially
- ⚠️ **Mapping overhead** — Domain models need mapping to/from JPA entities and DTOs
- ⚠️ **Learning curve** — Team must understand Clean Architecture and DDD concepts
- ⚠️ **Boilerplate** — Port interfaces and adapters add some code volume

### Mitigation Strategies
- Documentation and onboarding focus on layer responsibilities and dependency rules
- Clear naming conventions (`*Port` for domain ports, `*Adapter` for infrastructure implementations, `*Service` for application services)
- Comprehensive test organization mirroring production structure
- Continuous validation that dependency rules are not violated

## Alternatives Considered

### 1. Layered Architecture (Traditional 3-layer)
- Presentation → Business → Data
- **Rejected** because it typically still allows presentation to reference data layers directly and lacks the flexibility of ports/adapters

### 2. Package-by-Feature Organization
- Organize by feature (e.g., `pricing.`, `inventory.`) rather than layer
- **Rejected** because it obscures technical concerns and makes it harder to enforce consistent architectural patterns across features

### 3. Remain Monolithic Flat Package
- Keep everything in `com.gildedrose` with conventions
- **Rejected** because conventions alone (without compiler enforcement) historically fail as teams scale; Java package boundaries enforce encapsulation

## Decision Record

- **Proposed by:** Architecture team
- **Decided on:** 2026-03-25
- **Implemented in Task:** 014 — Clean Architecture & DDD Refactor
- **Test Results:** All 86 existing tests passing; no regressions

## Implementation Details

- **Domain Layer:** Contains item updaters, pricing service, projection service, domain events, repository port
- **Application Layer:** Contains `ShopService` for use-case orchestration
- **Infrastructure Layer:** Contains JPA persistence, repository adapter, Spring configuration
- **API Layer:** Contains REST controller (unchanged external contract)
- **Root Files:** `GildedRoseApplication.java` (Spring Boot entry point) and `Item.java` (immutable goblin class per requirements)

## Related Decisions

- Uses Strategy pattern (ItemUpdater factory) for polymorphic item type handling
- Implements Repository pattern for data access abstraction
- Publishes domain events (DayAdvancedEvent, ItemExpiredEvent, ItemQualityChangedEvent) for eventual consistency
- DTOs (`ItemDto`) separate internal domain models from external API contracts

## References

- [Clean Architecture: A Craftsman's Guide to Software Structure and Design](https://www.goodreads.com/book/show/18043633-clean-architecture) — Robert C. Martin
- [Domain-Driven Design: Tackling Complexity in the Heart of Software](https://www.goodreads.com/book/show/179133.Domain-Driven-Design) — Eric Evans
- [Ports and Adapters Pattern (Hexagonal Architecture)](https://alistair.cockburn.us/hexagonal-architecture/)
