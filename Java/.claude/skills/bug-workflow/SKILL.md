---
name: bug-workflow
description: >
  Report, track, investigate, fix, and close bugs in this project. Creates structured bug report
  files under /bugs/, assigns incrementing 3-digit IDs, documents root cause and fix, and drives
  the bug through its lifecycle: reported → in-progress → implemented → fixed → done.
  Use this skill whenever the user reports a bug, describes something that isn't working, says
  "this is broken", "I found a bug", "it's not rendering correctly", "an error appeared", or
  anything that describes incorrect or unexpected behaviour in the running application.
  Also use it when the user reviews a fixed bug (accepts or rejects), or asks for the status of
  open bugs. Do NOT use this skill for new features or planned work — that is the
  requirements-engineering skill.
---

# Bug Workflow

This skill manages the full lifecycle of a bug: from the moment the user reports it, through
investigation and fix, to the user's final acceptance.

---

## Workflow overview

```
User reports a bug
        │
        ▼
  1. Create bug file  (status: reported)
        │
        ▼
  2. User asks to fix  ─────────────────────────────────────────┐
        │                                                        │
        ▼  agent starts investigation & fix                     │
    in-progress                                                 │
        │                                                        │
        ▼  fix verified, tests pass                             │
    implemented  ←  agent signals done                          │
        │                                                        │
      ┌─┴──────────────┐                                        │
  User accepts     User rejects                                 │
      │                    │                                     │
      ▼                    ▼                                     │
    fixed           back to in-progress ────────────────────────┘
```

> **Only start fixing when the user explicitly asks** (e.g. "fix it", "go ahead", "work on it").
> Reporting a bug does NOT automatically start the fix.

---

## Step 1 — Create the bug file (status: reported)

### Determine the next bug ID

Scan `/bugs/` for sub-directories whose names start with a 3-digit number (e.g. `001-short-title/`).
The next ID is the highest existing number + 1, zero-padded to 3 digits. Start at `001` if none exist.

> Never reuse or skip IDs. IDs are permanent once assigned.

### Derive the short title

From the user's description, derive a concise kebab-case slug (3–6 words, no stop-words).

**Examples:**
- "Clarity CSS variables not loading" → `clarity-css-variables-not-loaded`
- "Inventory datagrid shows 500 error" → `inventory-datagrid-500-error`
- "Advance day button does nothing" → `advance-day-button-no-op`

The folder and file both use the full ID+slug: `002-inventory-datagrid-500-error`.

### Create the folder and file

```
bugs/
└── <ID>-<short-title>/
    └── <ID>-<short-title>.md
```

Use the template below. Fill every section with what is already known — leave a section blank or
note "unknown" only if the information is genuinely unavailable at report time.

---

### Bug file template

```markdown
# <ID> — <Human-readable title>

**Status:** reported

---

## Description

<2–4 sentences describing what is wrong. What is the observed behaviour? What was expected?
Include any error messages, browser console output, or stack traces if provided.>

---

## Root Cause

<Leave blank or write "under investigation" until the cause is known.>

---

## Fix

<Leave blank until the fix is implemented. Then describe exactly what was changed and why.>

---

## Acceptance Criteria

- [ ] <Verifiable condition that proves the bug is gone.>
- [ ] <Additional checks if needed.>

---

## Affected Files

| File | Change |
|------|--------|
| _to be filled during fix_ | |

---

## Changelog

| Date | Status | Note |
|------|--------|------|
| YYYY-MM-DD | reported | Bug reported: <one-line summary> |
```

---

After creating the file, confirm to the user that the bug has been logged and show the file path.
Tell them to let you know when they want you to start investigating and fixing it.

---

## Step 2 — Start the fix (reported → in-progress)

Only begin when the user explicitly asks to fix the bug.

1. Re-read the bug file to confirm the details.
2. Update `**Status:**` to `in-progress`.
3. Append a Changelog row:
   ```
   | YYYY-MM-DD | in-progress | Investigation and fix started |
   ```
4. Investigate the root cause by reading relevant source files. Update the **Root Cause** section
   of the bug file once the cause is identified.

---

## Step 3 — Implement the fix (in-progress)

### TDD is required — every bug must have a unit test

**Every bug must be verified with a unit test.** This ensures the bug is reproducible, stays fixed,
and prevents regression.

Follow the Red-Green-Refactor cycle:

1. **Red** — write a failing test that reproduces the bug.
2. **Green** — write the minimal fix that makes it pass.
3. **Refactor** — clean up without changing behaviour; tests must still pass.

For infrastructure/config bugs (e.g. incorrect CSS paths, missing files, build config), a
verification test (not necessarily a unit test) must still be created — either a test that checks
the fix, or a documented manual verification procedure with clear success criteria.

### Fill in the bug file

- Update the **Root Cause** section with a clear explanation.
- Update the **Fix** section describing what was changed and why.
- Fill in the **Affected Files** table.

---

## Step 4 — Mark implemented (in-progress → implemented)

When the fix is complete and verified (tests pass / build succeeds):

1. Update `**Status:**` to `implemented`.
2. Update the **Acceptance Criteria** checkboxes — check off each verified criterion.
3. Append a Changelog row:
   ```
   | YYYY-MM-DD | implemented | <short summary: root cause, fix applied, tests> |
   ```
4. Report to the user: describe the root cause, what was changed, and how it was verified.
   Invite them to review.

---

## Step 5 — Handle the user's review

### If the user accepts (says "done", "looks good", "approved", "fixed", "ship it", etc.)

Transition to `fixed`:

1. Update `**Status:**` to `fixed`.
2. Append a Changelog row:
   ```
   | YYYY-MM-DD | fixed | Accepted by user |
   ```

### If the user rejects (requests changes or says it's still broken)

Move back to `in-progress`:

1. Update `**Status:**` to `in-progress`.
2. Append a Changelog row:
   ```
   | YYYY-MM-DD | in-progress | Returned: <short summary of feedback> |
   ```
3. Address the feedback and return to Step 3.

---

## Status reference

| Status | Set by | Meaning |
|---|---|---|
| `reported` | Agent (this skill) | Bug logged, not yet being worked on |
| `in-progress` | Agent (this skill) | Actively being investigated or fixed |
| `implemented` | Agent (this skill) | Fix applied — waiting for user review |
| `fixed` | User | Accepted — bug is closed |

Transition rules:
- Agent creates the file in `reported`.
- Agent moves `reported` → `in-progress` **only when the user explicitly asks to fix**.
- Agent moves `in-progress` → `implemented` when fix is verified.
- Agent moves `implemented` → `in-progress` if the user rejects.
- **Only the user** can move `implemented` → `fixed`.

---

## Listing bugs

When the user asks "what bugs are open?", "show me all bugs", or similar, scan `/bugs/` for bug
folders and produce a summary table:

```
| ID  | Title                              | Status       |
|-----|------------------------------------|--------------|
| 001 | Clarity CSS Variables Not Loaded   | fixed        |
```

---

## Rules and constraints

- **Never start fixing without an explicit request.** Reporting a bug does NOT trigger a fix.
- **Changelog is append-only.** Never edit or delete an existing row.
- **IDs are permanent.** Never reuse or renumber existing bug IDs.
- **Root cause must be documented** before or alongside the fix — never leave it blank on a
  fixed bug.
- **Every bug must be verified with a unit test.** No bug is considered fixed without a test that
  reproduces the original failure and passes with the fix applied. This is mandatory.
