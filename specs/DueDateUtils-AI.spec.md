# Spec: `DueDateUtils.kt`

> Post-conditions are the test oracle: write the expected behavior from intent, not from the code.
> Tag each post-condition [Unit] if its test runs on the JVM; otherwise tag it [Integration].
> End each valid case with `Tested by <test>` if an existing test checks it, or `Tip:` describing the test to write.

## `dueDateLabel(dueDate: LocalDate?, today: LocalDate, isCompleted: Boolean): String`

**Intent**: Build the D-day string shown for one task. A completed task is CLEAR. An active task with no due date shows nothing. Otherwise the label is D-Day, D-N, or D+N depending on how many days are left.

**Pre-conditions**
- `today` is the calendar day the label is computed for.
- `dueDate` is either null or a calendar date.
- `isCompleted` is the task's completion flag at the time of the call.

**Post-conditions**
- Success cases
  - An active task due today returns "D-Day". `[Unit]`
    - Tip: Given the due date is today and isCompleted = false, When dueDateLabel is called, Then the result is "D-Day".
  - An active task due in the future returns "D-N". `[Unit]`
    - Tip: Given the due date is 3 days after today and isCompleted = false, When dueDateLabel is called, Then the result is "D-3".
  - An active overdue task returns "D+N". `[Unit]`
    - Tip: Given the due date is 2 days before today and isCompleted = false, When dueDateLabel is called, Then the result is "D+2".
  - An active task with no due date returns an empty string. `[Unit]`
    - Tip: Given dueDate = null and isCompleted = false, When dueDateLabel is called, Then the result is "".
  - A completed task returns "CLEAR". `[Unit]`
    - Tip: Given isCompleted = true, When dueDateLabel is called, Then the result is "CLEAR".
- Error cases
  - None. Every combination of a nullable due date and a completion flag is valid. `[Unit]`
    - Tip: No call is rejected.
- Edge cases
  - Yesterday, today, and tomorrow sit on the boundary between overdue, due today, and upcoming. `[Unit]`
    - Tip: Given an active task due yesterday, today, or tomorrow, When dueDateLabel is called, Then the results are "D+1", "D-Day", and "D-1".

**Undefined behavior** (optional)
- The time of day is ignored because both inputs are dates.
