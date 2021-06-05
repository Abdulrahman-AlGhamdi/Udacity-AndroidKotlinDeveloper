package com.udacity.project4.ui.save

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.udacity.project4.model.ReminderModel

class SaveReminderViewModel : ViewModel() {

    val reminderTitle = MutableLiveData<String>()
    val reminderDescription = MutableLiveData<String>()

    fun addReminder(title: String?, description: String?) {
        if (title.isNullOrEmpty() || title.isBlank()) reminderTitle.value = ""
        else reminderTitle.value = title
        if (description.isNullOrEmpty()) reminderDescription.value = ""
        else reminderDescription.value = description
    }

    fun validateEnteredData(reminder: ReminderModel): Int {
        return if (reminder.title == null || reminder.title == "") 1
        else if (reminder.latitude == null && reminder.latitude == 0.0 && reminder.longitude == null && reminder.longitude == 0.0) 2
        else if (reminder.title != "" && reminder.latitude != null && reminder.longitude != null) 3
        else 4
    }
}