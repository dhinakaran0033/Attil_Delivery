package com.develop.sns.Location

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.develop.sns.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class LocationService : Service() {
    private val NOTIFICATION_CHANNEL_ID = "my_notification_location"
    private val TAG = "LocationService"
    override fun onCreate() {
        super.onCreate()
        isServiceStarted = true
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setOngoing(false)
                .setSmallIcon(R.drawable.ic_launcher_background)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_ID, NotificationManager.IMPORTANCE_LOW
            )
            notificationChannel.description = NOTIFICATION_CHANNEL_ID
            notificationChannel.setSound(null, null)
            notificationManager.createNotificationChannel(notificationChannel)
            startForeground(1, builder.build())
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val timer = Timer()
        LocationHelper().startListeningUserLocation(
            this, object : MyLocationListener {
                override fun onLocationChanged(location: Location?) {
                    Log.e("test", location?.longitude.toString())
                    mLocation = location
                    mLocation?.let {
                        Toast.makeText(applicationContext,it.latitude.toString(),Toast.LENGTH_SHORT).show()
                        Log.e("Test", "onLocationChanged: Latitude ${it.latitude} , Longitude ${it.longitude}")
                        Log.d(TAG, "onLocationChanged: Latitude ${it.latitude} , Longitude ${it.longitude}")
                        Log.d(TAG, "run: Running = Location Update Successful")

                    }
                }
            })
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        isServiceStarted = false

    }

    companion object {
        var mLocation: Location? = null
        var isServiceStarted = false
    }
}