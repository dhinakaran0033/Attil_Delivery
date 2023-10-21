package com.develop.sns.LocationLiveData

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat

object EnablingGPS {
    fun CheckLocationEnable(context: Context): Boolean {
        var gpsStatus: Boolean
        try {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

            if(gpsStatus){
                return true;
            }else{
                displayPromptForEnablingGPS(context)
            }

        } catch (ex: Exception) {
        }
        return false
    }


    fun displayPromptForEnablingGPS(context: Context) {
        val builder = AlertDialog.Builder(context)
        val action: String = Settings.ACTION_LOCATION_SOURCE_SETTINGS
        val message = "Do you want open GPS setting?"
        builder.setMessage(message)
        builder.setPositiveButton("Yes") { dialog, which ->
            dialog.dismiss()
            context.startActivity(Intent(action))
        }
        builder.setCancelable(false)
        builder.show()
    }

     fun checkPermission(context: Context): Boolean {
        val result = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val result1 = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
    }





}