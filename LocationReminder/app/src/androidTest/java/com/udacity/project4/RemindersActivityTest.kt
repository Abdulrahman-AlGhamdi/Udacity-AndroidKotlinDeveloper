package com.udacity.project4

import android.content.Context
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
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

@RunWith(AndroidJUnit4::class)
@LargeTest
class RemindersActivityTest : AutoCloseKoinTest() {

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
    fun endToEnd() {
        runBlocking {
            repository.addReminder(
                ReminderModel(
                    id = 0,
                    title = "title",
                    description = "description",
                    latitude = 1111.0,
                    longitude = 2222.0
                )
            )

            val scenario = ActivityScenario.launch(RemindersActivity::class.java)

            onView(withId(R.id.welcome)).check(matches(isDisplayed()))
            onView(withId(R.id.login)).check(matches(isDisplayed()))
            onView(withText("Welcome to Location Reminder")).check(matches(isDisplayed()))
            onView(withId(R.id.login)).perform(ViewActions.longClick())
            onView(withId(R.id.floating)).perform(ViewActions.click())
            onView(withId(R.id.save)).perform(ViewActions.click())
            onView(withText("Please select a location to save it")).inRoot(ToastMatcher()).check(matches(isDisplayed()))
            onView(withId(R.id.map)).perform(ViewActions.doubleClick())
            onView(withId(R.id.map)).perform(ViewActions.swipeLeft())
            onView(withId(R.id.map)).perform(ViewActions.swipeUp())
            onView(withId(R.id.map)).perform(ViewActions.longClick())
            onView(withId(R.id.save)).perform(ViewActions.click())
            onView(withId(R.id.save)).perform(ViewActions.click())
            onView(withId(com.google.android.material.R.id.snackbar_text)).check(matches(withText("Please add title")))
            onView(withId(R.id.title_text)).perform(ViewActions.typeText("Hello, place to visit"))
            onView(withId(R.id.title_text)).perform(ViewActions.closeSoftKeyboard())
            onView(withId(R.id.description_text)).perform(ViewActions.typeText("New wonderful place to noy tell my friends to come over. i wanna have fun alone"))
            onView(withId(R.id.description_text)).perform(ViewActions.closeSoftKeyboard())
            onView(withId(R.id.save)).perform(ViewActions.click())
            scenario.close()
        }
    }
}