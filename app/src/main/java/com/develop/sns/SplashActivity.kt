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

class SplashActivity : SubModuleActivity() {
    private var notificationType = 0
    private var dataObject: String? = null
    private val binding by lazy { ActivitySplashBinding.inflate(layoutInflater) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        Handler(Looper.getMainLooper()).postDelayed({
            checkNewIntent()
        }, 500)
    }

    private fun checkNewIntent() {
        try {
            onNewIntent(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        try {
//            val extras: Bundle = intent.getExtras()!!
            launchNextActivity()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun launchNextActivity() {
        try {
            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private val TAG = SplashActivity::class.java.simpleName
    }
}