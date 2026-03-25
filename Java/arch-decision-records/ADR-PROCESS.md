# Architecture Decision Record (ADR) Process

This document defines the process for proposing, approving, and documenting significant architecture decisions.

---

## When an ADR is Needed

An ADR is required for any **significant architecture decision**, including:

- Major package/module restructuring
- Introducing new layers or removing layers
- Significant pattern changes (e.g., flat package → layered architecture)
- Restructuring core components
- Changes that affect multiple files across different packages
- Changes to fundamental design principles or patterns

---

## ADR Process (Mandatory)

**Never implement a significant architecture change without explicit approval.**

### Step 1: Propose the Architecture Change

When a significant architecture decision is needed:

1. **Ask permission first** — Do not assume approval is delegated
2. **Explain the rationale** — Why is this change needed?
3. **Articulate trade-offs** — What are the benefits and drawbacks?
4. **Present alternatives** — What other approaches were considered?
5. **Wait for approval** — Only proceed after explicit user approval

### Step 2: Create the ADR

Once approved, create a new ADR file documenting:

- **Context** — The problem or situation that prompted the decision
- **Decision** — The architecture change being made
- **Consequences** — Positive and negative impacts
- **Alternatives Considered** — Other options that were rejected and why
- **Implementation Details** — How the decision will be implemented across the codebase
- **Related Design Patterns** — Applicable patterns used in the solution
- **References** — Links to relevant literature or resources

### Step 3: Implement the Change

Only after the ADR is approved and documented, proceed with implementation.

---

## ADR File Structure

```
arch-decision-records/
├── ADR-001-<decision-title>.md
├── ADR-002-<decision-title>.md
├── ADR-PROCESS.md  (this file)
└── ...
```

**File naming:** `ADR-<3-digit-ID>-<kebab-case-title>.md`

---

## Core Rule

**Do not implement unilateral architecture changes without consulting and getting explicit approval.**

Architecture decisions are documented in ADRs to preserve rationale and enable future developers to understand why the architecture is structured as it is.

