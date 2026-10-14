# Spec: `DueDateUtils.kt`

> Post-conditions are the test oracle: write the expected behavior from intent, not from the code.
> Tag each post-condition [Unit] if its test runs on the JVM; otherwise tag it [Integration].
> End each valid case with `Tested by <test>` if an existing test checks it, or `Tip:` describing the test to write.

## `dueDateLabel(dueDate: LocalDate?, today: LocalDate, isCompleted: Boolean): String`

**Intent**: Return the list label for one task. Completed tasks return "CLEAR" whether or not they have a due date. An active task with no due date returns "". Otherwise N is the total number of calendar days from `today` to `dueDate` (`ChronoUnit.DAYS.between(today, dueDate)`), counting every date across month ends, year ends, and leap days. N = 0 returns "D-Day", N > 0 returns "D-N", and N < 0 returns "D+" plus the positive day count.

**Pre-conditions**
- `today` and, when present, `dueDate` are calendar dates with no time of day.
- `dueDate == null` means the task has no due date. It is valid input, not an error.
- `isCompleted` is the task's completion flag. Completion is decided before the day count.

**Post-conditions**
- Success cases
  - An active task due on the same calendar day returns "D-Day". `[Unit]`
    - Tip: Given today = 2026-10-14, dueDate = 2026-10-14, isCompleted = false, When dueDateLabel is called, Then the result is "D-Day". Tested by `DueDateUtilsTest.dueDateLabel_activeDueToday_returnsDDay`.
  - An active task due in a later day of the same month returns "D-N" for the total calendar days. `[Unit]`
    - Tip: Given today = 2026-10-14, dueDate = 2026-10-17, isCompleted = false, When dueDateLabel is called, Then the result is "D-3". Tested by `DueDateUtilsTest.dueDateLabel_activeDueIn3Days_returnsD3`.
  - An active task overdue within the same month returns "D+N" for the total calendar days. `[Unit]`
    - Tip: Given today = 2026-10-14, dueDate = 2026-10-12, isCompleted = false, When dueDateLabel is called, Then the result is "D+2". Tested by `DueDateUtilsTest.dueDateLabel_activeOverdue2Days_returnsDPlus2`.
  - An active task with no due date returns an empty string, so the list shows no label. `[Unit]`
    - Tip: Given today = 2026-10-14, dueDate = null, isCompleted = false, When dueDateLabel is called, Then the result is "". Tested by `DueDateUtilsTest.dueDateLabel_activeNoDueDate_returnsEmpty`.
  - A completed task with a due date returns "CLEAR", not a D-day count. `[Unit]`
    - Tip: Given today = 2026-10-14, dueDate = 2026-10-20, isCompleted = true, When dueDateLabel is called, Then the result is "CLEAR". Tested by `DueDateUtilsTest.dueDateLabel_completedWithDueDate_returnsClear`.
  - A completed task with no due date returns "CLEAR". `[Unit]`
    - Tip: Given today = 2026-10-14, dueDate = null, isCompleted = true, When dueDateLabel is called, Then the result is "CLEAR". Tested by `DueDateUtilsTest.dueDateLabel_completedNoDueDate_returnsClear`.
- Error cases
  - None. A null due date, a past date, a future date, and either completion flag are all defined. `[Unit]`
    - Tip: Given today = 2026-10-14, dueDate = null, isCompleted = false, When dueDateLabel is called, Then the call returns "" and does not throw. Tested by `DueDateUtilsTest.dueDateLabel_activeNoDueDate_returnsEmpty`.
