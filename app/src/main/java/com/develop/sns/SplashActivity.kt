package com.develop.sns

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.develop.sns.databinding.ActivitySplashBinding
import com.develop.sns.home.HomeActivityNew
import com.develop.sns.login.LoginActivity
import com.develop.sns.utils.AppConstant
import com.develop.sns.utils.PreferenceHelper

class SplashActivity : SubModuleActivity() {
    private val binding by lazy { ActivitySplashBinding.inflate(layoutInflater) }
    private val context: SplashActivity = this@SplashActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        Handler(Looper.getMainLooper()).postDelayed({
            initClassReference()
        }, 500)
    }

    private fun initClassReference() {
        try {
            preferenceHelper = PreferenceHelper(context)
            val token: String = preferenceHelper!!.getValueFromSharedPrefs(AppConstant.KEY_TOKEN)!!
            if (token.isNotEmpty()) {
                launchHomeActivity()
            }else{
                launchLoginActivity()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun launchLoginActivity() {
        try {
            val intent = Intent(context, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
            finish()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun launchHomeActivity() {
        try {
            val intent = Intent(context, HomeActivityNew::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private val TAG = SplashActivity::class.java.simpleName
    }
}