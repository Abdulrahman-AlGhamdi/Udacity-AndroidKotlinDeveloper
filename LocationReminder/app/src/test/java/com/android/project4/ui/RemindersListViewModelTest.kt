package com.android.project4.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.android.project4.TestCoroutineRule
import com.android.project4.getOrAwaitValueTest
import com.udacity.project4.model.ReminderModel
import com.android.project4.repository.FakeRemindersRepository
import com.google.common.truth.Truth.assertThat
import org.hamcrest.MatcherAssert.assertThat
import com.udacity.project4.ui.RemindersListViewModel
import com.udacity.project4.ui.RemindersListViewModel.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.Matchers.`is`
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    private lateinit var viewModel: RemindersListViewModel
    private lateinit var repository: FakeRemindersRepository

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var testCoroutineRule = TestCoroutineRule()

    @Before
    fun setup() {
        repository = FakeRemindersRepository()
        viewModel = RemindersListViewModel(repository)
    }

    @Test
    fun `insert reminder item, return true`() {
        runBlocking {
            val reminderItem = ReminderModel(
                id = 0,
                title = "title",
                description = "description",
                latitude = 1.0,
                longitude = 2.0
            )
            viewModel.saveReminder(reminderItem)
            viewModel.getAllReminders()
            val value = viewModel.getRemindersState.getOrAwaitValueTest()
            if (value is RemindersState.ReminderList) {
                assertThat(value.reminderList.isNotEmpty())
                assertThat(value.reminderList.size == 1)
                assertThat(value.reminderList.contains(reminderItem))
            }
        }
    }

    @Test
    fun `insert reminder item and then delete it, return true`() {
        runBlocking {
            val reminderItem = ReminderModel(
                id = 1,
                title = "title",
                description = "description",
                latitude = 1.0,
                longitude = 2.0
            )
            viewModel.saveReminder(reminderItem)
            viewModel.deleteAllReminders()
            viewModel.getAllReminders()
            val value = viewModel.getRemindersState.getOrAwaitValueTest()
            if (value is RemindersState.ReminderList) {
                assertThat(value.reminderList.isEmpty())
            }
        }
    }

    @Test
    fun `insert two reminder items and then delete them, return true`() {
        runBlocking {
            viewModel.deleteAllReminders()
            val reminderItem = ReminderModel(
                id = 2,
                title = "title",
                description = "description",
                latitude = 1.0,
                longitude = 2.0
            )
            val reminderItemTwo = ReminderModel(
                id = 3,
                title = "title",
                description = "description",
                latitude = 1.0,
                longitude = 2.0
            )
            viewModel.saveReminder(reminderItem)
            viewModel.saveReminder(reminderItemTwo)
            viewModel.deleteAllReminders()
            viewModel.getAllReminders()
            val value = viewModel.getRemindersState.getOrAwaitValueTest()
            if (value is RemindersState.ReminderList) {
                assertThat(value.reminderList.isEmpty())
            }
        }
    }

    @Test
    fun check_loading() {
        testCoroutineRule.runBlockingTest {
            testCoroutineRule.pauseDispatcher()
            viewModel.getAllReminders()
            assertThat(viewModel.getRemindersState.getOrAwaitValueTest(), `is`(RemindersState.Loading))
            testCoroutineRule.resumeDispatcher()
            assertThat(viewModel.getRemindersState.getOrAwaitValueTest(), `is`(RemindersState.Empty))
        }
    }

    @Test
    fun shouldReturnError() {
        testCoroutineRule.runBlockingTest {
            repository.shouldReturnError(true)
            viewModel.getAllReminders()
            assertThat(viewModel.getRemindersState.getOrAwaitValueTest(), `is`(RemindersState.Failed("Failed exception")))
        }
    }
}