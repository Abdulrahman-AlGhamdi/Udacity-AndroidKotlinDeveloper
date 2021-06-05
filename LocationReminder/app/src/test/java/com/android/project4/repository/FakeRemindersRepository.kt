package com.android.project4.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.udacity.project4.model.ReminderModel
import com.udacity.project4.repository.RemindersLocalRepository
import com.udacity.project4.repository.RemindersLocalRepository.*
import com.udacity.project4.repository.RemindersRepository

class FakeRemindersRepository : RemindersRepository {

    private var shouldReturnError = false
    private val reminderItems = mutableListOf<ReminderModel>()
    private val reminderList = MutableLiveData<List<ReminderModel>>(reminderItems)

    private fun refreshLiveData() {
        reminderList.postValue(reminderItems)
    }

    override suspend fun addReminder(reminder: ReminderModel) {
        reminderItems.add(reminder)
        refreshLiveData()
    }

    override suspend fun getAllReminders(): RemindersResult<LiveData<List<ReminderModel>>> {
        refreshLiveData()
        return if (shouldReturnError) {
            RemindersResult.Failed("Failed exception")
        } else try {
            RemindersResult.Success(reminderList)
        } catch (exception: Exception) {
            RemindersResult.Failed(exception.localizedMessage)
        }
    }

    override suspend fun deleteReminder(reminder: ReminderModel) {
        reminderItems.remove(reminder)
        refreshLiveData()
    }

    override suspend fun getReminder(id: Int): RemindersResult<ReminderModel> {
        refreshLiveData()
        return if (shouldReturnError) {
            RemindersResult.Failed("Failed exception")
        } else try {
            RemindersResult.Success(reminderItems[id])
        } catch (exception: Exception) {
            RemindersResult.Failed(exception.localizedMessage)
        }
    }

    override suspend fun deleteAllReminders() {
        reminderItems.clear()
        refreshLiveData()
    }

    fun shouldReturnError(value: Boolean) {
        shouldReturnError = value
    }
}