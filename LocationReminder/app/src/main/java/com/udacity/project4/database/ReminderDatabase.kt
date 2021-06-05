package com.udacity.project4.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.udacity.project4.model.ReminderModel

@Database(entities = [ReminderModel::class], version = 1, exportSchema = false)
abstract class ReminderDatabase : RoomDatabase() {

    abstract fun reminderDao(): ReminderDao

    companion object {
        const val DATABASE_NAME = "reminder_database"
    }
}