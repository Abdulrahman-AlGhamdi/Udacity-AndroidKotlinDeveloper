package com.udacity.project4.repository

import androidx.lifecycle.LiveData
import com.udacity.project4.model.ReminderModel
import com.udacity.project4.repository.RemindersLocalRepository.RemindersResult

interface RemindersRepository {

    suspend fun addReminder(reminder: ReminderModel)

    suspend fun getAllReminders(): RemindersResult<LiveData<List<ReminderModel>>>

    suspend fun deleteReminder(reminder: ReminderModel)

    suspend fun getReminder(id: Int): RemindersResult<ReminderModel>

    suspend fun deleteAllReminders()
}