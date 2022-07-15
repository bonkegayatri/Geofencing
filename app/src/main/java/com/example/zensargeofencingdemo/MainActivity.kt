package com.example.zensargeofencingdemo

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GooglePlayServicesUtil
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import java.util.*


class MainActivity : AppCompatActivity() {

   /* var mGeofencePendingIntent: PendingIntent? = null
    val CONNECTION_FAILURE_RESOLUTION_REQUEST = 100
    var mGeofenceList: MutableList<Geofence>? = null
    var mGoogleApiClient: GoogleApiClient? = null
    val TAG = "Activity"
    var mLocationRequest: LocationRequest? = null
    var currentLatitude = 37.377166
    var currentLongitude = -122.086966

    var locationFound: Boolean? = false
    var locationManager: LocationManager? = null
    var locationListener: LocationListener? = null
*/
    lateinit var geofencingClient: GeofencingClient
    var mGeofenceList: MutableList<Geofence>? = mutableListOf()
    var currentLatitude = 18.4516
    var currentLongitude = 73.9149

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        geofencingClient = LocationServices.getGeofencingClient(this)

        createGeofenceObject()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }

        geofencingClient?.addGeofences(getGeofencingRequest(), geofencePendingIntent)?.run {
            addOnSuccessListener {
                // Geofences added
                Log.i("TAG", "Saving Geofence")
            }
            addOnFailureListener {
                // Failed to add geofences
                Log.i("TAG", "Failed to add geofences")
            }
        }



       /* if (savedInstanceState == null) {
            mGeofenceList = mutableListOf()
            val resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this)
            if (resp == ConnectionResult.SUCCESS) {
                initGoogleAPIClient()
                createGeofences(currentLatitude, currentLongitude)
            } else {
                Log.e(TAG, "Your Device doesn't support Google Play Services.")
            }

            // Create the LocationRequest object
            mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval((1 * 1000).toLong()) // 10 seconds, in milliseconds
                .setFastestInterval((1 * 1000).toLong()) // 1 second, in milliseconds

        }*/
    }


   /* override fun onStart() {
        super.onStart()
        if (mGoogleApiClient?.isConnecting == false || mGoogleApiClient?.isConnected == false) {
            mGoogleApiClient?.connect()
            Log.e(TAG, "mGoogleApiClient connected");
        }
    }*/

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun createGeofenceObject() {
        val id = UUID.randomUUID().toString()

        mGeofenceList?.add(Geofence.Builder()
            // Set the request ID of the geofence. This is a string to identify this
            // geofence.
            .setRequestId(id)

            // Set the circular region of this geofence.
            .setCircularRegion(
                currentLatitude,
                currentLongitude,
                500f
            )

            // Set the expiration duration of the geofence. This geofence gets automatically
            // removed after this period of time.
            .setExpirationDuration(Geofence.NEVER_EXPIRE)

            // Set the transition types of interest. Alerts are only generated for these
            // transition. We track entry and exit transitions in this sample.
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)

            // Create the geofence.
            .build())
    }

    private fun getGeofencingRequest(): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofences(mGeofenceList)
        }.build()
    }


     /*fun initGoogleAPIClient() {
         mGoogleApiClient = GoogleApiClient.Builder(this)
             .addApi(LocationServices.API)
             .addConnectionCallbacks(connectionAddListener)
             .addOnConnectionFailedListener(connectionFailedListener)
             .build()
     }

     private val connectionAddListener: GoogleApiClient.ConnectionCallbacks = object :
         GoogleApiClient.ConnectionCallbacks {
         override fun onConnected(bundle: Bundle?) {
             Log.i(TAG, "onConnected")
             var location: Location? = null
              if (ActivityCompat.checkSelfPermission(
                     applicationContext,
                     Manifest.permission.ACCESS_FINE_LOCATION
                 ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                     applicationContext,
                     Manifest.permission.ACCESS_COARSE_LOCATION
                 ) != PackageManager.PERMISSION_GRANTED
             ) {
                  location = null
             }else{

                 location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)

              }

             if (location == null) {
                 LocationServices.FusedLocationApi.requestLocationUpdates(
                     mGoogleApiClient,
                     mLocationRequest,
                     this@MainActivity
                 )
             } else {
                 //If everything went fine lets get latitude and longitude
                 currentLatitude = location.latitude
                 currentLongitude = location.longitude
                 Log.i(TAG, "$currentLatitude WORKS $currentLongitude")

                 //createGeofences(currentLatitude, currentLongitude);
                 //registerGeofences(mGeofenceList);
             }
             try {
                 LocationServices.GeofencingApi.addGeofences(
                     mGoogleApiClient,
                     getGeofencingRequest(),
                     getGeofencePendingIntent()
                 ).setResultCallback(ResultCallback<Status> { status ->
                     if (status.isSuccess) {
                         Log.i(TAG, "Saving Geofence")
                     } else {
                         Log.e(
                             TAG,
                             "Registering geofence failed: " + status.statusMessage +
                                     " : " + status.statusCode
                         )
                     }
                 })
             } catch (securityException: SecurityException) {
                 // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
                 Log.e(TAG, "Error")
             }
         }

         override fun onConnectionSuspended(i: Int) {
             Log.e(TAG, "onConnectionSuspended")
         }
     }

     private val connectionFailedListener =
         GoogleApiClient.OnConnectionFailedListener { Log.e(TAG, "onConnectionFailed") }

     private fun getGeofencingRequest(): GeofencingRequest? {
         val builder = GeofencingRequest.Builder()
         builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
         builder.addGeofences(mGeofenceList)
         return builder.build()
     }

     private fun getGeofencePendingIntent(): PendingIntent? {
         // Reuse the PendingIntent if we already have it.
         if (mGeofencePendingIntent != null) {
             return mGeofencePendingIntent
         }
         val intent = Intent(this, GeofenceTransitionsIntentService::class.java)
         // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
         // calling addGeofences() and removeGeofences().
         return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
     }

     fun createGeofences(latitude: Double, longitude: Double) {
         val id = UUID.randomUUID().toString()
         val fence = Geofence.Builder()
             .setRequestId(id)
             .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
             .setCircularRegion(latitude, longitude, 200f)
             .setExpirationDuration(Geofence.NEVER_EXPIRE)
             .build()
         mGeofenceList?.add(fence)
     }

     override fun onLocationChanged(location: Location?) {
         if (location != null) {
             currentLatitude = location.getLatitude()
         }
         if (location != null) {
             currentLongitude = location.getLongitude()
         }
         Log.i(TAG, "onLocationChanged")
     }*/
}