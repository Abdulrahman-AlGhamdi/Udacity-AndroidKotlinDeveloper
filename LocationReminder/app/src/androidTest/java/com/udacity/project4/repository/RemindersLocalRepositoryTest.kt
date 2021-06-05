package com.udacity.project4.repository

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.AndroidTestCoroutineRule
import com.udacity.project4.database.ReminderDao
import com.udacity.project4.database.ReminderDatabase
import com.udacity.project4.getOrAwaitValue
import com.udacity.project4.model.ReminderModel
import com.google.common.truth.Truth
import com.udacity.project4.repository.RemindersLocalRepository
import com.udacity.project4.repository.RemindersLocalRepository.*
import com.udacity.project4.ui.RemindersListViewModel
import com.udacity.project4.ui.RemindersListViewModel.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Test

import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest
class RemindersLocalRepositoryTest {

    @get:Rule
    var mainCoroutineRule = AndroidTestCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var context: Context
    private lateinit var database: ReminderDatabase
    private lateinit var dao: ReminderDao
    private lateinit var repository: RemindersLocalRepository

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), ReminderDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.reminderDao()
        repository = RemindersLocalRepository(dao)

        runBlocking {
            repository.deleteAllReminders()
        }
    }

    @Test
    fun addReminder() {
        mainCoroutineRule.runBlockingTest {
            repository.deleteAllReminders()
            val reminderItem = ReminderModel(
                id = 0,
                title = "title",
                description = "description",
                latitude = 1.0,
                longitude = 2.0
            )
            repository.addReminder(reminderItem)
            val result = repository.getAllReminders() as RemindersResult.Success<LiveData<List<ReminderModel>>>
            val value = result.data.getOrAwaitValue()
            Truth.assertThat(value.isNotEmpty())
            Truth.assertThat(value.size == 1)
            Truth.assertThat(value.contains(reminderItem))
        }
    }

    @Test
    fun getAllReminders() {
        mainCoroutineRule.runBlockingTest {
            repository.deleteAllReminders()
            val reminderItemOne = ReminderModel(
                id = 1,
                title = "title",
                description = "description",
                latitude = 1.0,
                longitude = 2.0
            )
            val reminderItemTwo = ReminderModel(
                id = 2,
                title = "title",
                description = "description",
                latitude = 1.0,
                longitude = 2.0
            )
            repository.addReminder(reminderItemOne)
            repository.addReminder(reminderItemTwo)
            val result = repository.getAllReminders() as RemindersResult.Success<LiveData<List<ReminderModel>>>
            val value = result.data.getOrAwaitValue()
            Truth.assertThat(value.isNotEmpty())
            Truth.assertThat(value.size == 2)
            Truth.assertThat(value.contains(reminderItemOne) && value.contains(reminderItemTwo))
        }
    }

    @Test
    fun deleteReminder() {
        mainCoroutineRule.runBlockingTest {
            repository.deleteAllReminders()
            val reminderItem = ReminderModel(
                id = 3,
                title = "title",
                description = "description",
                latitude = 1.0,
                longitude = 2.0
            )
            repository.addReminder(reminderItem)
            repository.deleteReminder(reminderItem)
            val result = repository.getAllReminders() as RemindersResult.Success<LiveData<List<ReminderModel>>>
            val value = result.data.getOrAwaitValue()
            Truth.assertThat(value.isEmpty())
            Truth.assertThat(!value.contains(reminderItem))
        }
    }

    @Test
    fun deleteAllReminders() {
        mainCoroutineRule.runBlockingTest {
            repository.deleteAllReminders()
            val reminderItemOne = ReminderModel(
                id = 4,
                title = "title",
                description = "description",
                latitude = 1.0,
                longitude = 2.0
            )
            val reminderItemTwo = ReminderModel(
                id = 5,
                title = "title",
                description = "description",
                latitude = 1.0,
                longitude = 2.0
            )
            val reminderItemThree = ReminderModel(
                id = 2,
                title = "title",
                description = "description",
                latitude = 1.0,
                longitude = 2.0
            )
            repository.addReminder(reminderItemOne)
            repository.addReminder(reminderItemTwo)
            repository.addReminder(reminderItemThree)
            repository.deleteAllReminders()
            val result = repository.getAllReminders() as RemindersResult.Success<LiveData<List<ReminderModel>>>
            val value = result.data.getOrAwaitValue()
            Truth.assertThat(value.isEmpty())
            Truth.assertThat(!value.contains(reminderItemOne) && !value.contains(reminderItemTwo) && !value.contains(reminderItemThree))
        }
    }

    @Test
    fun noReminderFound() = mainCoroutineRule.runBlockingTest {
        val reminder = repository.getReminder(10) as RemindersResult.Failed
        MatcherAssert.assertThat(reminder.message, CoreMatchers.`is`("Reminder not found!"))
    }

    @After
    fun tearDown() {
        database.close()
    }
}