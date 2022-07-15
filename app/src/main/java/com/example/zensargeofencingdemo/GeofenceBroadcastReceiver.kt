package com.example.zensargeofencingdemo

import android.app.IntentService
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.GeofencingEvent
import com.google.android.gms.location.GeofenceStatusCodes
import com.example.zensargeofencingdemo.R
import com.google.android.gms.location.Geofence

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent.hasError()) {
            val errorMessage = GeofenceStatusCodes
                .getStatusCodeString(geofencingEvent.errorCode)
            Log.e("TAG", errorMessage)
            return
        }

        // Get the transition type.
        val geofenceTransition = geofencingEvent.geofenceTransition

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {
            Toast.makeText(context, "Entered and Dwelling in the Location",Toast.LENGTH_LONG).show()
            showNotification("Entered", "Entered the Location",context)
        }else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            Toast.makeText(context, "Exited the Location",Toast.LENGTH_LONG).show()
            Log.i("TAG", "Showing Notification...")
            showNotification("Exited", "Exited the Location",context)
        } else {
            // Log the error.
            showNotification("Error", "Error",context)
            Log.e("TAG", "Error ")
        }
        // Test that the reported transition was of interest.
      /*  if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
        geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            val triggeringGeofences = geofencingEvent.triggeringGeofences

            // Get the transition details as a String.
//            val geofenceTransitionDetails = getGeofenceTransitionDetails(
//                this,
//                geofenceTransition,
//                triggeringGeofences
//            )

            // Send notification and log the transition details.
//            sendNotification(geofenceTransitionDetails)
            Toast.makeText(context, "Receiver $geofenceTransition $triggeringGeofences",Toast.LENGTH_LONG).show()
            Log.i("Receiver TAG", "$geofenceTransition $triggeringGeofences")
        } else {
            // Log the error.
            Toast.makeText(context, "Receiver Invalid Type",Toast.LENGTH_LONG).show()
            Log.e("Receiver TAG", "Invalid Type")
        }*/
    }

    fun showNotification(text: String?, bigText: String?,context: Context) {

        // 1. Create a NotificationManager
        val notificationManager = context.getSystemService(IntentService.NOTIFICATION_SERVICE) as NotificationManager

        // 2. Create a PendingIntent for AllGeofencesActivity
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingNotificationIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        // 3. Create and send a notification
        val notification = Notification.Builder(context)
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

}