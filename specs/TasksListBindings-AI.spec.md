# Spec: `TasksListBindings.kt`

> Post-conditions are the test oracle: write the expected behavior from intent, not from the code.
> Tag each post-condition [Unit] if its test runs on the JVM; otherwise tag it [Integration].
> End each valid case with `Tested by <test>` if an existing test checks it, or `Tip:` describing the test to write.

## `setDueDateLabel(textView: TextView, task: Task?): Unit`

**Intent**: Put the task's D-day label on the list-row text view. The label comes from dueDateLabel, using the current date.

**Pre-conditions**
- `textView` is the due-date view in a task row.
- `task` is the row's task, or null when the row has no task.

**Post-conditions**
- Success cases
  - An active task due today shows "D-Day". `[Integration]`
    - Tip: Given an active task whose due date is today, When setDueDateLabel is called, Then the TextView shows "D-Day".
  - A completed task shows "CLEAR". `[Integration]`
    - Tip: Given a completed task, When setDueDateLabel is called, Then the TextView shows "CLEAR".
  - An active task with no due date shows nothing. `[Integration]`
    - Tip: Given an active task with dueDate = null, When setDueDateLabel is called, Then the TextView text is empty.
- Error cases
  - None. A null task is not a rejection. `[Integration]`
    - Tip: Given task = null, When setDueDateLabel is called, Then the call does not throw.
- Edge cases
  - None beyond the success cases. `[Integration]`
    - Tip: No further boundary is specified.

**Undefined behavior** (optional)
- Color, typeface, and layout position of the label are left to the row layout.
