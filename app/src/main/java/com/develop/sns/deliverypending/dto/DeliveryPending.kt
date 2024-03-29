package com.develop.sns.deliverypending.dto

data class DeliveryPending(
    val code: Int,
    val `data`: List<DeliveryPendingData>,
    val message: String,
    val status: Boolean
){
    data class Bounds(
        val northeast: Northeast,
        val southwest: Southwest
    )

    data class Distance(
        val text: String,
        val value: Int
    )

    data class Duration(
        val text: String,
        val value: Int
    )

    data class EndLocation(
        val lat: Double,
        val lng: Double
    )

    data class GeocodedWaypoint(
        val geocoder_status: String,
        val place_id: String,
        val types: List<String>
    )

    data class DeliveryPendingData(
        val _id: String,
        val address: String,
        val currentLocation: Boolean,
        val deliveryLocation: DeliveryLocation,
        val landmark: String,
        val mapData: MapData,
        val orderDateTime: String,
        val orderId: String,
        val orderObjectId: String,
        val orderStatus: String,
        val payment: Payment,
        val phoneNumber: String,
        val pinCode: String,
        val shopInfo: ShopInfo,
        val townORcity: String,
        val type: String
    )

    data class DeliveryLocation(
        val lat: Double,
        val lng: Double
    )

    data class Northeast(
        val lat: Double,
        val lng: Double
    )

    data class Payment(
        val STATUS: String,
        val amount: Int,
        val paymentMode: String
    )

    data class Polyline(
        val points: String
    )

    data class Route(
        val bounds: Bounds,
        val copyrights: String,
        val legs: List<Leg>,
        val overview_polyline: OverviewPolyline,
        val summary: String,
        val warnings: List<Any>,
        val waypoint_order: List<Any>
    )

    data class ShopInfo(
        val _id: String,
        val serialNumber: String,
        val shopLocation: ShopLocation
    )

    data class ShopLocation(
        val lat: Double,
        val lng: Double
    )

    data class Southwest(
        val lat: Double,
        val lng: Double
    )

    data class StartLocation(
        val lat: Double,
        val lng: Double
    )

    data class Step(
        val distance: Distance,
        val duration: Duration,
        val end_location: EndLocation,
        val html_instructions: String,
        val maneuver: String,
        val polyline: Polyline,
        val start_location: StartLocation,
        val travel_mode: String
    )

    data class MapData(
        val geocoded_waypoints: List<GeocodedWaypoint>,
        val routes: List<Route>,
        val status: String
    )

    data class Leg(
        val distance: Distance,
        val duration: Duration,
        val end_address: String,
        val end_location: EndLocation,
        val start_address: String,
        val start_location: StartLocation,
        val steps: List<Step>,
        val traffic_speed_entry: List<Any>,
        val via_waypoint: List<Any>
    )

    data class OverviewPolyline(
        val points: String
    )
}