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
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.develop.sns.R
import com.develop.sns.ViewOrderDetails.ViewOrderDetailsFragment
import com.develop.sns.databinding.FragmentDeliveryPendingBinding
import com.develop.sns.deliverypending.adapter.DeliveryPendingListAdapter
import com.develop.sns.deliverypending.dto.DeliveryPending
import com.develop.sns.deliverypending.listener.PendingListener

import com.develop.sns.utils.AppConstant
import com.develop.sns.utils.AppUtils
import com.develop.sns.utils.CommonClass
import com.develop.sns.utils.PreferenceHelper
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONObject

class DeliveryPendingFragment: Fragment() , PendingListener {

    private val binding by lazy { FragmentDeliveryPendingBinding.inflate(layoutInflater) }
    private var preferenceHelper: PreferenceHelper? = null
    lateinit var accessToken: String
    lateinit var carrierId: String
    private lateinit var deliveryPendingList: ArrayList<DeliveryPending.DeliveryPendingData>
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
        binding.idSwipeToRefresh.setOnRefreshListener {
            binding.idSwipeToRefresh.isRefreshing = false
            getAccepted()
        }


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
                    var gson = Gson()
                    var testModel = gson.fromJson(jsonObject.toString(), DeliveryPending::class.java)

                    if( testModel.code == 200){
                        binding.lvNotification.visibility = View.VISIBLE
                        deliveryPendingList.clear()
                        deliveryPendingList.addAll(testModel.data)
                        populateNormalOfferList()
                    }else{
                        binding.lvNotification.visibility = View.GONE
                        binding.tvNormalOfferNoData.visibility = View.VISIBLE
                        CommonClass.handleErrorResponse(requireActivity(), jsonObject, binding.layCon)
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

    private fun populateNormalOfferList() {
        try {
            if (!deliveryPendingList.isNullOrEmpty()) {
                linearLayoutManager = LinearLayoutManager(requireActivity())
                binding.lvNotification.layoutManager = linearLayoutManager
                notificationListAdapter =
                    DeliveryPendingListAdapter(requireActivity(), deliveryPendingList, this@DeliveryPendingFragment)
                binding.lvNotification.adapter = notificationListAdapter
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun selectPendingItem(itemDto: DeliveryPending.DeliveryPendingData, status: String) {
        if(status == resources.getString(R.string.Pickedup)){
            val dialog = AppUtils.showDiolog(requireActivity(),"Do you Pick Up this order?")
            dialog.findViewById<Button>(R.id.btn_yes).setOnClickListener {
                pickUpOrder(itemDto,status)
                dialog.dismiss()
            }

        }else if(status == resources.getString(R.string.delivered)){
            val dialog = AppUtils.showDiolog(requireActivity(),"Are you Delivered this order?")
            dialog.findViewById<Button>(R.id.btn_yes).setOnClickListener {
                pickUpOrder(itemDto,status)
                dialog.dismiss()
            }
        }else if(status == resources.getString(R.string.return_accepted)){
            val dialog = AppUtils.showDiolog(requireActivity(),"Are you returned the order?")
            dialog.findViewById<Button>(R.id.btn_yes).setOnClickListener {
                pickUpOrder(itemDto,status)
                dialog.dismiss()
            }
        }else if(status == resources.getString(R.string.return_completed)){
            val dialog = AppUtils.showDiolog(requireActivity(),"Are you returned the order?")
            dialog.findViewById<Button>(R.id.btn_yes).setOnClickListener {
                pickUpOrder(itemDto,status)
                dialog.dismiss()
            }
        }else if(status == resources.getString(R.string.view_order)){
            launchOrderDetailsFragment(itemDto.orderObjectId)
        }
    }

    private fun pickUpOrder(itemDto: DeliveryPending.DeliveryPendingData, status: String) {
        try {
                showProgressBar()
                if (AppUtils.isConnectedToInternet(requireActivity())) {
                    val requestObject = JsonObject()
                    requestObject.addProperty("notificationId", itemDto._id)
                    requestObject.addProperty("orderObjectId", itemDto.orderObjectId)
                    requestObject.addProperty("carrierId", carrierId)
                    requestObject.addProperty("status", status)
                    requestObject.addProperty("type", "type!")
                    val deliveryPendingViewModel = DeliveryPendingViewModel(context)
                    deliveryPendingViewModel.setOrderStatus(requestObject,accessToken)
                        .observe(this, { jsonObject ->
                            if (jsonObject != null) {
                                dismissProgressBar()
                                getAccepted()
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