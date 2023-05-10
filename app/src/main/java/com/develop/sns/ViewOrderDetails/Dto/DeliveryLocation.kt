package com.develop.sns.ViewOrderDetails.Dto

import com.google.gson.annotations.SerializedName


data class DeliveryLocation (

  @SerializedName("lat" ) var lat : Double? = null,
  @SerializedName("lng" ) var lng : Double? = null

)