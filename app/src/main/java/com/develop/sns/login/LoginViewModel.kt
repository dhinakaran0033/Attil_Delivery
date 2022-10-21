package com.develop.sns.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.develop.sns.repository.Api
import com.develop.sns.repository.ApiRepository
import com.google.gson.JsonObject
import org.json.JSONObject

class LoginViewModel : ViewModel() {
    private val apiRepository: ApiRepository = ApiRepository()
    private val api = Api.initRetrofit()

    fun makeLogin(requestObject: JsonObject): LiveData<JSONObject> {
        val call = api.loginService("", requestObject)
        return apiRepository.callApi(call)
    }
}