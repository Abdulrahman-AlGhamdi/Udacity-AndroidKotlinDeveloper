package com.udacity.project4.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.project4.model.ReminderModel
import com.udacity.project4.repository.RemindersLocalRepository
import com.udacity.project4.repository.RemindersRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RemindersListViewModel(
    private val remindersRepository: RemindersRepository
) : ViewModel() {

    val getRemindersState = MutableLiveData<RemindersState>()

    suspend fun saveReminder(reminder: ReminderModel) = remindersRepository.addReminder(reminder)

    fun getAllReminders() {
        getRemindersState.value = RemindersState.Loading
        viewModelScope.launch {
            when (val result = remindersRepository.getAllReminders()) {
                is RemindersLocalRepository.RemindersResult.Failed -> {
                    getRemindersState.value = RemindersState.Failed(message = result.message)
                }
                is RemindersLocalRepository.RemindersResult.Success -> {
                    result.data.observeForever {
                        if (it.isEmpty()) getRemindersState.value = RemindersState.Empty
                        else getRemindersState.value = RemindersState.ReminderList(it)
                    }
                }
            }
        }
    }

    fun deleteReminder(reminder: ReminderModel) {
        viewModelScope.launch(Dispatchers.IO) {
            remindersRepository.deleteReminder(reminder)
        }
    }

    fun deleteAllReminders() {
        viewModelScope.launch(Dispatchers.IO) {
            remindersRepository.deleteAllReminders()
        }
    }

    sealed class RemindersState {
        object Loading : RemindersState()
        object Empty : RemindersState()
        data class Failed(val message: String?) : RemindersState()
        data class ReminderList(val reminderList: List<ReminderModel>) : RemindersState()
    }
}