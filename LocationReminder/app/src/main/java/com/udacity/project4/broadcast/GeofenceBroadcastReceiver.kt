package com.udacity.project4.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
import com.udacity.project4.service.LocationReminderService

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        JobIntentService.enqueueWork(context, LocationReminderService::class.java, 2, intent)
    }
}