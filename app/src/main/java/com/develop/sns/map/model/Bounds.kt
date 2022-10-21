package com.develop.sns.map.model

import com.google.gson.annotations.SerializedName

data class Bounds (

    @SerializedName("northeast") val northeast : Northeast,
    @SerializedName("southwest") val southwest : Southwest
)