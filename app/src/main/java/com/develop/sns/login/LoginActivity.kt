package com.develop.sns.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.provider.Settings
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import com.develop.sns.BuildConfig
import com.develop.sns.MainActivityViewModel
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

        binding.btnSignIn.setOnClickListener {
            logInService()
        }

        binding.cbShowPassword.setOnCheckedChangeListener { buttonView, isChecked ->
            val start: Int
            val end: Int
            if (!isChecked) {
                start = binding.etPassword.selectionStart
                end = binding.etPassword.selectionEnd
                binding.etPassword.transformationMethod = PasswordTransformationMethod()
                binding.etPassword.setSelection(start, end)
            } else {
                start = binding.etPassword.selectionStart
                end = binding.etPassword.selectionEnd
                binding.etPassword.transformationMethod = null
                binding.etPassword.setSelection(start, end)
            }
        }

    }

    private fun initClassReference() {
        try {
            preferenceHelper = PreferenceHelper(context)
            languageId = preferenceHelper!!.getIntFromSharedPrefs(AppConstant.KEY_LANGUAGE_ID)
            binding.cbShowPassword.setButtonDrawable(R.drawable.password_show_drawable)
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
                binding.lnProgressbar.progressBar.visibility= View.VISIBLE
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
                                binding.lnProgressbar.progressBar.visibility= View.GONE
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
            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}