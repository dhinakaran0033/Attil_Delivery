package com.develop.sns.notification.listener

import com.develop.sns.notification.dto.NotificationDto

interface NotificationListener {
    fun selectNotificationItem(itemDto: NotificationDto,status: String)
}