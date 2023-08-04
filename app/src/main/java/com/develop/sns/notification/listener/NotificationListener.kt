package com.develop.sns.notification.listener

import com.develop.sns.notification.dto.Notification


interface NotificationListener {
    fun selectNotificationItem(itemDto: Notification.NotificationData, status: String)
}