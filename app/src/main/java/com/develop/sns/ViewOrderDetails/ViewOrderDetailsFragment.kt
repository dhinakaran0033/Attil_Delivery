package com.develop.sns.ViewOrderDetails

import android.app.Dialog
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
        binding.tvOrderId.text = data.orderId
        binding.tvDate.text = data.orderDateTime

    }

    private fun getOrderDetails() {
        try {
            binding.lnProgressbar.progressBar.visibility = View.VISIBLE
            if (AppUtils.isConnectedToInternet(requireActivity())) {
                val requestObject = JsonObject()
                requestObject.addProperty("orderObjectId", orderId)
                Log.e("Normal request", requestObject.toString())
                val viewOrderDetailsViewModel = ViewOrderDetailsViewModel()
                viewOrderDetailsViewModel.getAccepted(
                    requestObject,
                    accessToken
                ).observe(viewLifecycleOwner, Observer<JSONObject?> { jsonObject ->
                    binding.lnProgressbar.progressBar.visibility = View.GONE

                    val gson = Gson()
                    val testModel = gson.fromJson(jsonObject.toString(), OrderResponse::class.java)
                    val orderResponse: ArrayList<Data> = testModel.data
                    data = orderResponse[0]
                    setUpViews()

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

}