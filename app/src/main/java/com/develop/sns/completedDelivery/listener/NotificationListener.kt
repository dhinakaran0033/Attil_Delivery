package com.develop.sns.completedDelivery.listener

import com.develop.sns.completedDelivery.dto.CompletedDeliveryDto


interface NotificationListener {
    fun selectNotificationItem(itemDto: CompletedDeliveryDto, status: String)
}