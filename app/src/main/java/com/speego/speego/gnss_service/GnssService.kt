package com.speego.speego.gnss_service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.speego.speego.MainActivity
import com.speego.speego.database.TripCoordinate
import com.speego.speego.database.TripDatabaseInterface
import com.speego.speego.model.GlobalModel
import com.speego.speego.viewmodel.TripViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class GnssService : Service() {
    companion object {
        const val CHANNEL_ID = "GPS"
        const val NOTIFICATION_ID = 1

        fun startForegroundService(context: Context) {
            Log.i("GNSS Service", "Starting service")
            val serviceIntent = Intent(context, GnssService::class.java)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
        }

        fun stopForegroundService(context: Context) {
            Log.i("GNSS Service", "Stopping service")
            val serviceIntent = Intent(context, GnssService::class.java)
            context.stopService(serviceIntent)
        }
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener

    @OptIn(ExperimentalCoroutinesApi::class)
    private val locationScope = CoroutineScope(
        Dispatchers.IO.limitedParallelism(1) + SupervisorJob()
    )

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createNotificationChannel()
        //setupGPS()
        setupFusedLocation()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, createNotification())
        //startGPS()
        return START_STICKY
    }

    private fun setupFusedLocation() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
            .setMinUpdateDistanceMeters(0f)
            .build()

        val coordinateLiveData: MutableLiveData<TripCoordinate> = GlobalModel.getMutableCoordinateContainer()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationScope.launch {
                    for (location in locationResult.locations) {
                        Log.d("FastGPS", "Lat: ${location.latitude}, Lon: ${location.longitude}")
                        Log.d("FastGPS", "Provider: ${location.provider}, Accuracy: ${location.accuracy}m")
                        val newCoordinate: TripCoordinate = TripDatabaseInterface.createNewCoordinate(
                            tripStartTime = GlobalModel.getCurrentTripName(),
                            latitude = location.latitude,
                            longitude = location.longitude,
                            coordinateTime = location.time
                        )
                        coordinateLiveData.postValue(newCoordinate)
                    }
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Start live updates
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        }
    }

    private fun setupGPS() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationListener = LocationListener { location ->
            locationScope.launch {
                Log.d("GPS", "Lat: ${location.latitude}, Lon: ${location.longitude}")
                TripDatabaseInterface.createNewCoordinate(
                    tripStartTime = GlobalModel.getCurrentTripName(),
                    latitude = location.latitude,
                    longitude = location.longitude,
                    coordinateTime = location.time
                )
            }
        }
    }

    private fun startGPS() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0.0f, locationListener)
        }
    }

    override fun onDestroy() {
        //locationManager.removeUpdates(locationListener)
        fusedLocationClient.removeLocationUpdates(locationCallback)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "GPS",
                NotificationManager.IMPORTANCE_LOW
            )
            serviceChannel.setSound(null, null)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    private fun createNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("SpeeGo Active")
            .setContentText("SpeeGo is now tracking your position data.")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setContentIntent(pendingIntent)
            .build()
    }
}
