package com.develop.sns.notification

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.develop.sns.R
import com.develop.sns.databinding.FragmentNotificationBinding
import com.develop.sns.notification.adapter.NotificationListAdapter
import com.develop.sns.notification.dto.NotificationDto
import com.develop.sns.notification.listener.NotificationListener
import com.develop.sns.utils.AppConstant
import com.develop.sns.utils.AppUtils
import com.develop.sns.utils.CommonClass
import com.develop.sns.utils.PreferenceHelper
import com.google.gson.JsonObject
import org.json.JSONObject

class NotificationFragment: Fragment() , NotificationListener {

    private val binding by lazy { FragmentNotificationBinding.inflate(layoutInflater) }
    private var preferenceHelper: PreferenceHelper? = null
    lateinit var accessToken: String
    lateinit var carrierId: String
    private lateinit var notificationList: ArrayList<NotificationDto>
    private lateinit var notificationListAdapter: NotificationListAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager

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
            notificationList = ArrayList()
            getNotifications()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun getNotifications() {
        try {
            binding.lnProgressbar.progressBar.visibility = View.VISIBLE
            if (AppUtils.isConnectedToInternet(requireActivity())) {
                val requestObject = JsonObject()
                requestObject.addProperty("carrierId", carrierId)
                requestObject.addProperty("skip", 0)
                Log.e("Normal request", requestObject.toString())
                val offersViewModel = NotificationViewModel()
                offersViewModel.getNotification(
                    requestObject,
                    accessToken
                ).observe(viewLifecycleOwner, Observer<JSONObject?> { jsonObject ->
                    Log.e("test11", jsonObject.toString())
                    parseNormalOffersResponse(jsonObject)
                    binding.lnProgressbar.progressBar.visibility = View.GONE
                })
            } else {
                binding.lnProgressbar.progressBar.visibility = View.GONE
                CommonClass.showToastMessage(
                    requireActivity(),
                    binding.layCon,
                    resources.getString(R.string.no_internet),
                    Toast.LENGTH_SHORT
                )
            }
        } catch (e: Exception) {
            binding.lnProgressbar.progressBar.visibility = View.GONE
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
                            val normalOfferDto = NotificationDto()

                            if (itemObject.has("_id") && !itemObject.isNull("_id")) {
                                normalOfferDto.id = itemObject.getString("_id")
                                Log.e("id", normalOfferDto.id)
                            }

                            if (itemObject.has("orderObjectId") && !itemObject.isNull("orderObjectId")) {
                                normalOfferDto.orderObjectId = itemObject.getString("orderObjectId")
                                Log.e("orderObjectId", normalOfferDto.orderObjectId)
                            }

                            if (itemObject.has("notificationStatus") && !itemObject.isNull("notificationStatus")) {
                                normalOfferDto.notificationStatus = itemObject.getString("notificationStatus")
                                Log.e("notificationStatus", normalOfferDto.notificationStatus)
                            }

                            if (itemObject.has("orderId") && !itemObject.isNull("orderId")) {
                                normalOfferDto.orderId = itemObject.getString("orderId")
                                Log.e("orderId", normalOfferDto.orderId)
                            }

                            if (itemObject.has("currentLocation") && !itemObject.isNull("currentLocation")) {
                                normalOfferDto.currentLocation = itemObject.getBoolean("currentLocation")
                                Log.e("currentLocation", normalOfferDto.currentLocation.toString())
                            }

                            if (itemObject.has("type") && !itemObject.isNull("type")) {
                                normalOfferDto.type = itemObject.getString("type")
                                Log.e("type", normalOfferDto.type)
                            }

                            if (itemObject.has("orderDateTime") && !itemObject.isNull("orderDateTime")) {
                                normalOfferDto.orderDateTime = itemObject.getString("orderDateTime")
                                Log.e("orderDateTime", normalOfferDto.orderDateTime)
                            }

                            if (itemObject.has("address") && !itemObject.isNull("address")) {
                                normalOfferDto.address = itemObject.getString("address")
                                Log.e("address", normalOfferDto.address)
                            }

                            if (itemObject.has("landmark") && !itemObject.isNull("landmark")) {
                                normalOfferDto.landmark = itemObject.getString("landmark")
                                Log.e("landmark", normalOfferDto.landmark)
                            }

                            if (itemObject.has("townORcity") && !itemObject.isNull("townORcity")) {
                                normalOfferDto.townORcity = itemObject.getString("townORcity")
                                Log.e("townORcity", normalOfferDto.townORcity)
                            }

                            if (itemObject.has("pinCode") && !itemObject.isNull("pinCode")) {
                                normalOfferDto.pinCode = itemObject.getString("pinCode")
                                Log.e("pinCode", normalOfferDto.pinCode)
                            }

                            if (itemObject.has("phoneNumber") && !itemObject.isNull("phoneNumber")) {
                                normalOfferDto.phoneNumber = itemObject.getString("phoneNumber")
                                Log.e("phoneNumber", normalOfferDto.phoneNumber)
                            }

                            //Delivery_location
                            if (itemObject.has("deliveryLocation") && !itemObject.isNull("deliveryLocation")) {
                                val itemObjectDelivery = itemObject.getJSONObject("deliveryLocation")
                                if (itemObjectDelivery.has("lat") && !itemObjectDelivery.isNull("lat")) {
                                    normalOfferDto.deliveryLat = itemObjectDelivery.getDouble("lat")
                                    Log.e("deliveryLat", normalOfferDto.deliveryLat.toString())
                                }
                                if (itemObjectDelivery.has("lng") && !itemObjectDelivery.isNull("lng")) {
                                    normalOfferDto.deliveryLng = itemObjectDelivery.getDouble("lng")
                                    Log.e("deliveryLng", normalOfferDto.deliveryLng.toString())
                                }
                            }

                            //Payment
                            if (itemObject.has("payment") && !itemObject.isNull("payment")) {
                                val itemObjectDelivery = itemObject.getJSONObject("payment")
                                if (itemObjectDelivery.has("amount") && !itemObjectDelivery.isNull("amount")) {
                                    normalOfferDto.amount = itemObjectDelivery.getDouble("amount")
                                    Log.e("amount", normalOfferDto.amount.toString())
                                }
                                if (itemObjectDelivery.has("paymentMode") && !itemObjectDelivery.isNull("paymentMode")) {
                                    normalOfferDto.paymentMode = itemObjectDelivery.getString("paymentMode")
                                    Log.e("paymentMode", normalOfferDto.paymentMode)
                                }
                                if (itemObjectDelivery.has("STATUS") && !itemObjectDelivery.isNull("STATUS")) {
                                    normalOfferDto.paymentstatus = itemObjectDelivery.getString("STATUS")
                                    Log.e("paymentstatus", normalOfferDto.paymentstatus)
                                }
                            }

                            //shopInfo
                            if (itemObject.has("shopInfo") && !itemObject.isNull("shopInfo")) {
                                val shopInfo = itemObject.getJSONObject("shopInfo")


                                if (shopInfo.has("shopLocation") && !shopInfo.isNull("shopLocation")) {
                                    val itemObjectShopLocation = shopInfo.getJSONObject("shopLocation")

                                    if (itemObjectShopLocation.has("lat") && !itemObjectShopLocation.isNull("lat")) {
                                        normalOfferDto.shopLat = itemObjectShopLocation.getDouble("lat")
                                        Log.e("shopLat", normalOfferDto.shopLat.toString())
                                    }
                                    if (itemObjectShopLocation.has("lng") && !itemObjectShopLocation.isNull("lng")) {
                                        normalOfferDto.shopLng = itemObjectShopLocation.getDouble("lng")
                                        Log.e("shopLng", normalOfferDto.shopLng.toString())
                                    }

                                }

                                if (shopInfo.has("_id") && !shopInfo.isNull("_id")) {
                                    normalOfferDto.shopID = shopInfo.getString("_id")
                                    Log.e("shopID", normalOfferDto.shopID.toString())
                                }

                                if (shopInfo.has("serialNumber") && !shopInfo.isNull("serialNumber")) {
                                    normalOfferDto.serialNumber = shopInfo.getString("serialNumber")
                                    Log.e("serialNumber", normalOfferDto.serialNumber)
                                }


                            }

                            notificationList.add(normalOfferDto)
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
            if (!notificationList.isNullOrEmpty()) {
                linearLayoutManager = LinearLayoutManager(requireActivity())
                binding.lvNotification.layoutManager = linearLayoutManager
                notificationListAdapter =
                    NotificationListAdapter(requireActivity(), notificationList, this@NotificationFragment)
                binding.lvNotification.adapter = notificationListAdapter
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun selectNotificationItem(itemDto: NotificationDto,status: String) {
        accept_Order(itemDto,status)
    }

    private fun accept_Order(itemDto: NotificationDto,status: String) {
        try {
                binding.lnProgressbar.progressBar.visibility= View.VISIBLE
                if (AppUtils.isConnectedToInternet(requireActivity())) {
                    val requestObject = JsonObject()
                    requestObject.addProperty("notificationId", itemDto.id)
                    requestObject.addProperty("orderObjectId", itemDto.orderObjectId)
                    requestObject.addProperty("carrierId", carrierId)
                    requestObject.addProperty("status", status)
                    requestObject.addProperty("type", "type!")
                    val notificationViewModel = NotificationViewModel()
                    notificationViewModel.setOrderStatus(requestObject,accessToken)
                        .observe(this, { jsonObject ->
                            //Log.e("jsonObject", jsonObject.toString() + "")
                            if (jsonObject != null) {
                                binding.lnProgressbar.progressBar.visibility= View.GONE
                                notificationList.remove(itemDto)
                                notificationListAdapter.notifyDataSetChanged()
                            }
                        })
                } else {
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

}