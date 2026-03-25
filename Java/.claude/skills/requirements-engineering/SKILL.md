---
name: requirements-engineering
description: >
  Manage requirements and tasks for this project. Creates structured task files under /requirements/,
  assigns incrementing 3-digit task IDs, writes a description and implementation plan, and guides the
  task through an approval workflow. Use this skill whenever a user mentions a requirement, a feature
  request, a bug to fix, a task to plan, or says things like "I want to...", "we need to...",
  "add support for...", "plan this...", "create a task for...", "track this work", or "let's implement...".
  Also trigger this skill when the user reviews or approves/rejects a task, asks for task status,
  or wants to list all requirements. Even if they don't say "requirement" or "task" explicitly —
  if they are describing work to be done, use this skill.
---

# Requirements Engineering & Task Management

This skill turns raw requirements into tracked, structured task files under `/requirements/`, walks
them through an approval gate, and keeps every task in a well-known status so the team always knows
what is ready to build.

---

## Workflow overview

```
New requirement described by user
        │
        ▼
  1. Determine next task ID
        │
        ▼
  2. Create task file  (status: funnel)
        │
        ▼
  3. Check existing open tasks for conflicts
     └─ update affected tasks + their Changelog
        │
        ▼
  4. Present to user for approval
        │
      ┌─┴──────────────┐
   Approved          Changes requested
      │                    │
      ▼                    ▼
  5. Mark             Revise & re-present
  ready-for-development
        │
        ▼
     Agent picks up work
        │
        ▼
    in-progress
        │
        ▼
    implemented  ←  agent signals done
        │
      ┌─┴──────────────┐
  Review OK        Changes requested
      │                    │
      ▼                    ▼
    done            back to in-progress
```

---

## Step 1 — Determine the next task ID

Scan the `/requirements/` folder for sub-directories whose names start with a 3-digit number
(e.g. `001-short-title/`). The next ID is the highest existing number + 1, zero-padded to 3 digits.
If no task folders exist yet, start at `001`.

```
existing folders: 001-aged-brie-fix, 002-conjured-item
next ID: 003
```

> Never reuse or skip IDs. IDs are permanent once assigned.

---

## Step 2 — Derive the short title

From the requirement text, derive a concise kebab-case title (3–6 words, no stop-words).

**Examples:**
- "I want to add support for Conjured items" → `conjured-item-support`
- "Fix the quality cap bug for Aged Brie" → `aged-brie-quality-cap`
- "Refactor GildedRose to use the Strategy pattern" → `strategy-pattern-refactor`

The folder name and file name both use the full ID+title slug: `003-conjured-item-support`.

---

## Step 3 — Create the task folder and file

Create:
```
requirements/
└── <ID>-<short-title>/
    └── <ID>-<short-title>.md
```

Use the template below. Fill in every section — do not leave placeholders.

---

### Task file template

```markdown
# <ID> — <Human-readable title>

**Status:** funnel

---

## Description

<2–4 sentences that describe what needs to be done and why. Written from the perspective of the
desired outcome, not the implementation. Non-technical enough for a product owner to understand.>

---

## Implementation Plan

<Numbered list of concrete steps. Each step should be small enough to be a single commit.
Include notes on which files to touch, which patterns to follow, and any risks or open questions.>

1. …
2. …
3. …

---

## Acceptance Criteria

<Bullet list of conditions that must be true for this task to be considered done.
Each item should be directly verifiable — either by a test or by visual inspection.>

- [ ] …
- [ ] …

---

## Notes

<Optional. Any additional context, links, decisions, or constraints.>

---

## Changelog

| Date | Status | Note |
|------|--------|------|
| YYYY-MM-DD | funnel | Task created |
```

---

## Step 4 — Check existing open tasks for conflicts

Before presenting the new task to the user, scan all existing task files whose status is **not**
`done` (i.e. `funnel`, `ready-for-development`, `in-progress`, or `implemented`) for potential
conflicts with the new task.

