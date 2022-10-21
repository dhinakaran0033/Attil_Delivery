package com.develop.sns

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.develop.sns.databinding.ActivityMainBinding
import com.develop.sns.home.HomeActivity
import com.develop.sns.login.LoginActivity
import com.develop.sns.utils.AppConstant
import com.develop.sns.utils.PreferenceHelper

class MainActivity : AppCompatActivity() {
    private val context: MainActivity = this@MainActivity
    private var preferenceHelper: PreferenceHelper? = null
    private var token = ""

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initClassReference()

    }


    private fun launchLoginActivity() {
        try {
            val intent = Intent(context, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            resultLauncher.launch(intent)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun launchHomeActivity() {
        try {
            val intent = Intent(context, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                //checkForToken()
            } else {
                finish()
            }
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

}