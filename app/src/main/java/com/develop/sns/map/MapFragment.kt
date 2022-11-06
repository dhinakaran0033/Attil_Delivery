package com.develop.sns.map

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.develop.sns.R
import com.develop.sns.databinding.FragmentMapBinding
import com.develop.sns.deliverypending.dto.DeliveryPendingDto
import com.develop.sns.map.model.DirectionResponses
import com.develop.sns.notification.dto.NotificationDto
import com.develop.sns.utils.AppConstant
import com.develop.sns.utils.AppUtils
import com.develop.sns.utils.CommonClass
import com.develop.sns.utils.PreferenceHelper
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.maps.android.PolyUtil
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


class MapFragment: Fragment(), OnMapReadyCallback {

    private val binding by lazy { FragmentMapBinding.inflate(layoutInflater) }
    private var preferenceHelper: PreferenceHelper? = null
    lateinit var accessToken: String
    lateinit var carrierId: String
    private lateinit var deliveryPendingList: ArrayList<DeliveryPendingDto>
    private lateinit var mMap: GoogleMap
    private lateinit var fromLatlog: LatLng
    private lateinit var toLatlog: LatLng
    private lateinit var shape: String
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var directionList: DirectionResponses


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

        mapFragment = childFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment

    }

    private fun initClassReference() {
        try {
            preferenceHelper = context?.let { PreferenceHelper(it) }
            accessToken = preferenceHelper!!.getValueFromSharedPrefs(AppConstant.KEY_TOKEN)!!
            carrierId = preferenceHelper!!.getValueFromSharedPrefs(AppConstant.KEY_CARRIER_ID)!!
            deliveryPendingList = ArrayList()
            getAcceptedAll()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getAcceptedAll() {
        try {
            //binding.lnProgressbar.progressBar.visibility = View.VISIBLE
            if (AppUtils.isConnectedToInternet(requireActivity())) {
                val requestObject = JsonObject()
                requestObject.addProperty("carrierId", carrierId)
                requestObject.addProperty("skip", 0)
                val mapViewModel = MapViewModel()
                mapViewModel.getAcceptedAll(
                    requestObject,
                    accessToken
                ).observe(viewLifecycleOwner, Observer<JSONObject?> { jsonObject ->
                   parseNormalOffersResponse(jsonObject)
                    Log.e("Test_result",jsonObject.toString())
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


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val markerFkip = MarkerOptions()
            .position(fromLatlog)
            .title("Shop")
        val markerMonas = MarkerOptions()
            .position(toLatlog)
            .title("Delivery Location")

        mMap.addMarker(markerFkip)
        mMap.addMarker(markerMonas)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(toLatlog, 11.6f))

        shape = directionList.routes?.get(0)?.overviewPolyline?.points.toString()

        val steps = directionList.routes?.get(0)?.legs?.get(0)?.steps
        if (steps != null) {
            for (x in steps) {

                val lat: Double = x?.startLocation?.lat!!
                val lon: Double = x?.startLocation?.lng!!
                val markPoints = LatLng(lat,lon)

                val markerFkip = MarkerOptions()
                    .position(markPoints)
                    .title(x?.htmlInstructions)
                mMap.addMarker(markerFkip)
            }
        }

        drawPolyline()

    }
// not come api so we need to draw and send response to server
    fun drawMap(fromLatlog: String,toLatlog: String,mapFragment: MapFragment,orderObjectId: String) {
        val apiServices = RetrofitClient.apiServices(this)
        apiServices.getDirection(fromLatlog, toLatlog,"driving", getString(R.string.map_key))
            .enqueue(object : Callback<DirectionResponses> {
                override fun onResponse(call: Call<DirectionResponses>, response: Response<DirectionResponses>) {
                    Log.e("Test_res",response.toString())
                    directionList = response.body()!!
                    this@MapFragment.mapFragment.getMapAsync(mapFragment)

                    var gson = Gson()
                    var jsonString = gson.toJson(directionList)
                    Log.e("Test_33",jsonString)
                    accept_Order(orderObjectId,jsonString);

                }

                override fun onFailure(call: Call<DirectionResponses>, t: Throwable) {
                    Log.e("anjir error", t.localizedMessage)
                }
            })


    }

    private fun drawPolyline() {
        Log.e("Test", shape.toString())
        val polyline = PolylineOptions()
            .addAll(PolyUtil.decode(shape))
            .width(8f)
            .color(Color.RED)
        mMap.addPolyline(polyline)
    }


    private interface ApiServices {
        @GET("maps/api/directions/json")
        fun getDirection(@Query("origin") origin: String,
                         @Query("destination") destination: String,
                         @Query("mode") mode: String,
                         @Query("key") apiKey: String): Call<DirectionResponses>
    }

    private object RetrofitClient {
        fun apiServices(context: MapFragment): ApiServices {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(context.resources.getString(R.string.base_url))
                .build()

            return retrofit.create<ApiServices>(ApiServices::class.java)
        }
    }


    private fun parseNormalOffersResponse(obj: JSONObject) {
        try {
            //Log.e("NormalOffers", obj.toString())
            if (obj.has("code") && obj.getInt("code") == 200) {
                //binding.lvNotification.visibility = View.VISIBLE
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

                            // map data
                            if (itemObject.has("mapData") && !itemObject.isNull("mapData")) {
                                val shopInfo = itemObject.getJSONObject("mapData")
                                var gson = Gson()
                                directionList = gson.fromJson(shopInfo.toString() ,DirectionResponses::class.java)
                                fromLatlog = LatLng(deliveryPendingDto.shopLat, deliveryPendingDto.shopLng)
                                toLatlog = LatLng(deliveryPendingDto.deliveryLat, deliveryPendingDto.deliveryLng)

                                mapFragment.getMapAsync(this)

                            }else{
                                fromLatlog = LatLng(deliveryPendingDto.shopLat, deliveryPendingDto.shopLng)
                                toLatlog = LatLng(deliveryPendingDto.deliveryLat, deliveryPendingDto.deliveryLng)
                                val fromFKIP = deliveryPendingDto.shopLat.toString() +  "," + deliveryPendingDto.shopLng.toString()
                                val toMonas = deliveryPendingDto.deliveryLat.toString() + "," + deliveryPendingDto.deliveryLng.toString()

                                drawMap(fromFKIP,toMonas,this,deliveryPendingDto.orderObjectId)
                            }




                            deliveryPendingList.add(deliveryPendingDto)
                        }
                    }

                }

            } else {
                /*binding.lvNotification.visibility = View.GONE
                binding.tvNormalOfferNoData.visibility = View.VISIBLE*/
                CommonClass.handleErrorResponse(requireActivity(), obj, binding.layCon)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun accept_Order(orderObjectId: String, jsonString: String) {
        try {
            binding.lnProgressbar.progressBar.visibility= View.VISIBLE
            if (AppUtils.isConnectedToInternet(requireActivity())) {
                val requestObject = JsonObject()
                requestObject.addProperty("orderObjectId", orderObjectId)
                requestObject.addProperty("mapData", jsonString)
                val mapViewModel = MapViewModel()
                mapViewModel.sendMapData(requestObject,accessToken)
                    .observe(this, { jsonObject ->
                        //Log.e("jsonObject", jsonObject.toString() + "")
                        if (jsonObject != null) {
                            binding.lnProgressbar.progressBar.visibility= View.GONE
                            Log.e("Test","Test_innn")
                            Log.e("Test",jsonObject.toString())
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
