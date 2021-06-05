package com.udacity.project4.database

import android.content.Context
import androidx.room.Room

object LocalDatabase {

    fun createRemindersDao(context: Context): ReminderDao {
        return Room.databaseBuilder(
            context.applicationContext,
            ReminderDatabase::class.java,
            ReminderDatabase.DATABASE_NAME
        ).build().reminderDao()
    }
}