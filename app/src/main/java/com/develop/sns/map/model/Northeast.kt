package com.develop.sns.map.model

import com.google.gson.annotations.SerializedName

data class Northeast(
    @SerializedName("lat")
    var lat: Double?,
    @SerializedName("lng")
    var lng: Double?
)