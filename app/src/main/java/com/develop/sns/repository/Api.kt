package com.develop.sns.repository

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.develop.sns.BuildConfig
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import java.util.concurrent.TimeUnit


interface Api {

    @POST("topOffer/allProducts")
    fun topOffersCall(
        @Header("Authorization") authorization: String,
        @Body requestObject: JsonObject,
    ): Call<ResponseBody>

    @POST("deliveryNotifications/get/All")
    fun notifications(
        @Header("Authorization") authorization: String,
        @Body requestObject: JsonObject,
    ): Call<ResponseBody>

    @POST("deliveryNotifications/get/accepted/All")
    fun getAccepted(
        @Header("Authorization") authorization: String,
        @Body requestObject: JsonObject,
    ): Call<ResponseBody>

    @POST("deliveryNotifications/get/devileryOrderDetails")
    fun devileryOrderDetails(
        @Header("Authorization") authorization: String,
        @Body requestObject: JsonObject,
    ): Call<ResponseBody>

    @POST("deliveryNotifications/get/delivered/All")
    fun getDeliveredOrders(
        @Header("Authorization") authorization: String,
        @Body requestObject: JsonObject,
    ): Call<ResponseBody>


    @POST("deliveryNotifications/updateCarrierDeliveryStatus")
    fun setNotificationStatus(
        @Header("Authorization") authorization: String,
        @Body requestObject: JsonObject,
    ): Call<ResponseBody>

    @POST("order/create/orderMapdata")
    fun sendMapData(
        @Header("Authorization") authorization: String,
        @Body requestObject: JsonObject,
    ): Call<ResponseBody>

    @POST("customer/cartCount")
    fun cartCount(
        @Header("Authorization") authorization: String,
        @Body requestObject: JsonObject,
    ): Call<ResponseBody>

    @POST("customer/addtoCart")
    fun addToCart(
        @Header("Authorization") authorization: String,
        @Body requestObject: JsonObject,
    ): Call<ResponseBody>

    @POST("otp/verify")
    fun verifyOtp(
        @Header("Authorization") authorization: String,
        @Body requestObject: JsonObject,
    ): Call<ResponseBody>

    @POST("auth/otpLoginVerify")
    fun otpLoginVerify(
        @Header("Authorization") authorization: String,
        @Body requestObject: JsonObject,
    ): Call<ResponseBody>

    @POST("auth/otpLogin")
    fun sendOtpLogin(
        @Header("Authorization") authorization: String,
        @Body requestObject: JsonObject,
    ): Call<ResponseBody>

    @POST("carrier/login")
    fun loginService(
        @Header("Authorization") authorization: String,
        @Body requestObject: JsonObject,
    ): Call<ResponseBody>

    @POST("createAccount")
    fun createAccount(
        @Header("Authorization") authorization: String,
        @Body requestObject: JsonObject,
    ): Call<ResponseBody>

    @POST("otp/generate")
    fun generateOtp(
        @Header("Authorization") authorization: String,
        @Body requestObject: JsonObject,
    ): Call<ResponseBody>

    @POST("isUserNameExist")
    fun isUserNameExist(
        @Header("Authorization") authorization: String,
        @Body requestObject: JsonObject,
    ): Call<ResponseBody>

    @POST("getSystemConfig")
    fun getSystemConfig(): Call<ResponseBody>

    @POST("productCategory/list/all")
    fun getCategoryList(@Header("Authorization") authorization: String): Call<ResponseBody>

    @POST("product/search/list/all")
    fun getProductByCategory(
        @Header("Authorization") authorization: String,
        @Body requestObject: JsonObject,
    ): Call<ResponseBody>

    @POST("product/search/varities/all")
    fun getProductByVarities(
        @Header("Authorization") authorization: String,
        @Body requestObject: JsonObject,
    ): Call<ResponseBody>

    @POST("product/search/list/brands")
    fun getProductBrands(
        @Header("Authorization") authorization: String,
        @Body requestObject: JsonObject,
    ): Call<ResponseBody>

    @POST("customer/viewCart")
    fun getCartItems(
        @Header("Authorization") authorization: String,
        @Body requestObject: JsonObject,
    ): Call<ResponseBody>

    @POST("customer/removeCartItem")
    fun removeCartItem(
        @Header("Authorization") authorization: String,
        @Body requestObject: JsonObject,
    ): Call<ResponseBody>

    @POST("customer/userAddress/getAll")
    fun getSavedAddrList(
        @Header("Authorization") authorization: String,
        @Body requestObject: JsonObject,
    ): Call<ResponseBody>

    @POST("customer/userAddress/create")
    fun saveAddress(
        @Header("Authorization") authorization: String,
        @Body requestObject: JsonObject,
    ): Call<ResponseBody>

    @POST("shop-info/findShopByCoordinates")
    fun findShop(
        @Header("Authorization") authorization: String,
        @Body requestObject: JsonObject,
    ): Call<ResponseBody>


    companion object {

        fun initRetrofit(context: Context?): Api {
            val chuckerInterceptor = ChuckerInterceptor.Builder(context!!)
                .collector(ChuckerCollector(context!!))
                .build()
            val api: Api
            val logging = HttpLoggingInterceptor()
            //logging.setLevel(HttpLoggingInterceptor.Level.BASIC)
            //logging.setLevel(HttpLoggingInterceptor.Level.HEADERS)
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val httpClient = OkHttpClient.Builder()
                .connectTimeout(60000, TimeUnit.SECONDS)
                .readTimeout(60000, TimeUnit.SECONDS)
                .addInterceptor(chuckerInterceptor)
            httpClient.addInterceptor(logging)

            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build()
            api = retrofit.create(Api::class.java)
            return api
        }
    }
}