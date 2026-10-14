package com.example.swpp.tododapp.tasks

import android.view.View
import android.widget.TextView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.platform.app.InstrumentationRegistry
import com.example.swpp.tododapp.data.Task
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@MediumTest
@RunWith(AndroidJUnit4::class)
class TasksListBindingsTest {

    private lateinit var textView: TextView

    @Before
    fun setUp() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.runOnMainSync {
            textView = TextView(instrumentation.targetContext)
        }
    }

    @Test
    fun setDueDateLabel_activeDueToday_showsDDay() {
        // GIVEN - An active task due today
        val task = task(LocalDate.now(), completed = false)

        // WHEN - The row label is bound
        val shown = bind(task)

        // THEN - D-Day is visible
        assertEquals("D-Day", shown.text)
        assertEquals(View.VISIBLE, shown.visibility)
    }

    @Test
    fun setDueDateLabel_activeDueTomorrow_showsD1() {
        // GIVEN - An active task due on the next calendar day
        val task = task(LocalDate.now().plusDays(1), completed = false)

        // WHEN - The row label is bound
        val shown = bind(task)

        // THEN - D-1 is visible
        assertEquals("D-1", shown.text)
        assertEquals(View.VISIBLE, shown.visibility)
    }

    @Test
    fun setDueDateLabel_activeOverdueOneDay_showsDPlus1() {
        // GIVEN - An active task due yesterday
        val task = task(LocalDate.now().minusDays(1), completed = false)

        // WHEN - The row label is bound
        val shown = bind(task)

        // THEN - D+1 is visible
        assertEquals("D+1", shown.text)
        assertEquals(View.VISIBLE, shown.visibility)
    }

    @Test
    fun setDueDateLabel_completedWithDueDate_showsClear() {
        // GIVEN - A completed task that still has a due date
        val task = task(LocalDate.now().plusDays(6), completed = true)

        // WHEN - The row label is bound
        val shown = bind(task)

        // THEN - CLEAR is visible
        assertEquals("CLEAR", shown.text)
        assertEquals(View.VISIBLE, shown.visibility)
    }

    @Test
    fun setDueDateLabel_completedNoDueDate_showsClear() {
        // GIVEN - A completed task with no due date
        val task = task(null, completed = true)

        // WHEN - The row label is bound
        val shown = bind(task)

        // THEN - CLEAR is visible
        assertEquals("CLEAR", shown.text)
        assertEquals(View.VISIBLE, shown.visibility)
    }

    @Test
    fun setDueDateLabel_activeNoDueDate_hidesLabel() {
        // GIVEN - A fresh label view and an active task with no due date
        val task = task(null, completed = false)

        // WHEN - The row label is bound
        val shown = bind(task)

        // THEN - No label is shown
        assertEquals("", shown.text)
        assertEquals(View.GONE, shown.visibility)
    }

    @Test
    fun setDueDateLabel_nullTask_hidesLabel() {
        // GIVEN - A fresh label view and no task
        // WHEN - The row label is bound
        val shown = bind(null)

        // THEN - No label is shown
        assertEquals("", shown.text)
        assertEquals(View.GONE, shown.visibility)
    }

    @Test
    fun setDueDateLabel_uncheckCompletedNoDueDate_clearsStaleClear() {
        // GIVEN - The row already shows CLEAR for a completed task with no due date
        val completed = task(null, completed = true)
        bind(completed)
        val active = completed.copy(isCompleted = false)

        // WHEN - The task is unchecked and the same row is bound again
        val shown = bind(active)

        // THEN - CLEAR is gone and no label remains
        assertEquals("", shown.text)
        assertEquals(View.GONE, shown.visibility)
    }

    @Test
    fun setDueDateLabel_clearDueDate_clearsStaleLabel() {
        // GIVEN - The row already shows D-Day for an active task due today
        val withDueDate = task(LocalDate.now(), completed = false)
        bind(withDueDate)
        val cleared = withDueDate.copy(dueDate = null)

        // WHEN - The due date is cleared and the same row is bound again
        val shown = bind(cleared)

        // THEN - The previous D-day label is removed
        assertEquals("", shown.text)
        assertEquals(View.GONE, shown.visibility)
    }

    @Test
    fun setDueDateLabel_uncheckCompletedWithDueDate_showsDDay() {
        // GIVEN - The row shows CLEAR for a completed task due today
        val completed = task(LocalDate.now(), completed = true)
        bind(completed)
        val active = completed.copy(isCompleted = false)

        // WHEN - The task is unchecked and the same row is bound again
        val shown = bind(active)

        // THEN - CLEAR is replaced by D-Day
        assertEquals("D-Day", shown.text)
        assertEquals(View.VISIBLE, shown.visibility)
    }

    @Test
    fun setDueDateLabel_nullTaskAfterClear_clearsStaleLabel() {
        // GIVEN - The row already shows CLEAR
        bind(task(null, completed = true))

        // WHEN - The row is bound with no task
        val shown = bind(null)

        // THEN - The previous CLEAR label is removed
        assertEquals("", shown.text)
        assertEquals(View.GONE, shown.visibility)
    }

    @Test
    fun setDueDateLabel_activeDueDayBeforeMonthAnniversary_showsTotalDays() {
        // GIVEN - An active task due the day before the same day next month
        val today = LocalDate.now()
        val dueDate = today.plusMonths(1).minusDays(1)
        val task = task(dueDate, completed = false)

        // WHEN - The row label is bound
        val shown = bind(task)

        // THEN - The label is the total calendar-day count. On 2026-10-14 this is D-30.
        assertEquals(expectedFutureLabel(today, dueDate), shown.text)
        assertEquals(View.VISIBLE, shown.visibility)
    }

    @Test
    fun setDueDateLabel_activeDueOneMonthLater_showsTotalDays() {
        // GIVEN - An active task due on the same day next month
        val today = LocalDate.now()
        val dueDate = today.plusMonths(1)
        val task = task(dueDate, completed = false)

        // WHEN - The row label is bound
        val shown = bind(task)

        // THEN - The label is the total calendar-day count. On 2026-10-14 this is D-31.
        assertEquals(expectedFutureLabel(today, dueDate), shown.text)
        assertEquals(View.VISIBLE, shown.visibility)
    }

    @Test
    fun setDueDateLabel_activeDueDayAfterMonthAnniversary_showsTotalDays() {
        // GIVEN - An active task due the day after the same day next month
        val today = LocalDate.now()
        val dueDate = today.plusMonths(1).plusDays(1)
        val task = task(dueDate, completed = false)

        // WHEN - The row label is bound
        val shown = bind(task)

        // THEN - The label is the total calendar-day count. On 2026-10-14 this is D-32.
        assertEquals(expectedFutureLabel(today, dueDate), shown.text)
        assertEquals(View.VISIBLE, shown.visibility)
    }

    private fun task(dueDate: LocalDate?, completed: Boolean): Task {
        return Task(
            title = "Title",
            description = "Description",
            isCompleted = completed,
            id = "task-1",
            dueDate = dueDate
        )
    }

    private fun bind(task: Task?): ShownLabel {
        val shown = arrayOfNulls<ShownLabel>(1)
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            setDueDateLabel(textView, task)
            shown[0] = ShownLabel(textView.text.toString(), textView.visibility)
        }
        return shown[0]!!
    }

    private fun expectedFutureLabel(today: LocalDate, dueDate: LocalDate): String {
        val days = ChronoUnit.DAYS.between(today, dueDate)
        return "D-$days"
    }

    private data class ShownLabel(val text: String, val visibility: Int)
}
