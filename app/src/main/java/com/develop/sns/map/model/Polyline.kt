package com.develop.sns.map.model

import com.google.gson.annotations.SerializedName

data class Polyline(
    @SerializedName("points")
    var points: String?
)