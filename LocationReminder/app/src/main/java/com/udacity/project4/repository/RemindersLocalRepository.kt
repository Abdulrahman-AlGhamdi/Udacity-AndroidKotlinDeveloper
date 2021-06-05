package com.udacity.project4.repository

import androidx.lifecycle.LiveData
import com.udacity.project4.database.ReminderDao
import com.udacity.project4.model.ReminderModel

class RemindersLocalRepository(
    private val reminderDao: ReminderDao
) : RemindersRepository {

    override suspend fun addReminder(reminder: ReminderModel) = reminderDao.addReminder(reminder)

    override suspend fun getAllReminders(): RemindersResult<LiveData<List<ReminderModel>>> {
        return try {
            RemindersResult.Success(reminderDao.getAllReminders())
        } catch (exception: Exception) {
            RemindersResult.Failed(exception.localizedMessage)
        }
    }

    override suspend fun deleteReminder(reminder: ReminderModel) = reminderDao.deleteReminder(reminder)

    override suspend fun deleteAllReminders() = reminderDao.deleteAllReminders()

    override suspend fun getReminder(id: Int): RemindersResult<ReminderModel> {
        return try {
            val reminder = reminderDao.getReminderById(id)
            if (reminder != null) RemindersResult.Success(reminder)
            else RemindersResult.Failed("Reminder not found!")
        } catch (exception: Exception) {
            RemindersResult.Failed(exception.localizedMessage)
        }
    }

    sealed class RemindersResult<out T : Any> {
        data class Success<out T : Any>(val data: T) : RemindersResult<T>()
        data class Failed(val message: String?) : RemindersResult<Nothing>()
    }
}