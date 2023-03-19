package com.develop.sns.deliverypending

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.develop.sns.repository.Api
import com.develop.sns.repository.ApiRepository
import com.google.gson.JsonObject
import org.json.JSONObject

class DeliveryPendingViewModel(context: Context?)  : ViewModel() {

    private val apiRepository: ApiRepository = ApiRepository()
    private val api = Api.initRetrofit(context)

    fun getAccepted(requestObject: JsonObject, token: String): LiveData<JSONObject> {
        val call = api.getAccepted("Bearer $token", requestObject)
        return apiRepository.callApi(call)
    }

    fun setOrderStatus(requestObject: JsonObject, token: String): LiveData<JSONObject> {
        val call = api.setNotificationStatus("Bearer $token", requestObject)
        return apiRepository.callApi(call)
    }


}