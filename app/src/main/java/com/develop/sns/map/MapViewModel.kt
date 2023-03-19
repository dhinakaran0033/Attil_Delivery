package com.develop.sns.map

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.develop.sns.repository.Api
import com.develop.sns.repository.ApiRepository
import com.google.gson.JsonObject
import org.json.JSONObject

class MapViewModel(context: Context?): ViewModel() {

    private val apiRepository: ApiRepository = ApiRepository()
    private val api = Api.initRetrofit(context)

    fun getAcceptedAll(requestObject: JsonObject, token: String): LiveData<JSONObject> {
        val call = api.getAccepted("Bearer $token", requestObject)
        return apiRepository.callApi(call)
    }

    fun sendMapData(requestObject: JsonObject, token: String): LiveData<JSONObject> {
        val call = api.sendMapData("Bearer $token", requestObject)
        return apiRepository.callApi(call)
    }


}