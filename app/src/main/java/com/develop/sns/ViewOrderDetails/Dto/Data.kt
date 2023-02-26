package com.develop.sns.ViewOrderDetails.Dto

import com.google.gson.annotations.SerializedName


data class Data (

  @SerializedName("_id"              ) var Id               : String?           = null,
  @SerializedName("payment"          ) var payment          : Payment?          = Payment(),
  @SerializedName("userDetail"       ) var userDetail       : UserDetail?       = UserDetail(),
  @SerializedName("orderObjectId"    ) var orderObjectId    : String?           = null,
  @SerializedName("orderId"          ) var orderId          : String?           = null,
  @SerializedName("currentLocation"  ) var currentLocation  : Boolean?          = null,
  @SerializedName("deliveryLocation" ) var deliveryLocation : DeliveryLocation? = DeliveryLocation(),
  @SerializedName("orderStatus"      ) var orderStatus      : String?           = null,
  @SerializedName("orderDateTime"    ) var orderDateTime    : String?           = null

)