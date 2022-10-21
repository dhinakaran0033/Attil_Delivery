package com.develop.sns.map.model

import com.google.gson.annotations.SerializedName

data class Geocoded_waypoints (

    @SerializedName("geocoder_status") val geocoder_status : String,
    @SerializedName("place_id") val place_id : String,
    @SerializedName("types") val types : List<String>
)