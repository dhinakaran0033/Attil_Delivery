package com.develop.sns.notification

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.develop.sns.R
import com.develop.sns.databinding.FragmentNotificationBinding
import com.develop.sns.notification.adapter.NotificationListAdapter
import com.develop.sns.notification.dto.Notification
import com.develop.sns.notification.listener.NotificationListener
import com.develop.sns.utils.AppConstant
import com.develop.sns.utils.AppUtils
import com.develop.sns.utils.CommonClass
import com.develop.sns.utils.PreferenceHelper
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONObject

class NotificationFragment: Fragment() , NotificationListener {

    private val binding by lazy { FragmentNotificationBinding.inflate(layoutInflater) }
    private var preferenceHelper: PreferenceHelper? = null
    lateinit var accessToken: String
    lateinit var carrierId: String
    private lateinit var notificationList: ArrayList<Notification.NotificationData>
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
            showProgressBar()
            if (AppUtils.isConnectedToInternet(requireActivity())) {
                val requestObject = JsonObject()
                requestObject.addProperty("carrierId", carrierId)
                requestObject.addProperty("skip", 0)
                Log.e("Normal request", requestObject.toString())
                val offersViewModel = NotificationViewModel(context)
                offersViewModel.getNotification(
                    requestObject,
                    accessToken
                ).observe(viewLifecycleOwner, Observer<JSONObject?> { jsonObject ->
                    Log.e("test33", jsonObject.toString())
                    var gson = Gson()
                    var testModel = gson.fromJson(jsonObject.toString(), Notification::class.java)

                    if( testModel.code == 200){
                        binding.lvNotification.visibility = View.VISIBLE
                        notificationList.addAll(testModel.data)
                        populateNormalOfferList()
                    }else{
                            binding.lvNotification.visibility = View.GONE
                            binding.tvNormalOfferNoData.visibility = View.VISIBLE
                            CommonClass.handleErrorResponse(requireActivity(), jsonObject, binding.layCon)
                    }

                    dismissProgressBar()
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
            dismissProgressBar()
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

    override fun selectNotificationItem(itemDto: Notification.NotificationData, status: String) {

        if(status == "Accepted"){
            val dialog = AppUtils.showDiolog(requireActivity(),"Do you Accept this order?")
            dialog.findViewById<Button>(R.id.btn_yes).setOnClickListener {
                accept_Order(itemDto,status)
                dialog.dismiss()
            }
        }else{
            val dialog = AppUtils.showDiolog(requireActivity(),"Do you Decline this order?")
            dialog.findViewById<Button>(R.id.btn_yes).setOnClickListener {
                accept_Order(itemDto,status)
                dialog.dismiss()
            }
        }


    }

    private fun accept_Order(itemDto: Notification.NotificationData, status: String) {
        try {
                showProgressBar()
                if (AppUtils.isConnectedToInternet(requireActivity())) {
                    val requestObject = JsonObject()
                    requestObject.addProperty("notificationId", itemDto.orderId)
                    requestObject.addProperty("orderObjectId", itemDto.orderObjectId)
                    requestObject.addProperty("carrierId", carrierId)
                    requestObject.addProperty("status", status)
                    requestObject.addProperty("type", "type!")
                    val notificationViewModel = NotificationViewModel(context)
                    notificationViewModel.setOrderStatus(requestObject,accessToken)
                        .observe(this) { jsonObject ->
                            if (jsonObject != null) {
                                dismissProgressBar()
                                notificationList.remove(itemDto)
                                notificationListAdapter.notifyDataSetChanged()
                            }
                        }
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
            dismissProgressBar()
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