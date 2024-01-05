package com.develop.sns.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.develop.sns.LocationLiveData.GpsUtils
import com.develop.sns.LocationLiveData.LocationViewModel
import com.develop.sns.map.GPS_REQUEST
import com.develop.sns.map.LOCATION_REQUEST
import com.develop.sns.map.MapViewModel
import com.google.gson.JsonObject

open class BaseFragment: Fragment() {

    private lateinit var locationViewModel: LocationViewModel
    private var isGPSEnabled = false
    var longitude:Double = 0.0
    var latitude:Double = 0.0
    private var preferenceHelper: PreferenceHelper? = null
    lateinit var accessToken1: String
    lateinit var carrierId1: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceHelper = PreferenceHelper(requireActivity())
        preferenceHelper = context?.let { PreferenceHelper(it) }
        accessToken1 = preferenceHelper!!.getValueFromSharedPrefs(AppConstant.KEY_TOKEN)!!
        carrierId1 = preferenceHelper!!.getValueFromSharedPrefs(AppConstant.KEY_CARRIER_ID)!!

        locationViewModel = ViewModelProviders.of(requireActivity()).get(LocationViewModel::class.java)
        GpsUtils(requireActivity()).turnGPSOn(object : GpsUtils.OnGpsListener {
            override fun gpsStatus(isGPSEnable: Boolean) {
                isGPSEnabled = isGPSEnable
            }
        })
    }


    override fun onStart() {
        super.onStart()
        invokeLocationAction()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GPS_REQUEST) {
                isGPSEnabled = true
                invokeLocationAction()
            }
        }
    }

    private fun invokeLocationAction() {
        when {
            //!isGPSEnabled -> latLong.text = getString(R.string.enable_gps)

            isPermissionsGranted() -> startLocationUpdate()

            shouldShowRequestPermissionRationale() -> Log.e("Test","Permision") /*latLong.text = getString(R.string.permission_request)*/

            else -> ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_REQUEST
            )
        }
    }

     fun startLocationUpdate() {
        locationViewModel.getLocationData().observe(this, Observer {
            //latLong.text =  getString(R.string.latLong, it.longitude, it.latitude)
            Log.e("Test2 dhina", it.longitude.toString())
            Log.e("Test2 karan", it.latitude.toString())
            longitude =  it.longitude
            latitude =  it.latitude
            Update_location()

        })
    }

    private fun Update_location() {
        if (AppUtils.isConnectedToInternet(requireActivity())) {
            val requestObject = JsonObject()
            requestObject.addProperty("carrierId", carrierId1)
            requestObject.addProperty("lng", longitude)
            requestObject.addProperty("lat", latitude)
            locationViewModel.updateLocation(requestObject,accessToken1)
                .observe(this, { jsonObject ->
                    if (jsonObject != null) {
                        Log.e("Test123","Test_innn")
                        Log.e("Test123",jsonObject.toString())
                    }
                })
        }
    }

    public fun isPermissionsGranted() =
        ActivityCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

    private fun shouldShowRequestPermissionRationale() =
        ActivityCompat.shouldShowRequestPermissionRationale(
            requireActivity(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) && ActivityCompat.shouldShowRequestPermissionRationale(
            requireActivity(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_REQUEST -> {
                invokeLocationAction()
            }
        }
    }
}