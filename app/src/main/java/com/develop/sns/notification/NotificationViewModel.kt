package com.develop.sns.notification

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.develop.sns.repository.Api
import com.develop.sns.repository.ApiRepository
import com.google.gson.JsonObject
import org.json.JSONObject

class NotificationViewModel  : ViewModel() {

    private val apiRepository: ApiRepository = ApiRepository()
    private val api = Api.initRetrofit()

    fun getNotification(requestObject: JsonObject, token: String): LiveData<JSONObject> {
        val call = api.notifications("Bearer $token", requestObject)
        return apiRepository.callApi(call)
    }

    fun setOrderStatus(requestObject: JsonObject, token: String): LiveData<JSONObject> {
        val call = api.setNotificationStatus("Bearer $token", requestObject)
        return apiRepository.callApi(call)
    }


}