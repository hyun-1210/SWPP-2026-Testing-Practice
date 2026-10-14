package com.example.swpp.tododapp.tasks

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class DueDateUtilsTest {

    @Test
    fun dueDateLabel_activeDueToday_returnsDDay() {
        // GIVEN - An active task due on the same calendar day
        val today = LocalDate.of(2026, 10, 14)
        val dueDate = LocalDate.of(2026, 10, 14)

        // WHEN - The label is computed
        val result = dueDateLabel(dueDate, today, false)

        // THEN - The label is D-Day
        assertEquals("D-Day", result)
    }

    @Test
    fun dueDateLabel_activeDueIn3Days_returnsD3() {
        // GIVEN - An active task due three calendar days later, still in October
        val today = LocalDate.of(2026, 10, 14)
        val dueDate = LocalDate.of(2026, 10, 17)

        // WHEN - The label is computed
        val result = dueDateLabel(dueDate, today, false)

        // THEN - The label counts those three days
        assertEquals("D-3", result)
    }

    @Test
    fun dueDateLabel_activeOverdue2Days_returnsDPlus2() {
        // GIVEN - An active task that was due two calendar days ago
        val today = LocalDate.of(2026, 10, 14)
        val dueDate = LocalDate.of(2026, 10, 12)

        // WHEN - The label is computed
        val result = dueDateLabel(dueDate, today, false)

        // THEN - The label is overdue by two days
        assertEquals("D+2", result)
    }

    @Test
    fun dueDateLabel_activeNoDueDate_returnsEmpty() {
        // GIVEN - An active task with no due date
        val today = LocalDate.of(2026, 10, 14)

        // WHEN - The label is computed
        val result = dueDateLabel(null, today, false)

        // THEN - Nothing is shown
        assertEquals("", result)
    }

    @Test
    fun dueDateLabel_completedWithDueDate_returnsClear() {
        // GIVEN - A completed task that still has a due date
        val today = LocalDate.of(2026, 10, 14)
        val dueDate = LocalDate.of(2026, 10, 20)

        // WHEN - The label is computed
        val result = dueDateLabel(dueDate, today, true)

        // THEN - Completion wins over the day count
        assertEquals("CLEAR", result)
    }

    @Test
    fun dueDateLabel_completedNoDueDate_returnsClear() {
        // GIVEN - A completed task with no due date
        val today = LocalDate.of(2026, 10, 14)

        // WHEN - The label is computed
        val result = dueDateLabel(null, today, true)

        // THEN - The label is still CLEAR
        assertEquals("CLEAR", result)
    }

    @Test
    fun dueDateLabel_activeDueYesterday_returnsDPlus1() {
        // GIVEN - An active task due the calendar day before today
        val today = LocalDate.of(2026, 10, 14)
        val dueDate = LocalDate.of(2026, 10, 13)

        // WHEN - The label is computed
        val result = dueDateLabel(dueDate, today, false)

        // THEN - One day overdue
        assertEquals("D+1", result)
    }

    @Test
    fun dueDateLabel_activeDueTomorrow_returnsD1() {
        // GIVEN - An active task due the next calendar day
        val today = LocalDate.of(2026, 10, 14)
        val dueDate = LocalDate.of(2026, 10, 15)

        // WHEN - The label is computed
        val result = dueDateLabel(dueDate, today, false)

        // THEN - Due in one day
        assertEquals("D-1", result)
    }

    @Test
    fun dueDateLabel_activeDueDayBeforeMonthAnniversary_returnsD30() {
        // GIVEN - The day before the same day next month
        val today = LocalDate.of(2026, 10, 14)
        val dueDate = LocalDate.of(2026, 11, 13)

        // WHEN - The label is computed
        val result = dueDateLabel(dueDate, today, false)

        // THEN - 30 calendar days remain
        assertEquals("D-30", result)
    }

    @Test
    fun dueDateLabel_activeDueOneMonthLater_returnsD31() {
        // GIVEN - The same day next month. October 14 to November 14 is 31 calendar days.
        val today = LocalDate.of(2026, 10, 14)
        val dueDate = LocalDate.of(2026, 11, 14)

        // WHEN - The label is computed
        val result = dueDateLabel(dueDate, today, false)

        // THEN - The label uses the total day count, not the day-of-month difference
        assertEquals("D-31", result)
    }

    @Test
    fun dueDateLabel_activeDueDayAfterMonthAnniversary_returnsD32() {
        // GIVEN - The day after the same day next month
        val today = LocalDate.of(2026, 10, 14)
        val dueDate = LocalDate.of(2026, 11, 15)

        // WHEN - The label is computed
        val result = dueDateLabel(dueDate, today, false)

        // THEN - 32 calendar days remain
        assertEquals("D-32", result)
    }

    @Test
    fun dueDateLabel_activeOverdueDayBeforeMonth_returnsDPlus30() {
        // GIVEN - One day after the same day last month
        val today = LocalDate.of(2026, 11, 14)
        val dueDate = LocalDate.of(2026, 10, 15)

        // WHEN - The label is computed
        val result = dueDateLabel(dueDate, today, false)

        // THEN - Overdue by 30 calendar days
        assertEquals("D+30", result)
    }

    @Test
    fun dueDateLabel_activeOverdueOneMonth_returnsDPlus31() {
        // GIVEN - Due on the same day last month. November 14 back to October 14 is 31 days.
        val today = LocalDate.of(2026, 11, 14)
        val dueDate = LocalDate.of(2026, 10, 14)

        // WHEN - The label is computed
        val result = dueDateLabel(dueDate, today, false)

        // THEN - Overdue by the total day count
        assertEquals("D+31", result)
    }

    @Test
    fun dueDateLabel_activeOverdueDayAfterMonth_returnsDPlus32() {
        // GIVEN - Due the day before the same day last month
        val today = LocalDate.of(2026, 11, 14)
        val dueDate = LocalDate.of(2026, 10, 13)

        // WHEN - The label is computed
        val result = dueDateLabel(dueDate, today, false)

        // THEN - Overdue by 32 calendar days
        assertEquals("D+32", result)
    }

    @Test
    fun dueDateLabel_activeDueAcrossNewYear_returnsD1() {
        // GIVEN - Due on the next calendar day, which is also the next year
        val today = LocalDate.of(2026, 12, 31)
        val dueDate = LocalDate.of(2027, 1, 1)

        // WHEN - The label is computed
        val result = dueDateLabel(dueDate, today, false)

        // THEN - One calendar day remains
        assertEquals("D-1", result)
    }

    @Test
    fun dueDateLabel_activeDueDayBeforeYearAnniversary_returnsD30() {
        // GIVEN - The day before the same day next month, across the new year
        val today = LocalDate.of(2026, 12, 31)
        val dueDate = LocalDate.of(2027, 1, 30)

        // WHEN - The label is computed
        val result = dueDateLabel(dueDate, today, false)

        // THEN - 30 calendar days remain
        assertEquals("D-30", result)
    }

    @Test
    fun dueDateLabel_activeDueOneMonthIntoNextYear_returnsD31() {
        // GIVEN - The same day next month, across the new year
        val today = LocalDate.of(2026, 12, 31)
        val dueDate = LocalDate.of(2027, 1, 31)

        // WHEN - The label is computed
        val result = dueDateLabel(dueDate, today, false)

        // THEN - 31 calendar days remain
        assertEquals("D-31", result)
    }

    @Test
    fun dueDateLabel_activeDueDayAfterYearAnniversary_returnsD32() {
        // GIVEN - The day after the same day next month, across the new year
        val today = LocalDate.of(2026, 12, 31)
        val dueDate = LocalDate.of(2027, 2, 1)

        // WHEN - The label is computed
        val result = dueDateLabel(dueDate, today, false)

        // THEN - 32 calendar days remain
        assertEquals("D-32", result)
    }

    @Test
    fun dueDateLabel_activeDueOnLeapDay_returnsD1() {
        // GIVEN - 2024 is a leap year and the due date is the leap day
        val today = LocalDate.of(2024, 2, 28)
        val dueDate = LocalDate.of(2024, 2, 29)

        // WHEN - The label is computed
        val result = dueDateLabel(dueDate, today, false)

        // THEN - The leap day itself is one calendar day away
        assertEquals("D-1", result)
    }

    @Test
    fun dueDateLabel_activeDueDayAfterLeapDay_returnsD2() {
        // GIVEN - The due date is the day after the leap day
        val today = LocalDate.of(2024, 2, 28)
        val dueDate = LocalDate.of(2024, 3, 1)

        // WHEN - The label is computed
        val result = dueDateLabel(dueDate, today, false)

        // THEN - The leap day is included, so two calendar days remain
        assertEquals("D-2", result)
    }

    @Test
    fun dueDateLabel_activeDueOneMonthAcrossLeapDay_returnsD29() {
        // GIVEN - The same day next month, and February 2024 has a leap day
        val today = LocalDate.of(2024, 2, 28)
        val dueDate = LocalDate.of(2024, 3, 28)

        // WHEN - The label is computed
        val result = dueDateLabel(dueDate, today, false)

        // THEN - 29 calendar days remain
        assertEquals("D-29", result)
    }

    @Test
    fun dueDateLabel_activeDueDayBeforeShortMonthEnd_returnsD27() {
        // GIVEN - 2026 is not a leap year. The day before the clamped end of February.
        val today = LocalDate.of(2026, 1, 31)
        val dueDate = LocalDate.of(2026, 2, 27)

        // WHEN - The label is computed
        val result = dueDateLabel(dueDate, today, false)

        // THEN - 27 calendar days remain
        assertEquals("D-27", result)
    }

    @Test
    fun dueDateLabel_activeDueOnShortMonthEnd_returnsD28() {
        // GIVEN - January 31 to the last day of a non-leap February
        val today = LocalDate.of(2026, 1, 31)
        val dueDate = LocalDate.of(2026, 2, 28)

        // WHEN - The label is computed
        val result = dueDateLabel(dueDate, today, false)

        // THEN - 28 calendar days remain
        assertEquals("D-28", result)
    }

    @Test
    fun dueDateLabel_activeDueDayAfterShortMonth_returnsD29() {
        // GIVEN - January 31 to the day after the short month ends
        val today = LocalDate.of(2026, 1, 31)
        val dueDate = LocalDate.of(2026, 3, 1)

        // WHEN - The label is computed
        val result = dueDateLabel(dueDate, today, false)

        // THEN - 29 calendar days remain
        assertEquals("D-29", result)
    }
}