- Edge cases
  - One day before today, today, and one day after today are the boundary between D+N, D-Day, and D-N. `[Unit]`
    - Tip: Given today = 2026-10-14, dueDate = 2026-10-13, isCompleted = false, When dueDateLabel is called, Then the result is "D+1". Tested by `DueDateUtilsTest.dueDateLabel_activeDueYesterday_returnsDPlus1`.
    - Tip: Given today = 2026-10-14, dueDate = 2026-10-14, isCompleted = false, When dueDateLabel is called, Then the result is "D-Day". Tested by `DueDateUtilsTest.dueDateLabel_activeDueToday_returnsDDay`.
    - Tip: Given today = 2026-10-14, dueDate = 2026-10-15, isCompleted = false, When dueDateLabel is called, Then the result is "D-1". Tested by `DueDateUtilsTest.dueDateLabel_activeDueTomorrow_returnsD1`.
  - The month anniversary is a boundary. The day before it, the anniversary, and the day after it all use the total calendar-day count. 2026-10-14 to 2026-11-14 is 31 days, not 0. `[Unit]`
    - Tip: Given today = 2026-10-14, dueDate = 2026-11-13, isCompleted = false, When dueDateLabel is called, Then the result is "D-30". Tested by `DueDateUtilsTest.dueDateLabel_activeDueDayBeforeMonthAnniversary_returnsD30`.
    - Tip: Given today = 2026-10-14, dueDate = 2026-11-14, isCompleted = false, When dueDateLabel is called, Then the result is "D-31". Tested by `DueDateUtilsTest.dueDateLabel_activeDueOneMonthLater_returnsD31`.
    - Tip: Given today = 2026-10-14, dueDate = 2026-11-15, isCompleted = false, When dueDateLabel is called, Then the result is "D-32". Tested by `DueDateUtilsTest.dueDateLabel_activeDueDayAfterMonthAnniversary_returnsD32`.
  - The same three-day window on the overdue side of a month boundary uses the total day count. 2026-11-14 back to 2026-10-14 is 31 days overdue, not 0. `[Unit]`
    - Tip: Given today = 2026-11-14, dueDate = 2026-10-15, isCompleted = false, When dueDateLabel is called, Then the result is "D+30". Tested by `DueDateUtilsTest.dueDateLabel_activeOverdueDayBeforeMonth_returnsDPlus30`.
    - Tip: Given today = 2026-11-14, dueDate = 2026-10-14, isCompleted = false, When dueDateLabel is called, Then the result is "D+31". Tested by `DueDateUtilsTest.dueDateLabel_activeOverdueOneMonth_returnsDPlus31`.
    - Tip: Given today = 2026-11-14, dueDate = 2026-10-13, isCompleted = false, When dueDateLabel is called, Then the result is "D+32". Tested by `DueDateUtilsTest.dueDateLabel_activeOverdueDayAfterMonth_returnsDPlus32`.
  - A year boundary has the same shape: the next calendar day, the day before the month anniversary, the anniversary, and the day after it. `[Unit]`
    - Tip: Given today = 2026-12-31, dueDate = 2027-01-01, isCompleted = false, When dueDateLabel is called, Then the result is "D-1". Tested by `DueDateUtilsTest.dueDateLabel_activeDueAcrossNewYear_returnsD1`.
    - Tip: Given today = 2026-12-31, dueDate = 2027-01-30, isCompleted = false, When dueDateLabel is called, Then the result is "D-30". Tested by `DueDateUtilsTest.dueDateLabel_activeDueDayBeforeYearAnniversary_returnsD30`.
    - Tip: Given today = 2026-12-31, dueDate = 2027-01-31, isCompleted = false, When dueDateLabel is called, Then the result is "D-31". Tested by `DueDateUtilsTest.dueDateLabel_activeDueOneMonthIntoNextYear_returnsD31`.
    - Tip: Given today = 2026-12-31, dueDate = 2027-02-01, isCompleted = false, When dueDateLabel is called, Then the result is "D-32". Tested by `DueDateUtilsTest.dueDateLabel_activeDueDayAfterYearAnniversary_returnsD32`.
  - A leap day counts as a calendar day. 2024-02-28 to 2024-03-28 is 29 days, not 0. `[Unit]`
    - Tip: Given today = 2024-02-28, dueDate = 2024-02-29, isCompleted = false, When dueDateLabel is called, Then the result is "D-1". Tested by `DueDateUtilsTest.dueDateLabel_activeDueOnLeapDay_returnsD1`.
    - Tip: Given today = 2024-02-28, dueDate = 2024-03-01, isCompleted = false, When dueDateLabel is called, Then the result is "D-2". Tested by `DueDateUtilsTest.dueDateLabel_activeDueDayAfterLeapDay_returnsD2`.
    - Tip: Given today = 2024-02-28, dueDate = 2024-03-28, isCompleted = false, When dueDateLabel is called, Then the result is "D-29". Tested by `DueDateUtilsTest.dueDateLabel_activeDueOneMonthAcrossLeapDay_returnsD29`.
  - A short month is a boundary too. 2026 is not a leap year, so 2026-01-31 to 2026-02-28 is 28 days and 2026-01-31 to 2026-03-01 is 29 days. `[Unit]`
    - Tip: Given today = 2026-01-31, dueDate = 2026-02-27, isCompleted = false, When dueDateLabel is called, Then the result is "D-27". Tested by `DueDateUtilsTest.dueDateLabel_activeDueDayBeforeShortMonthEnd_returnsD27`.
    - Tip: Given today = 2026-01-31, dueDate = 2026-02-28, isCompleted = false, When dueDateLabel is called, Then the result is "D-28". Tested by `DueDateUtilsTest.dueDateLabel_activeDueOnShortMonthEnd_returnsD28`.
    - Tip: Given today = 2026-01-31, dueDate = 2026-03-01, isCompleted = false, When dueDateLabel is called, Then the result is "D-29". Tested by `DueDateUtilsTest.dueDateLabel_activeDueDayAfterShortMonth_returnsD29`.

**Undefined behavior** (optional)
- Instants and time zones are undefined. The function compares dates only.
- Labels other than "", "CLEAR", "D-Day", "D-N", and "D+N" are undefined.
