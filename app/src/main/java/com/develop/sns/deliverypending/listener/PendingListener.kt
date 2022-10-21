package com.develop.sns.deliverypending.listener

import com.develop.sns.deliverypending.dto.DeliveryPendingDto


interface PendingListener {
    fun selectPendingItem(itemDto: DeliveryPendingDto, status: String)
}