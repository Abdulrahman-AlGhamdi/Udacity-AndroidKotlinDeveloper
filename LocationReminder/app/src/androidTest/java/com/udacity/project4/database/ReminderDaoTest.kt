package com.udacity.project4.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.getOrAwaitValue
import com.udacity.project4.model.ReminderModel
import com.google.common.truth.Truth.assertThat
import com.udacity.project4.database.ReminderDao
import com.udacity.project4.database.ReminderDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class ReminderDaoTest {

    private lateinit var database: ReminderDatabase
    private lateinit var dao: ReminderDao

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ReminderDatabase::class.java
        ).allowMainThreadQueries().build()

        dao = database.reminderDao()
    }

    @Test
    fun addReminderItem() = runBlockingTest {
        val reminderItem = ReminderModel(
            id = 0,
            title = "title",
            description = "description",
            latitude = 1.0,
            longitude = 2.0
        )
        dao.addReminder(reminderItem)
        val value = dao.getAllReminders().getOrAwaitValue()
        assertThat(value.contains(reminderItem))
    }

    @Test
    fun deleteReminderItem() = runBlockingTest {
        val reminderItem = ReminderModel(
            id = 0,
            title = "title",
            description = "description",
            latitude = 1.0,
            longitude = 2.0
        )
        dao.addReminder(reminderItem)
        dao.deleteReminder(reminderItem)
        val value = dao.getAllReminders().getOrAwaitValue()
        assertThat(!value.contains(reminderItem))
    }

    @Test
    fun getAllReminders() = runBlockingTest {
        val reminderItem = ReminderModel(
            id = 0,
            title = "title",
            description = "description",
            latitude = 1.0,
            longitude = 2.0
        )
        val reminderItemTwo = ReminderModel(
            id = 1,
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
        dao.addReminder(reminderItem)
        dao.addReminder(reminderItemTwo)
        dao.addReminder(reminderItemThree)

        val value = dao.getAllReminders().getOrAwaitValue()
        assertThat(value.isNotEmpty())
        assertThat(value.size == 3)
    }

    @After
    fun tearDown() {
        database.close()
    }
}