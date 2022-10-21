package com.develop.sns.completedDelivery.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.develop.sns.completedDelivery.dto.CompletedDeliveryDto
import com.develop.sns.databinding.CompletedDeliveryListAdapterBinding
import com.develop.sns.completedDelivery.listener.NotificationListener
import com.develop.sns.utils.PreferenceHelper
import org.json.JSONArray


class CompletedDeliveryListAdapter (
    val context: Context,
    val items: ArrayList<CompletedDeliveryDto>?,
    val notificationListener: NotificationListener,
) : RecyclerView.Adapter<CompletedDeliveryListAdapter.ViewHolder>() {

    var preferenceHelper = PreferenceHelper(context)
    var measureText = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CompletedDeliveryListAdapterBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items?.size!!

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(items?.get(position)!!, position)

    inner class ViewHolder(val binding: CompletedDeliveryListAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CompletedDeliveryDto, position: Int) {
            with(binding) {

                binding.tvOrderId.text = "Order no: "+item.orderId
                binding.tvAddress.text = item.address
            }
        }
    }
}