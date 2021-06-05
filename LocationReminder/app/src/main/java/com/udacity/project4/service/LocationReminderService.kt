package com.udacity.project4.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.android.project4.R
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.udacity.project4.repository.RemindersLocalRepository.RemindersResult
import com.udacity.project4.repository.RemindersRepository
import com.udacity.project4.ui.details.DetailsActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class LocationReminderService : JobIntentService() {

    private val remindersLocalRepository: RemindersRepository by inject()

    override fun onHandleWork(intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)

        if (geofencingEvent.geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
            geofencingEvent.geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {
            showNotification(this, geofencingEvent.triggeringGeofences)
        }
    }

    private fun showNotification(context: Context, triggeringGeofences: MutableList<Geofence>) {
        val requestId = triggeringGeofences[0].requestId

        GlobalScope.launch(Dispatchers.Main) {
            val result = remindersLocalRepository.getReminder(requestId.toInt())
            if (result is RemindersResult.Success) {

                val pendingIntent: PendingIntent by lazy {
                    val intent = Intent(context, DetailsActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .putExtra(REMINDER, result.data)

                    PendingIntent.getActivity(
                        context,
                        1,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                }

                val action = NotificationCompat.Action.Builder(
                    0,
                    "Show reminder details",
                    pendingIntent
                ).build()

                val build = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText(result.data.title)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .addAction(action)
                    .build()

                NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, build)
                this.cancel()
            }
        }
    }

    companion object {
        const val REMINDER = "reminder"
        private const val NOTIFICATION_ID = 0
        private const val CHANNEL_ID = "channel_id"
    }
}