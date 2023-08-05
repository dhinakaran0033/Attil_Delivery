package com.develop.sns.deliverypending.listener

import com.develop.sns.deliverypending.dto.DeliveryPending


interface PendingListener {
    fun selectPendingItem(itemDto: DeliveryPending.DeliveryPendingData, status: String)
}