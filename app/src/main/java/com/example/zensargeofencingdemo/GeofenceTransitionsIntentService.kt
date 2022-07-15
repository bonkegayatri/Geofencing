package com.example.zensargeofencingdemo

import android.app.IntentService
import android.app.Notification
import android.content.Intent
import com.example.zensargeofencingdemo.GeofenceTransitionsIntentService
import com.google.android.gms.location.GeofencingEvent
import com.google.android.gms.location.Geofence
import android.app.NotificationManager
import com.example.zensargeofencingdemo.MainActivity
import android.app.PendingIntent
import android.util.Log
import com.example.zensargeofencingdemo.R

class GeofenceTransitionsIntentService : IntentService("GeofenceTransitionsIntentService") {
    override fun onHandleIntent(intent: Intent?) {
        Log.i(TAG, "onHandleIntent")
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent.hasError()) {
            //String errorMessage = GeofenceErrorMessages.getErrorString(this,
            //      geofencingEvent.getErrorCode());
            Log.e(TAG, "Goefencing Error " + geofencingEvent.errorCode)
            return
        }

        // Get the transition type.
        val geofenceTransition = geofencingEvent.geofenceTransition
        Log.i(
            TAG,
            "geofenceTransition = " + geofenceTransition + " Enter : " + Geofence.GEOFENCE_TRANSITION_ENTER + "Exit : " + Geofence.GEOFENCE_TRANSITION_EXIT
        )
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {
            showNotification("Entered", "Entered the Location")
        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            Log.i(TAG, "Showing Notification...")
            showNotification("Exited", "Exited the Location")
        } else {
            // Log the error.
            showNotification("Error", "Error")
            Log.e(TAG, "Error ")
        }
    }

    fun showNotification(text: String?, bigText: String?) {

        // 1. Create a NotificationManager
        val notificationManager = this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // 2. Create a PendingIntent for AllGeofencesActivity
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingNotificationIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        // 3. Create and send a notification
        val notification = Notification.Builder(this)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Title")
            .setContentText(text)
            .setContentIntent(pendingNotificationIntent)
            .setStyle(Notification.BigTextStyle().bigText(bigText))
            .setPriority(Notification.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(0, notification)
    }

    companion object {
        private const val TAG = "GeofenceTransitions"
    }
}