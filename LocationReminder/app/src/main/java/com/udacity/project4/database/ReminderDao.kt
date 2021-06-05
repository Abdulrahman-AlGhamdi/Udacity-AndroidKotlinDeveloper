package com.udacity.project4.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.udacity.project4.model.ReminderModel
import com.udacity.project4.repository.RemindersLocalRepository
import com.udacity.project4.repository.RemindersLocalRepository.*

@Dao
interface ReminderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addReminder(reminderModel: ReminderModel)

    @Query("SELECT * FROM reminder")
    fun getAllReminders(): LiveData<List<ReminderModel>>

    @Query("SELECT * FROM reminder where id = :id")
    suspend fun getReminderById(id: Int): ReminderModel?

    @Delete
    suspend fun deleteReminder(reminderModel: ReminderModel)

    @Query("DELETE FROM reminder")
    suspend fun deleteAllReminders()
}
