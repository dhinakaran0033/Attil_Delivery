package com.develop.sns.ViewOrderDetails.Dto

import com.google.gson.annotations.SerializedName


data class Payment (

  @SerializedName("paymentMode" ) var paymentMode : String? = null,
  @SerializedName("amount"      ) var amount      : Int?    = null,
  @SerializedName("STATUS"      ) var STATUS      : String? = null

)