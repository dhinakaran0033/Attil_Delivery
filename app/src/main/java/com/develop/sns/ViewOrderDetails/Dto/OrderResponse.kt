package com.develop.sns.ViewOrderDetails.Dto

import com.google.gson.annotations.SerializedName


data class OrderResponse (

  @SerializedName("code"    ) var code    : Int?            = null,
  @SerializedName("status"  ) var status  : Boolean?        = null,
  @SerializedName("message" ) var message : String?         = null,
  @SerializedName("data"    ) var data    : ArrayList<Data> = arrayListOf()

)