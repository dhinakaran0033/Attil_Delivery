package com.develop.sns.ViewOrderDetails

import android.app.Dialog
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import com.develop.sns.R
import com.develop.sns.ViewOrderDetails.Dto.Data
import com.develop.sns.ViewOrderDetails.Dto.OrderResponse
import com.develop.sns.databinding.FragmentViewOrderDetailsBinding
import com.develop.sns.utils.AppConstant
import com.develop.sns.utils.AppUtils
import com.develop.sns.utils.CommonClass
import com.develop.sns.utils.PreferenceHelper
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONObject
import java.io.IOException
import java.util.*


class ViewOrderDetailsFragment: DialogFragment()  {

    private val binding by lazy { FragmentViewOrderDetailsBinding.inflate(layoutInflater) }
    private var preferenceHelper: PreferenceHelper? = null
    lateinit var accessToken: String
    lateinit var carrierId: String
    lateinit var orderId:String
    lateinit var data:Data

    override fun onStart() {
        super.onStart()
        val dialog: Dialog? = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.getWindow()?.setLayout(width, height)
            setShowsDialog(true)
        }else{
            setShowsDialog(false)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceHelper = PreferenceHelper(requireActivity())
        setStyle(DialogFragment.STYLE_NORMAL,
            android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        setShowsDialog(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
          orderId = arguments?.getString("orderId").toString()
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
            getOrderDetails()

            binding.btnBack.setOnClickListener{
                getDialog()?.dismiss()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setUpViews() {
        Log.e("test", data.currentLocation.toString())
        if(data.currentLocation == true){
            binding.layCurrentLocation.visibility = View.VISIBLE
        }else{
            binding.layCurrentLocation.visibility = View.GONE
        }
        binding.tvOrderId.text = "Order #"+data.orderId
        binding.tvDate.text = data.orderDateTime
        binding.tvType.text = data.payment!!.paymentMode
        binding.tvAmount.text = "$ "+ data.payment!!.amount
        binding.tvStatus.text = data.payment!!.STATUS
        binding.tvMobile.text = "+91 "+data.userDetail!!.phoneNumber

        data.deliveryLocation?.let {
            var address1 = getCompleteAddressString(it.lat!!.toDouble(), it.lng!!)
            address1?.let { it1 ->
                binding.tvAddress.text = it1
            }
        }


    }


    private fun getCompleteAddressString(LATITUDE: Double, LONGITUDE: Double): String? {
        var strAdd = ""
        val geocoder = Geocoder(requireActivity(), Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1)
            if (addresses != null) {
                val returnedAddress = addresses[0]
                val strReturnedAddress = java.lang.StringBuilder("")
                for (i in 0..returnedAddress.maxAddressLineIndex) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n")
                }
                strAdd = strReturnedAddress.toString()
                Log.w("My Current loction address", strReturnedAddress.toString())
            } else {
                Log.w("My Current loction address", "No Address returned!")
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Log.w("My Current loction address", "Canont get Address!")
        }
        return strAdd
    }

    private fun getOrderDetails() {
        try {
            binding.lnProgressbar.loadingAnim.visibility = View.VISIBLE
            if (AppUtils.isConnectedToInternet(requireActivity())) {
                val requestObject = JsonObject()
                requestObject.addProperty("orderObjectId", orderId)
                Log.e("Normal request", requestObject.toString())
                val viewOrderDetailsViewModel = ViewOrderDetailsViewModel(context)
                viewOrderDetailsViewModel.getAccepted(
                    requestObject,
                    accessToken
                ).observe(viewLifecycleOwner, Observer<JSONObject?> { jsonObject ->
                    binding.lnProgressbar.loadingAnim.visibility = View.GONE

                    val gson = Gson()
                    val testModel = gson.fromJson(jsonObject.toString(), OrderResponse::class.java)
                    val orderResponse: ArrayList<Data> = testModel.data
                    data = orderResponse[0]
                    setUpViews()

                })
            } else {
                binding.lnProgressbar.loadingAnim.visibility = View.GONE
                CommonClass.showToastMessage(
                    requireActivity(),
                    binding.layCon,
                    resources.getString(R.string.no_internet),
                    Toast.LENGTH_SHORT
                )
            }
        } catch (e: Exception) {
            binding.lnProgressbar.loadingAnim.visibility = View.GONE
            e.printStackTrace()
        }
    }

}