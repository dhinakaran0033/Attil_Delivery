package com.develop.sns.map.model

import com.google.gson.annotations.SerializedName

data class OverviewPolyline(
    @SerializedName("points")
    var points: String?
)