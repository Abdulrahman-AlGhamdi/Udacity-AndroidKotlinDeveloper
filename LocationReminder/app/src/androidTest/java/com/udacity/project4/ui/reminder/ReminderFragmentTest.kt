package com.udacity.project4.ui.reminder

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.android.project4.R
import com.firebase.ui.auth.AuthUI
import com.udacity.project4.database.LocalDatabase
import com.udacity.project4.model.ReminderModel
import com.udacity.project4.repository.RemindersLocalRepository
import com.udacity.project4.repository.RemindersRepository
import com.udacity.project4.ui.RemindersListViewModel
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.AutoCloseKoinTest
import org.koin.test.get
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
@MediumTest
class ReminderFragmentTest : AutoCloseKoinTest() {

    private lateinit var context: Context
    private lateinit var repository: RemindersRepository

    @Before
    fun setUp() {
        stopKoin()
        context = ApplicationProvider.getApplicationContext()
        AuthUI.getInstance().signOut(context)

        val appModule = module {
            single { LocalDatabase.createRemindersDao(context) }
            single { RemindersLocalRepository(get()) as RemindersRepository }
            viewModel { RemindersListViewModel(get()) }
        }

        startKoin { modules(listOf(appModule)) }

        repository = get()

        runBlocking {
            repository.deleteAllReminders()
        }
    }

    @Test
    fun displayReminderList() {
        runBlocking {
            val reminder = ReminderModel(
                id = 1,
                title = "title",
                description = "description",
                latitude = 0.0,
                longitude = 0.0
            )

            repository.addReminder(reminder)

            launchFragmentInContainer<ReminderFragment>(Bundle(), R.style.Theme_LocationReminder)
        }
    }

    @Test
    fun navigateToMapsFragment() {
        val navController = mock(NavController::class.java)
        val scenario = launchFragmentInContainer<ReminderFragment>(Bundle(), R.style.Theme_LocationReminder)
        scenario.onFragment { Navigation.setViewNavController(it.requireView(), navController) }
        onView(withId(R.id.floating)).perform(ViewActions.click())
        verify(navController).navigate(ReminderFragmentDirections.actionReminderFragmentToMapsFragment())
    }
}