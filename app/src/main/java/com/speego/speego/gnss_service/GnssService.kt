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
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.speego.speego.MainActivity

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

    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        setupGPS()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, createNotification())
        startGPS()
        return START_STICKY
    }

    private fun setupGPS() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationListener = LocationListener { location ->
            Log.d("GPS", "Lat: ${location.latitude}, Lon: ${location.longitude}")
            // Use your GPS data here
        }
    }

    private fun startGPS() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0.0f, locationListener)
        }
    }

    override fun onDestroy() {
        locationManager.removeUpdates(locationListener)
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
                NotificationManager.IMPORTANCE_DEFAULT
            )
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
