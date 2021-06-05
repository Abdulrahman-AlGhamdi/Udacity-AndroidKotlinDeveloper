package com.android.project4.ui.save

import com.udacity.project4.model.ReminderModel
import com.google.common.truth.Truth.assertThat
import com.udacity.project4.ui.save.SaveReminderViewModel
import org.junit.Before
import org.junit.Test

class SaveReminderViewModelTest {

    private lateinit var viewModel: SaveReminderViewModel

    @Before
    fun setup() {
        viewModel = SaveReminderViewModel()
    }

    @Test
    fun `enter a valid title, return true`() {
        val reminderModel = ReminderModel(title = "hello")
        val value = viewModel.validateEnteredData(reminderModel)
        assertThat(value == 2)
    }

    @Test
    fun `enter a not valid title, return false`() {
        val reminderModel = ReminderModel()
        val value = viewModel.validateEnteredData(reminderModel)
        assertThat(value == 1)
    }
}