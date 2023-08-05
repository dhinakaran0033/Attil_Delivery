package com.develop.sns.deliverypending

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.develop.sns.R
import com.develop.sns.ViewOrderDetails.ViewOrderDetailsFragment
import com.develop.sns.databinding.FragmentDeliveryPendingBinding
import com.develop.sns.deliverypending.adapter.DeliveryPendingListAdapter
import com.develop.sns.deliverypending.dto.DeliveryPendingDto
import com.develop.sns.deliverypending.listener.PendingListener

import com.develop.sns.utils.AppConstant
import com.develop.sns.utils.AppUtils
import com.develop.sns.utils.CommonClass
import com.develop.sns.utils.PreferenceHelper
import com.google.gson.JsonObject
import org.json.JSONObject

class DeliveryPending: Fragment() , PendingListener {

    private val binding by lazy { FragmentDeliveryPendingBinding.inflate(layoutInflater) }
    private var preferenceHelper: PreferenceHelper? = null
    lateinit var accessToken: String
    lateinit var carrierId: String
    private lateinit var deliveryPendingList: ArrayList<DeliveryPendingDto>
    private lateinit var notificationListAdapter: DeliveryPendingListAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var OrderDetailsFragment: ViewOrderDetailsFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceHelper = PreferenceHelper(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initClassReference()


    }


