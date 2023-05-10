package com.develop.sns.completedDelivery

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.develop.sns.R
import com.develop.sns.completedDelivery.adapter.CompletedDeliveryListAdapter
import com.develop.sns.completedDelivery.dto.CompletedDeliveryDto
import com.develop.sns.databinding.FragmentCompetedDeliveryBinding
import com.develop.sns.completedDelivery.listener.NotificationListener


import com.develop.sns.utils.AppConstant
import com.develop.sns.utils.AppUtils
import com.develop.sns.utils.CommonClass
import com.develop.sns.utils.PreferenceHelper
import com.google.gson.JsonObject
import org.json.JSONObject

class CompletedDelivery: Fragment() , NotificationListener {

    private val binding by lazy { FragmentCompetedDeliveryBinding.inflate(layoutInflater) }
    private var preferenceHelper: PreferenceHelper? = null
    lateinit var accessToken: String
    lateinit var carrierId: String
    private lateinit var deliveryPendingList: ArrayList<CompletedDeliveryDto>
    private lateinit var notificationListAdapter: CompletedDeliveryListAdapter
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
            deliveryPendingList = ArrayList()
            getAccepted()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun getAccepted() {
        try {
            if (AppUtils.isConnectedToInternet(requireActivity())) {
                val requestObject = JsonObject()
                requestObject.addProperty("carrierId", carrierId)
                requestObject.addProperty("skip", 0)
                Log.e("Normal request", requestObject.toString())
                val deliveryPendingViewModel = CompletedDeliveryViewModel(context)
                deliveryPendingViewModel.getAccepted(
                    requestObject,
                    accessToken
                ).observe(viewLifecycleOwner, Observer<JSONObject?> { jsonObject ->
                    //dismissProgressBar()
                    parseNormalOffersResponse(jsonObject)
                    Log.e("test11", jsonObject.toString())
                })
            } else {
                CommonClass.showToastMessage(
                    requireActivity(),
                    binding.lnNotification,
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
                            val completedDeliveryDto = CompletedDeliveryDto()

                            if (itemObject.has("_id") && !itemObject.isNull("_id")) {
                                completedDeliveryDto.id = itemObject.getString("_id")
                                Log.e("id", completedDeliveryDto.id)
                            }

                            if (itemObject.has("orderObjectId") && !itemObject.isNull("orderObjectId")) {
                                completedDeliveryDto.orderObjectId = itemObject.getString("orderObjectId")
                                Log.e("orderObjectId", completedDeliveryDto.orderObjectId)
                            }

                            if (itemObject.has("notificationStatus") && !itemObject.isNull("notificationStatus")) {
                                completedDeliveryDto.notificationStatus = itemObject.getString("notificationStatus")
                                Log.e("notificationStatus", completedDeliveryDto.notificationStatus)
                            }

                            if (itemObject.has("orderId") && !itemObject.isNull("orderId")) {
                                completedDeliveryDto.orderId = itemObject.getString("orderId")
                                Log.e("orderId", completedDeliveryDto.orderId)
                            }

                            if (itemObject.has("currentLocation") && !itemObject.isNull("currentLocation")) {
                                completedDeliveryDto.currentLocation = itemObject.getBoolean("currentLocation")
                                Log.e("currentLocation", completedDeliveryDto.currentLocation.toString())
                            }

                            if (itemObject.has("type") && !itemObject.isNull("type")) {
                                completedDeliveryDto.type = itemObject.getString("type")
                                Log.e("type", completedDeliveryDto.type)
                            }

                            if (itemObject.has("orderDateTime") && !itemObject.isNull("orderDateTime")) {
                                completedDeliveryDto.orderDateTime = itemObject.getString("orderDateTime")
                                Log.e("orderDateTime", completedDeliveryDto.orderDateTime)
                            }

                            if (itemObject.has("address") && !itemObject.isNull("address")) {
                                completedDeliveryDto.address = itemObject.getString("address")
                                Log.e("address", completedDeliveryDto.address)
                            }

                            if (itemObject.has("landmark") && !itemObject.isNull("landmark")) {
                                completedDeliveryDto.landmark = itemObject.getString("landmark")
                                Log.e("landmark", completedDeliveryDto.landmark)
                            }

                            if (itemObject.has("townORcity") && !itemObject.isNull("townORcity")) {
                                completedDeliveryDto.townORcity = itemObject.getString("townORcity")
                                Log.e("townORcity", completedDeliveryDto.townORcity)
                            }

                            if (itemObject.has("pinCode") && !itemObject.isNull("pinCode")) {
                                completedDeliveryDto.pinCode = itemObject.getString("pinCode")
                                Log.e("pinCode", completedDeliveryDto.pinCode)
                            }

                            if (itemObject.has("phoneNumber") && !itemObject.isNull("phoneNumber")) {
                                completedDeliveryDto.phoneNumber = itemObject.getString("phoneNumber")
                                Log.e("phoneNumber", completedDeliveryDto.phoneNumber)
                            }

                            //Delivery_location
                            if (itemObject.has("deliveryLocation") && !itemObject.isNull("deliveryLocation")) {
                                val itemObjectDelivery = itemObject.getJSONObject("deliveryLocation")
                                if (itemObjectDelivery.has("lat") && !itemObjectDelivery.isNull("lat")) {
                                    completedDeliveryDto.deliveryLat = itemObjectDelivery.getDouble("lat")
                                    Log.e("deliveryLat", completedDeliveryDto.deliveryLat.toString())
                                }
                                if (itemObjectDelivery.has("lng") && !itemObjectDelivery.isNull("lng")) {
                                    completedDeliveryDto.deliveryLng = itemObjectDelivery.getDouble("lng")
                                    Log.e("deliveryLng", completedDeliveryDto.deliveryLng.toString())
                                }
                            }

                            //Payment
                            if (itemObject.has("payment") && !itemObject.isNull("payment")) {
                                val itemObjectDelivery = itemObject.getJSONObject("payment")
                                if (itemObjectDelivery.has("amount") && !itemObjectDelivery.isNull("amount")) {
                                    completedDeliveryDto.amount = itemObjectDelivery.getDouble("amount")
                                    Log.e("amount", completedDeliveryDto.amount.toString())
                                }
                                if (itemObjectDelivery.has("paymentMode") && !itemObjectDelivery.isNull("paymentMode")) {
                                    completedDeliveryDto.paymentMode = itemObjectDelivery.getString("paymentMode")
                                    Log.e("paymentMode", completedDeliveryDto.paymentMode)
                                }
                                if (itemObjectDelivery.has("STATUS") && !itemObjectDelivery.isNull("STATUS")) {
                                    completedDeliveryDto.paymentstatus = itemObjectDelivery.getString("STATUS")
                                    Log.e("paymentstatus", completedDeliveryDto.paymentstatus)
                                }
                            }

                            //shopInfo
                            if (itemObject.has("shopInfo") && !itemObject.isNull("shopInfo")) {
                                val shopInfo = itemObject.getJSONObject("shopInfo")


                                if (shopInfo.has("shopLocation") && !shopInfo.isNull("shopLocation")) {
                                    val itemObjectShopLocation = shopInfo.getJSONObject("shopLocation")

                                    if (itemObjectShopLocation.has("lat") && !itemObjectShopLocation.isNull("lat")) {
                                        completedDeliveryDto.shopLat = itemObjectShopLocation.getDouble("lat")
                                        Log.e("shopLat", completedDeliveryDto.shopLat.toString())
                                    }
                                    if (itemObjectShopLocation.has("lng") && !itemObjectShopLocation.isNull("lng")) {
                                        completedDeliveryDto.shopLng = itemObjectShopLocation.getDouble("lng")
                                        Log.e("shopLng", completedDeliveryDto.shopLng.toString())
                                    }

                                }

                                if (shopInfo.has("_id") && !shopInfo.isNull("_id")) {
                                    completedDeliveryDto.shopID = shopInfo.getString("_id")
                                    Log.e("shopID", completedDeliveryDto.shopID.toString())
                                }

                                if (shopInfo.has("serialNumber") && !shopInfo.isNull("serialNumber")) {
                                    completedDeliveryDto.serialNumber = shopInfo.getString("serialNumber")
                                    Log.e("serialNumber", completedDeliveryDto.serialNumber)
                                }


                            }

                            deliveryPendingList.add(completedDeliveryDto)
                        }
                    }

                }
                populateNormalOfferList()
            } else {
                binding.lvNotification.visibility = View.GONE
                binding.tvNormalOfferNoData.visibility = View.VISIBLE
                CommonClass.handleErrorResponse(requireActivity(), obj, binding.lnNotification)
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
                    CompletedDeliveryListAdapter(requireActivity(), deliveryPendingList, this@CompletedDelivery)
                binding.lvNotification.adapter = notificationListAdapter
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun selectNotificationItem(itemDto: CompletedDeliveryDto,status: String) {
        logInService(itemDto,status)
    }

    private fun logInService(itemDto: CompletedDeliveryDto,status: String) {
        try {
                showProgressBar()
                if (AppUtils.isConnectedToInternet(requireActivity())) {
                    val requestObject = JsonObject()
                    requestObject.addProperty("notificationId", itemDto.id)
                    requestObject.addProperty("orderObjectId", itemDto.orderObjectId)
                    requestObject.addProperty("carrierId", carrierId)
                    requestObject.addProperty("status", status)
                    requestObject.addProperty("type", "type!")
                    val deliveryPendingViewModel = CompletedDeliveryViewModel(context)
                    deliveryPendingViewModel.setOrderStatus(requestObject,accessToken)
                        .observe(this, { jsonObject ->
                            //Log.e("jsonObject", jsonObject.toString() + "")
                            if (jsonObject != null) {
                                dismissProgressBar()
                               // parseSignInResponse(jsonObject)
                            }
                        })
                } else {
                    CommonClass.showToastMessage(
                        requireActivity(),
                        binding.lnNotification,
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