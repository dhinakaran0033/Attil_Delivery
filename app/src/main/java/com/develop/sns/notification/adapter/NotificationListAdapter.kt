package com.develop.sns.notification.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.develop.sns.databinding.NotificationListAdapterBinding
import com.develop.sns.notification.dto.Notification
import com.develop.sns.notification.listener.NotificationListener
import com.develop.sns.utils.PreferenceHelper
import org.json.JSONArray


class NotificationListAdapter (
    val context: Context,
    val items: ArrayList<Notification.NotificationData>?,
    val notificationListener: NotificationListener,
) : RecyclerView.Adapter<NotificationListAdapter.ViewHolder>() {

    var preferenceHelper = PreferenceHelper(context)
    var measureText = ""
    var mrp = 0.0
    var offerMrp = 0.0
    var minUnit = 0.0
    var diff = 0.0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = NotificationListAdapterBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items?.size!!

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(items?.get(position)!!, position)

    inner class ViewHolder(val binding: NotificationListAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Notification.NotificationData, position: Int) {
            with(binding) {

                binding.tvOrderId.text = "Order #"+item.orderId
                binding.tvAddress.text = item.address
                binding.tvAmount.text = " ₹ "+item.payment.amount.toString()
                binding.tvPaymentMode.text = item.payment.paymentMode

                binding.btnAcceptOrder.setOnClickListener {
                    val itemDto: Notification.NotificationData = items!!.get(position)
                    notificationListener.selectNotificationItem(itemDto,"Accepted")
                }

                binding.tvDecline.setOnClickListener {
                    val itemDto: Notification.NotificationData = items!!.get(position)
                    notificationListener.selectNotificationItem(itemDto,"Decline")
                }

            }
        }

    }

    private fun userexists(jsonArray: JSONArray, usernameToFind: String): Boolean {
        return jsonArray.toString().contains("\"$usernameToFind\"")
    }

}