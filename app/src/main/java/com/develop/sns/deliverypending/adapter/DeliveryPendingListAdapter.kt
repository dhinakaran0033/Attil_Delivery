package com.develop.sns.deliverypending.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.develop.sns.R
import com.develop.sns.databinding.DeliveryPendingListAdapterBinding
import com.develop.sns.deliverypending.dto.DeliveryPendingDto
import com.develop.sns.deliverypending.listener.PendingListener
import com.develop.sns.utils.PreferenceHelper
import org.json.JSONArray


class DeliveryPendingListAdapter (
    val context: Context,
    val items: ArrayList<DeliveryPendingDto>?,
    val notificationListener: PendingListener,
) : RecyclerView.Adapter<DeliveryPendingListAdapter.ViewHolder>() {

    var preferenceHelper = PreferenceHelper(context)
    var measureText = ""
    var mrp = 0.0
    var offerMrp = 0.0
    var minUnit = 0.0
    var diff = 0.0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DeliveryPendingListAdapterBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items?.size!!

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(items?.get(position)!!, position)

    inner class ViewHolder(val binding: DeliveryPendingListAdapterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DeliveryPendingDto, position: Int) {
            with(binding) {

                binding.tvOrderId.text = "Order #"+item.orderId
                binding.tvAddress.text = item.address

                binding.btnPickupOrder.setOnClickListener {
                    val itemDto: DeliveryPendingDto = items!!.get(position)
                    if(item.orderStatus == "Packed"){
                        notificationListener.selectPendingItem(itemDto,"Pickedup")
                    }else if(item.orderStatus == "Pickedup"){
                        notificationListener.selectPendingItem(itemDto,"Delivered")
                    }else if(item.orderStatus == "Return Requested"){
                        notificationListener.selectPendingItem(itemDto,"Return Accepted")
                    }else if(item.orderStatus == "Return Accepted"){
                        notificationListener.selectPendingItem(itemDto,"Return Completed")
                    }

                }

                binding.btnViewOrder.setOnClickListener {
                    val itemDto: DeliveryPendingDto = items!!.get(position)
                    notificationListener.selectPendingItem(itemDto,"View Order")
                }

                if(item.orderStatus == "Accepted" || item.orderStatus == "Pending"){
                    binding.waitingPacked.text = context.getString(R.string.waiting_packed)
                    binding.btnPickupOrder.isClickable = false
                    binding.btnPickupOrder.text = context.getString(R.string.pick_up)
                    binding.btnPickupOrder.setBackgroundColor(context.getColor(R.color.white))
                    binding.btnPickupOrder.setTextColor(context.getColor(R.color.black))
                }else if (item.orderStatus == "Packing"){
                    binding.waitingPacked.text = context.getString(R.string.Packing)
                    binding.btnPickupOrder.isClickable = false
                    binding.btnPickupOrder.text = context.getString(R.string.pick_up)
                    binding.btnPickupOrder.setBackgroundColor(context.getColor(R.color.white))
                    binding.btnPickupOrder.setTextColor(context.getColor(R.color.black))
                }else if (item.orderStatus == "Packed"){
                    binding.waitingPacked.text = context.getString(R.string.Packed)
                    binding.btnPickupOrder.isClickable = true
                    binding.btnPickupOrder.text = context.getString(R.string.pick_up)
                    binding.btnPickupOrder.setBackgroundColor(context.getColor(R.color.purple_500))
                    binding.btnPickupOrder.setTextColor(context.getColor(R.color.white))
                }else if (item.orderStatus == "Pickedup"){
                    binding.waitingPacked.text = context.getString(R.string.on_the_way)
                    binding.btnPickupOrder.isClickable = true
                    binding.btnPickupOrder.text = context.getString(R.string.i_am_reached)
                    binding.btnPickupOrder.setBackgroundColor(context.getColor(R.color.purple_500))
                    binding.btnPickupOrder.setTextColor(context.getColor(R.color.white))
                }else if (item.orderStatus == "Return Requested"){
                    binding.waitingPacked.text = context.getString(R.string.return_requested)
                    binding.btnPickupOrder.isClickable = true
                    binding.btnPickupOrder.text = context.getString(R.string.pick_up)
                    binding.btnPickupOrder.setBackgroundColor(context.getColor(R.color.yellow))
                    binding.btnPickupOrder.setTextColor(context.getColor(R.color.dark_green))
                }else if (item.orderStatus == "Return Accepted"){
                    binding.waitingPacked.text = context.getString(R.string.return_accepted)
                    binding.btnPickupOrder.isClickable = true
                    binding.btnPickupOrder.text = context.getString(R.string.i_am_returned)
                    binding.btnPickupOrder.setBackgroundColor(context.getColor(R.color.yellow))
                    binding.btnPickupOrder.setTextColor(context.getColor(R.color.dark_green))
                }
            }
        }

    }

    private fun userexists(jsonArray: JSONArray, usernameToFind: String): Boolean {
        return jsonArray.toString().contains("\"$usernameToFind\"")
    }

}