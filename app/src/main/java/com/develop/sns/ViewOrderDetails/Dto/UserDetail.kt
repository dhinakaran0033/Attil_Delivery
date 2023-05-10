package com.develop.sns.ViewOrderDetails.Dto

import com.google.gson.annotations.SerializedName


data class UserDetail (

  @SerializedName("username"    ) var username    : String? = null,
  @SerializedName("phoneNumber" ) var phoneNumber : String? = null

)