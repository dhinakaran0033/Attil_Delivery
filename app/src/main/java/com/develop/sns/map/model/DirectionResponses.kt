package com.develop.sns.map.model

import com.google.gson.annotations.SerializedName
import org.json.JSONObject

data class DirectionResponses(
    @SerializedName("geocoded_waypoints") val geocoded_waypoints : List<Geocoded_waypoints>,
    @SerializedName("routes")
    var routes: List<Route?>?,
    @SerializedName("status")
    var status: String?
)