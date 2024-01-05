package com.develop.sns.LocationLiveData

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.develop.sns.repository.Api
import com.develop.sns.repository.ApiRepository
import com.google.gson.JsonObject
import org.json.JSONObject

class LocationViewModel(application: Application) : AndroidViewModel(application) {

    private val locationData = LocationLiveData(application)
    private val apiRepository: ApiRepository = ApiRepository()
    private val api = Api.initRetrofit(application)

    fun getLocationData() = locationData

    fun updateLocation(requestObject: JsonObject, token: String): LiveData<JSONObject> {
        val call = api.updateLocation("Bearer $token", requestObject)
        return apiRepository.callApi(call)
    }
}
