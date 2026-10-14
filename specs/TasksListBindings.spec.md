# Spec: `TasksListBindings.kt`

> Post-conditions are the test oracle: write the expected behavior from intent, not from the code.
> Tag each post-condition [Unit] if its test runs on the JVM; otherwise tag it [Integration].
> End each valid case with `Tested by <test>` if an existing test checks it, or `Tip:` describing the test to write.

## `setDueDateLabel(textView: TextView, task: Task?): Unit`

**Intent**: Show `dueDateLabel(task.dueDate, LocalDate.now(), task.isCompleted)` on the task-row label. A non-empty label is written into the text and the view is VISIBLE. An empty label means no label: the text is cleared to "" and the view is GONE. This includes a second bind that replaces a label already on the same view. Checking or unchecking a task, and clearing its due date, must refresh whatever was shown before. A null task is the same as no label.

**Pre-conditions**
- `textView` is the row's due-date `TextView` (`R.id.due_date_text`).
- `task` may be null.
- "Today" inside this function is `LocalDate.now()` on the device. Exact examples below are written for today = 2026-10-14. A test that must stay valid on another day builds the same relationship from `LocalDate.now()` and expects the total calendar-day string from `DueDateUtils.spec.md`.

**Post-conditions**
- Success cases
  - An active task due today shows "D-Day" and is VISIBLE. `[Integration]`
    - Tip: Given today = 2026-10-14 and an active task with dueDate = 2026-10-14, When setDueDateLabel is called, Then text is "D-Day" and visibility is VISIBLE. Tested by `TasksListBindingsTest.setDueDateLabel_activeDueToday_showsDDay`.
  - An active task due tomorrow shows "D-1" and is VISIBLE. `[Integration]`
    - Tip: Given today = 2026-10-14 and an active task with dueDate = 2026-10-15, When setDueDateLabel is called, Then text is "D-1" and visibility is VISIBLE. Tested by `TasksListBindingsTest.setDueDateLabel_activeDueTomorrow_showsD1`.
  - An active task overdue by one day shows "D+1" and is VISIBLE. `[Integration]`
    - Tip: Given today = 2026-10-14 and an active task with dueDate = 2026-10-13, When setDueDateLabel is called, Then text is "D+1" and visibility is VISIBLE. Tested by `TasksListBindingsTest.setDueDateLabel_activeOverdueOneDay_showsDPlus1`.
  - A completed task with a due date shows "CLEAR" and is VISIBLE. `[Integration]`
    - Tip: Given today = 2026-10-14 and a completed task with dueDate = 2026-10-20, When setDueDateLabel is called, Then text is "CLEAR" and visibility is VISIBLE. Tested by `TasksListBindingsTest.setDueDateLabel_completedWithDueDate_showsClear`.
  - A completed task with no due date shows "CLEAR" and is VISIBLE. `[Integration]`
    - Tip: Given a completed task with dueDate = null, When setDueDateLabel is called, Then text is "CLEAR" and visibility is VISIBLE. Tested by `TasksListBindingsTest.setDueDateLabel_completedNoDueDate_showsClear`.
  - An active task with no due date shows no label: text is "" and visibility is GONE. `[Integration]`
    - Tip: Given a fresh TextView and an active task with dueDate = null, When setDueDateLabel is called, Then text is "" and visibility is GONE. Tested by `TasksListBindingsTest.setDueDateLabel_activeNoDueDate_hidesLabel`.
- Error cases
  - None. A null task is valid and shows no label. `[Integration]`
    - Tip: Given task = null, When setDueDateLabel is called on a fresh TextView, Then text is "" and visibility is GONE. Tested by `TasksListBindingsTest.setDueDateLabel_nullTask_hidesLabel`.
- Edge cases
  - Unchecking a completed task that has no due date must remove CLEAR. The boundary is the same task before and after `isCompleted` flips from true to false, with dueDate staying null. `[Integration]`
    - Tip: Given a TextView already bound to a completed task with dueDate = null, so it shows "CLEAR", When setDueDateLabel is called again for that task with isCompleted = false and dueDate = null, Then text is "" and visibility is GONE. Tested by `TasksListBindingsTest.setDueDateLabel_uncheckCompletedNoDueDate_clearsStaleClear`.
  - Clearing the due date of an active task must remove the previous D-day label. `[Integration]`
    - Tip: Given today = 2026-10-14 and a TextView already showing "D-Day" for an active task due 2026-10-14, When setDueDateLabel is called again with the same task and dueDate = null, Then text is "" and visibility is GONE. Tested by `TasksListBindingsTest.setDueDateLabel_clearDueDate_clearsStaleLabel`.
  - Unchecking a completed task that still has a due date must replace CLEAR with that date's label, not leave CLEAR. `[Integration]`
    - Tip: Given today = 2026-10-14 and a TextView showing "CLEAR" for a completed task due 2026-10-14, When setDueDateLabel is called again with isCompleted = false and the same due date, Then text is "D-Day" and visibility is VISIBLE. Tested by `TasksListBindingsTest.setDueDateLabel_uncheckCompletedWithDueDate_showsDDay`.
  - Binding null after a visible label must clear that label. `[Integration]`
    - Tip: Given a TextView showing "CLEAR" for a completed task with dueDate = null, When setDueDateLabel is called with task = null, Then text is "" and visibility is GONE. Tested by `TasksListBindingsTest.setDueDateLabel_nullTaskAfterClear_clearsStaleLabel`.
  - The month-anniversary boundary and the days on both sides of it use the total calendar-day count from `DueDateUtils.spec.md`. On 2026-10-14 those strings are "D-30", "D-31", and "D-32". The test builds due = today.plusMonths(1) plus or minus one day so the same oracle holds on any run date. `[Integration]`
    - Tip: Given today = 2026-10-14 and an active task due 2026-11-13, When setDueDateLabel is called, Then text is "D-30" and visibility is VISIBLE. Tested by `TasksListBindingsTest.setDueDateLabel_activeDueDayBeforeMonthAnniversary_showsTotalDays`.
    - Tip: Given today = 2026-10-14 and an active task due 2026-11-14, When setDueDateLabel is called, Then text is "D-31" and visibility is VISIBLE. Tested by `TasksListBindingsTest.setDueDateLabel_activeDueOneMonthLater_showsTotalDays`.
    - Tip: Given today = 2026-10-14 and an active task due 2026-11-15, When setDueDateLabel is called, Then text is "D-32" and visibility is VISIBLE. Tested by `TasksListBindingsTest.setDueDateLabel_activeDueDayAfterMonthAnniversary_showsTotalDays`.

**Undefined behavior** (optional)
- Text color, strikethrough, and padding are set by other adapters and the layout. This function only owns the label text and its visibility.
- The clock is `LocalDate.now()`. Tests do not freeze the time zone; month-boundary cases are expressed relative to that clock and checked against the total calendar-day count.
