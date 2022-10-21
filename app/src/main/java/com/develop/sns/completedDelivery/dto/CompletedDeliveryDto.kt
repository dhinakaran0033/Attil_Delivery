package com.develop.sns.completedDelivery.dto

import java.io.Serializable

class CompletedDeliveryDto : Serializable {

    var id: String = ""
    var orderObjectId: String = ""
    var notificationStatus: String = ""
    var orderId: String = ""
    var currentLocation: Boolean = false
    var type: String = ""
    var orderDateTime: String = ""
    var address: String = ""
    var landmark: String = ""
    var townORcity: String = ""
    var pinCode: String = ""
    var phoneNumber: String = ""
    var deliveryLat: Double = 0.0
    var deliveryLng: Double = 0.0
    var amount: Double = 0.0
    var paymentMode: String = ""
    var paymentstatus: String = ""
    var shopLat: Double = 0.0
    var shopLng: Double = 0.0
    var shopID: String = ""
    var serialNumber: String = ""

    override fun toString(): String {
        return id
    }

    override fun equals(other: Any?): Boolean {
        if (other is CompletedDeliveryDto) {
            return id == other.id
        }
        return false
    }

}