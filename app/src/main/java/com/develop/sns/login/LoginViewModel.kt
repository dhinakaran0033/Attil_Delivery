package com.develop.sns.login

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.develop.sns.repository.Api
import com.develop.sns.repository.ApiRepository
import com.google.gson.JsonObject
import org.json.JSONObject

class LoginViewModel(context : Context) : ViewModel() {
    private val apiRepository: ApiRepository = ApiRepository()
    private val api = Api.initRetrofit(context)

    fun makeLogin(requestObject: JsonObject): LiveData<JSONObject> {
        val call = api.loginService("", requestObject)
        return apiRepository.callApi(call)
    }
}