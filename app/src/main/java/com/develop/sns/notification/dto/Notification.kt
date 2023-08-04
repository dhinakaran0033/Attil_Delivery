package com.develop.sns.notification.dto

data class Notification(
    val code: Int,
    val `data`: List<NotificationData>,
    val message: String,
    val status: Boolean
){
    data class NotificationData(
        val _id: String,
        val address: String,
        val currentLocation: Boolean,
        val deliveryLocation: DeliveryLocation,
        val landmark: String,
        val notificationStatus: String,
        val orderDateTime: String,
        val orderId: String,
        val orderObjectId: String,
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

    data class Payment(
        val STATUS: String,
        val amount: Int,
        val paymentMode: String
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
}