    private fun initClassReference() {
        try {
            preferenceHelper = context?.let { PreferenceHelper(it) }
            accessToken = preferenceHelper!!.getValueFromSharedPrefs(AppConstant.KEY_TOKEN)!!
            carrierId = preferenceHelper!!.getValueFromSharedPrefs(AppConstant.KEY_CARRIER_ID)!!
            deliveryPendingList = ArrayList()
            getAccepted()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun launchOrderDetailsFragment(orderId: String) {
        try {
            OrderDetailsFragment = ViewOrderDetailsFragment()
            val bundle = Bundle()
            bundle.putString("orderId", orderId)
            OrderDetailsFragment.arguments = bundle
            val fragmentManager: FragmentManager = childFragmentManager
            fragmentManager?.let {OrderDetailsFragment.show(fragmentManager, "your tag")}
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    private fun getAccepted() {
        try {
            showProgressBar()
            if (AppUtils.isConnectedToInternet(requireActivity())) {
                val requestObject = JsonObject()
                requestObject.addProperty("carrierId", carrierId)
                requestObject.addProperty("skip", 0)
                Log.e("Normal request", requestObject.toString())
                val deliveryPendingViewModel = DeliveryPendingViewModel(context)
                deliveryPendingViewModel.getAccepted(
                    requestObject,
                    accessToken
                ).observe(viewLifecycleOwner, Observer<JSONObject?> { jsonObject ->
                    dismissProgressBar()
                    parseNormalOffersResponse(jsonObject)
                    Log.e("test11", jsonObject.toString())

                })
            } else {
                dismissProgressBar()
                CommonClass.showToastMessage(
                    requireActivity(),
                    binding.layCon,
                    resources.getString(R.string.no_internet),
                    Toast.LENGTH_SHORT
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun parseNormalOffersResponse(obj: JSONObject) {
        try {
            //Log.e("NormalOffers", obj.toString())
            if (obj.has("code") && obj.getInt("code") == 200) {
                binding.lvNotification.visibility = View.VISIBLE
                if (obj.has("status") && obj.getBoolean("status")) {
                    if (obj.has("data") && !obj.isNull("data")) {
                        val dataArray = obj.getJSONArray("data")
                        for (i in 0 until dataArray.length()) {
                            val itemObject = dataArray.getJSONObject(i)
                            val deliveryPendingDto = DeliveryPendingDto()

                            if (itemObject.has("_id") && !itemObject.isNull("_id")) {
                                deliveryPendingDto.id = itemObject.getString("_id")
                                Log.e("id", deliveryPendingDto.id)
                            }

                            if (itemObject.has("orderObjectId") && !itemObject.isNull("orderObjectId")) {
                                deliveryPendingDto.orderObjectId = itemObject.getString("orderObjectId")
                                Log.e("orderObjectId", deliveryPendingDto.orderObjectId)
                            }

                            if (itemObject.has("notificationStatus") && !itemObject.isNull("notificationStatus")) {
                                deliveryPendingDto.notificationStatus = itemObject.getString("notificationStatus")
                                Log.e("notificationStatus", deliveryPendingDto.notificationStatus)
                            }

                            if (itemObject.has("orderId") && !itemObject.isNull("orderId")) {
                                deliveryPendingDto.orderId = itemObject.getString("orderId")
                                Log.e("orderId", deliveryPendingDto.orderId)
                            }

                            if (itemObject.has("orderStatus") && !itemObject.isNull("orderStatus")) {
                                deliveryPendingDto.orderStatus = itemObject.getString("orderStatus")
                            }

                            if (itemObject.has("currentLocation") && !itemObject.isNull("currentLocation")) {
                                deliveryPendingDto.currentLocation = itemObject.getBoolean("currentLocation")
                                Log.e("currentLocation", deliveryPendingDto.currentLocation.toString())
                            }

                            if (itemObject.has("type") && !itemObject.isNull("type")) {
                                deliveryPendingDto.type = itemObject.getString("type")
                                Log.e("type", deliveryPendingDto.type)
                            }

                            if (itemObject.has("orderDateTime") && !itemObject.isNull("orderDateTime")) {
                                deliveryPendingDto.orderDateTime = itemObject.getString("orderDateTime")
                                Log.e("orderDateTime", deliveryPendingDto.orderDateTime)
                            }

                            if (itemObject.has("address") && !itemObject.isNull("address")) {
                                deliveryPendingDto.address = itemObject.getString("address")
                                Log.e("address", deliveryPendingDto.address)
                            }

                            if (itemObject.has("landmark") && !itemObject.isNull("landmark")) {
                                deliveryPendingDto.landmark = itemObject.getString("landmark")
                                Log.e("landmark", deliveryPendingDto.landmark)
                            }

                            if (itemObject.has("townORcity") && !itemObject.isNull("townORcity")) {
                                deliveryPendingDto.townORcity = itemObject.getString("townORcity")
                                Log.e("townORcity", deliveryPendingDto.townORcity)
                            }

                            if (itemObject.has("pinCode") && !itemObject.isNull("pinCode")) {
                                deliveryPendingDto.pinCode = itemObject.getString("pinCode")
                                Log.e("pinCode", deliveryPendingDto.pinCode)
                            }

                            if (itemObject.has("phoneNumber") && !itemObject.isNull("phoneNumber")) {
                                deliveryPendingDto.phoneNumber = itemObject.getString("phoneNumber")
                                Log.e("phoneNumber", deliveryPendingDto.phoneNumber)
                            }

                            //Delivery_location
                            if (itemObject.has("deliveryLocation") && !itemObject.isNull("deliveryLocation")) {
                                val itemObjectDelivery = itemObject.getJSONObject("deliveryLocation")
                                if (itemObjectDelivery.has("lat") && !itemObjectDelivery.isNull("lat")) {
                                    deliveryPendingDto.deliveryLat = itemObjectDelivery.getDouble("lat")
                                    Log.e("deliveryLat", deliveryPendingDto.deliveryLat.toString())
                                }
                                if (itemObjectDelivery.has("lng") && !itemObjectDelivery.isNull("lng")) {
                                    deliveryPendingDto.deliveryLng = itemObjectDelivery.getDouble("lng")
                                    Log.e("deliveryLng", deliveryPendingDto.deliveryLng.toString())
                                }
                            }

                            //Payment
                            if (itemObject.has("payment") && !itemObject.isNull("payment")) {
                                val itemObjectDelivery = itemObject.getJSONObject("payment")
                                if (itemObjectDelivery.has("amount") && !itemObjectDelivery.isNull("amount")) {
                                    deliveryPendingDto.amount = itemObjectDelivery.getDouble("amount")
                                    Log.e("amount", deliveryPendingDto.amount.toString())
                                }
                                if (itemObjectDelivery.has("paymentMode") && !itemObjectDelivery.isNull("paymentMode")) {
                                    deliveryPendingDto.paymentMode = itemObjectDelivery.getString("paymentMode")
                                    Log.e("paymentMode", deliveryPendingDto.paymentMode)
                                }
                                if (itemObjectDelivery.has("STATUS") && !itemObjectDelivery.isNull("STATUS")) {
                                    deliveryPendingDto.paymentstatus = itemObjectDelivery.getString("STATUS")
                                    Log.e("paymentstatus", deliveryPendingDto.paymentstatus)
                                }
                            }

                            //shopInfo
                            if (itemObject.has("shopInfo") && !itemObject.isNull("shopInfo")) {
                                val shopInfo = itemObject.getJSONObject("shopInfo")


                                if (shopInfo.has("shopLocation") && !shopInfo.isNull("shopLocation")) {
                                    val itemObjectShopLocation = shopInfo.getJSONObject("shopLocation")

                                    if (itemObjectShopLocation.has("lat") && !itemObjectShopLocation.isNull("lat")) {
                                        deliveryPendingDto.shopLat = itemObjectShopLocation.getDouble("lat")
                                        Log.e("shopLat", deliveryPendingDto.shopLat.toString())
                                    }
                                    if (itemObjectShopLocation.has("lng") && !itemObjectShopLocation.isNull("lng")) {
                                        deliveryPendingDto.shopLng = itemObjectShopLocation.getDouble("lng")
                                        Log.e("shopLng", deliveryPendingDto.shopLng.toString())
                                    }

                                }

                                if (shopInfo.has("_id") && !shopInfo.isNull("_id")) {
                                    deliveryPendingDto.shopID = shopInfo.getString("_id")
                                    Log.e("shopID", deliveryPendingDto.shopID.toString())
                                }

                                if (shopInfo.has("serialNumber") && !shopInfo.isNull("serialNumber")) {
                                    deliveryPendingDto.serialNumber = shopInfo.getString("serialNumber")
                                    Log.e("serialNumber", deliveryPendingDto.serialNumber)
                                }


                            }

                            deliveryPendingList.add(deliveryPendingDto)
                        }
                    }

                }
                populateNormalOfferList()
            } else {
                binding.lvNotification.visibility = View.GONE
                binding.tvNormalOfferNoData.visibility = View.VISIBLE
                CommonClass.handleErrorResponse(requireActivity(), obj, binding.layCon)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun populateNormalOfferList() {
        try {
            if (!deliveryPendingList.isNullOrEmpty()) {
                linearLayoutManager = LinearLayoutManager(requireActivity())
                binding.lvNotification.layoutManager = linearLayoutManager
                notificationListAdapter =
                    DeliveryPendingListAdapter(requireActivity(), deliveryPendingList, this@DeliveryPending)
                binding.lvNotification.adapter = notificationListAdapter
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun selectPendingItem(itemDto: DeliveryPendingDto, status: String) {
        if(status.equals("Pickedup")){
            val dialog = AppUtils.showDiolog(requireActivity(),"Do you Pick Up this order?")
            dialog.findViewById<Button>(R.id.btn_yes).setOnClickListener {
                pickUpOrder(itemDto,status)
                dialog.dismiss()
            }

        }else if(status.equals("Delivered")){
            val dialog = AppUtils.showDiolog(requireActivity(),"Are you Delivered this order?")
            dialog.findViewById<Button>(R.id.btn_yes).setOnClickListener {
                pickUpOrder(itemDto,status)
                dialog.dismiss()
            }
        }else if(status.equals("Return Accepted")){
            val dialog = AppUtils.showDiolog(requireActivity(),"Are you returned the order?")
            dialog.findViewById<Button>(R.id.btn_yes).setOnClickListener {
                pickUpOrder(itemDto,status)
                dialog.dismiss()
            }
        }else if(status.equals("Return Completed")){
            val dialog = AppUtils.showDiolog(requireActivity(),"Are you returned the order?")
            dialog.findViewById<Button>(R.id.btn_yes).setOnClickListener {
                pickUpOrder(itemDto,status)
                dialog.dismiss()
            }
        }else if(status.equals("View Order")){
            launchOrderDetailsFragment(itemDto.orderObjectId)
        }
    }

    private fun pickUpOrder(itemDto: DeliveryPendingDto,status: String) {
        try {
                showProgressBar()
                if (AppUtils.isConnectedToInternet(requireActivity())) {
                    val requestObject = JsonObject()
                    requestObject.addProperty("notificationId", itemDto.id)
                    requestObject.addProperty("orderObjectId", itemDto.orderObjectId)
                    requestObject.addProperty("carrierId", carrierId)
                    requestObject.addProperty("status", status)
                    requestObject.addProperty("type", "type!")
                    val deliveryPendingViewModel = DeliveryPendingViewModel(context)
                    deliveryPendingViewModel.setOrderStatus(requestObject,accessToken)
                        .observe(this, { jsonObject ->
                            //Log.e("jsonObject", jsonObject.toString() + "")
                            if (jsonObject != null) {
                                dismissProgressBar()
                               // parseSignInResponse(jsonObject)
                            }
                        })
                } else {
                    dismissProgressBar()
                    CommonClass.showToastMessage(
                        requireActivity(),
                        binding.layCon,
                        resources.getString(R.string.no_internet),
                        Toast.LENGTH_SHORT
                    )
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showProgressBar() {
        try {
            binding.lnProgressbar.loadingAnim.visibility = View.VISIBLE
            activity?.window?.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun dismissProgressBar() {
        try {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            binding.lnProgressbar.loadingAnim.visibility = View.GONE
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}