package com.develop.sns.login

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.develop.sns.Location.LocationService
import com.develop.sns.R
import com.develop.sns.SubModuleActivity
import com.develop.sns.databinding.ActivityLoginBinding
import com.develop.sns.home.HomeActivity
import com.develop.sns.utils.AppConstant
import com.develop.sns.utils.AppUtils
import com.develop.sns.utils.CommonClass
import com.develop.sns.utils.PreferenceHelper
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.JsonObject
import org.json.JSONObject


class LoginActivity : SubModuleActivity() {
    private val context: Context = this@LoginActivity
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }

    override var preferenceHelper: PreferenceHelper? = null
    private var submitFlag = false
    var gcmId = ""
    var otp = ""
    lateinit var myAnim: Animation


    var fa: Activity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initClassReference()
        gcmId = getFireBaseToken()

        if (!checkPermission()) {
            requestPermission()
        }

        binding.btnSignIn.setOnClickListener {
            logInService()
        }
    }

    private fun initClassReference() {
        try {
            preferenceHelper = PreferenceHelper(context)
            languageId = preferenceHelper!!.getIntFromSharedPrefs(AppConstant.KEY_LANGUAGE_ID)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun getFireBaseToken(): String {
        var gcmId: String = ""
        try {
            if (!FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this)
                FirebaseDatabase.getInstance().setPersistenceEnabled(true)
            }
            gcmId = FirebaseInstanceId.getInstance().token.toString()
           // Log.i("fcmId", "" + gcmId)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return gcmId
    }

    private fun getAndroidDeviceId() : String{
        var deviceId: String = ""
        try {
             deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
            preferenceHelper!!.saveValueToSharedPrefs(AppConstant.KEY_DEVICE_ID, deviceId)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return deviceId
    }


    private fun logInService() {
        try {
            hideKeyboard()
            if (validate()) {
                showProgressBar()
                if (AppUtils.isConnectedToInternet(context)) {
                    val requestObject = JsonObject()
                    requestObject.addProperty(
                        "username",
                        binding.etLoginId.text.toString()
                    )
                    requestObject.addProperty("password", binding.etPassword.text.toString())
                    requestObject.addProperty("appVersion", getAppVersion())
                    requestObject.addProperty("os", getOsVersion())
                    requestObject.addProperty("deviceToken", getFireBaseToken())
                    requestObject.addProperty("deviceModel", getPhoneModel())
                    requestObject.addProperty("udid", getAndroidDeviceId())
                    Log.e("requestObj", requestObject.toString())
                    //showProgressBar()
                    val loginViewModel = LoginViewModel()
                    loginViewModel.makeLogin(requestObject)
                        .observe(this, { jsonObject ->
                            //Log.e("jsonObject", jsonObject.toString() + "")
                            if (jsonObject != null) {
                                dismissProgressBar()
                                Log.e("test11",jsonObject.toString())
                                parseSignInResponse(jsonObject)
                            }
                        })
                } else {
                    CommonClass.showToastMessage(
                        context,
                        binding.rlLoginMainLayout,
                        resources.getString(R.string.no_internet),
                        Toast.LENGTH_SHORT
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun validate(): Boolean {
        var flag = true
        try {
            if (binding.etLoginId.text.toString().isEmpty()) {
                binding.etLoginId.requestFocus()
                binding.etLoginId.error = resources.getString(R.string.required)
                flag = false
            } else if (binding.etPassword.text.toString().isEmpty()) {
                binding.etPassword.requestFocus()
                binding.etPassword.error = resources.getString(R.string.required)
                flag = false
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        submitFlag = flag
        return flag
    }

    private fun parseSignInResponse(obj: JSONObject) {
        try {
            submitFlag = false
            if (obj.has("data") && !obj.isNull("data")) {
                preferenceHelper!!.saveValueToSharedPrefs(
                    AppConstant.KEY_USER_NAME,
                    binding.etLoginId.text.toString().trim()
                )
                if (binding.etPassword.text.toString().trim().isNotEmpty()) {
                    preferenceHelper!!.saveValueToSharedPrefs(
                        AppConstant.KEY_USER_PWD,
                        binding.etPassword.text.toString().trim()
                    )
                }
                val dataObject = obj.getJSONObject("data")
                if (dataObject.has("access_token") && !dataObject.isNull("access_token")) {
                    preferenceHelper!!.saveValueToSharedPrefs(
                        AppConstant.KEY_TOKEN,
                        dataObject.getString("access_token")
                    )
                }

                if (dataObject.has("userId") && !dataObject.isNull("userId")) {
                    preferenceHelper!!.saveValueToSharedPrefs(
                        AppConstant.KEY_USER_ID,
                        dataObject.getString("userId")
                    )
                }

                if (dataObject.has("carrierId") && !dataObject.isNull("carrierId")) {
                    preferenceHelper!!.saveValueToSharedPrefs(
                        AppConstant.KEY_CARRIER_ID,
                        dataObject.getString("carrierId")
                    )
                }



                if (dataObject.has("username") && !dataObject.isNull("username")) {
                    preferenceHelper!!.saveValueToSharedPrefs(
                        AppConstant.KEY_NAME,
                        dataObject.getString("username")
                    )
                }

                launchHomeActivity()

            } else {
                CommonClass.showToastMessage(
                    context,
                    binding.rlLoginMainLayout,
                    obj.getString("message"),
                    Toast.LENGTH_SHORT
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun launchHomeActivity() {
        try {
            // start location service
            ContextCompat.startForegroundService(this, Intent(this, LocationService::class.java))

            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION)
        val result1 = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION)
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
    }

     fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1)
    }

    override fun showProgressBar() {
        try {
            binding.lnProgressbar.progressBar.visibility = View.VISIBLE
            window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun dismissProgressBar() {
        try {
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            binding.lnProgressbar.progressBar.visibility = View.GONE
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}