**What counts as a conflict:**
- Both tasks touch the same classes, interfaces, or files.
- The new task introduces an API or abstraction that an existing task also plans to introduce (duplication risk).
- The new task makes assumptions (e.g. about data structures or method signatures) that contradict an existing task's plan.
- The new task's scope overlaps with an existing task in a way that could cause merge conflicts or contradictory implementations.

**If conflicts are found:**
1. Update the affected existing task file — add a note under its **Notes** section describing the conflict and how the two tasks relate.
2. Append a Changelog row to the affected task:
   ```
   | YYYY-MM-DD | <current status> | Conflict noted with <new-task-ID>: <short reason> |
   ```
3. Add a **Conflicts** subsection to the new task's **Notes** section listing every affected task and the nature of the conflict.

**If no conflicts are found:** proceed directly to Step 5 without modifying any existing task files.

---

## Step 5 — Present for approval

After creating the file (and resolving any conflicts), display the full content of the new task file
to the user inline (do not just say "file created"). Then ask:

> "Does this look right? Reply **approve** to mark it ready-for-development, or tell me what to change."

> The task starts in `funnel` status — it only moves to `ready-for-development` once you approve it.

Wait for the user's response before proceeding.

---

## Step 6 — Handle the response

### If the user approves (says "approve", "looks good", "yes", "lgtm", etc.)

Update the `**Status:**` line in the task file from `funnel` to `ready-for-development`:

```markdown
**Status:** ready-for-development
```

Append a new row to the Changelog table:

```markdown
| YYYY-MM-DD | ready-for-development | Approved by user |
```

Confirm to the user: "Task `<ID>-<short-title>` is now **ready-for-development**."

### If the user requests changes

Apply the requested changes to the task file (description, implementation plan, acceptance criteria,
or title/ID slug if truly needed). Append a new row to the Changelog table describing what was changed:

```markdown
| YYYY-MM-DD | funnel | Revised: <short summary of changes> |
```

Then re-present the updated content and go back to Step 5.

---

## Status reference

| Status | Set by | Meaning |
|---|---|---|
| `funnel` | Agent (on creation) | Freshly created, awaiting user approval |
| `ready-for-development` | User (approval) | Approved — safe for an agent to start building |
| `in-progress` | Agent (when work begins) | An agent has picked it up and is actively working |
| `implemented` | Agent (when work is done) | Implementation complete, waiting for user review |
| `done` | User (positive review) | Reviewed and accepted — task is closed |

Transition rules:
- Only the **user** can move a task from `funnel` → `ready-for-development` (via explicit approval).
- Only the **user** can move a task from `implemented` → `done` (via explicit sign-off).
- An **agent** moves tasks from `ready-for-development` → `in-progress` when it starts work, and from `in-progress` → `implemented` when it finishes.
- If the user rejects an `implemented` task, the agent moves it back to `in-progress` and notes the feedback in the Changelog.

Always append a Changelog row whenever the status changes.

---

## Listing tasks

When the user asks to see all tasks, list current requirements or asks "what's the status", scan
`/requirements/` for task folders and produce a summary table:

```
| ID  | Title                    | Status                  |
|-----|--------------------------|-------------------------|
| 001 | aged-brie-quality-cap    | ready-for-development   |
| 002 | conjured-item-support    | draft                   |
```

Read the status from each task file's `**Status:**` line.

---

## Rules and constraints

- **One task file per requirement.** Do not bundle unrelated requirements into one task.
- **IDs never change.** Once a folder is created with an ID, that ID is permanent.
- **Do not modify `/requirements/GildedRoseRequirements.md`** — that is the original kata spec, not a task file.
- **Always show the task content** to the user before asking for approval — never silently create a file and just report a path.
- **Status lives in the file.** The single source of truth is the `**Status:**` line inside the task `.md` file.
- **Changelog is append-only.** Every status change, revision, or significant decision must be recorded as a new row in the Changelog table (with today's date, the new status, and a short note). Never edit or delete an existing changelog row.
- **Always run the conflict check.** When creating a new task, always scan all non-`done` task files before presenting the task for approval. Skipping the conflict check is not permitted, even if it seems unlikely there are conflicts.
