package com.develop.sns.utils

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast


object AppUtils {
    fun isTablet(context: Context): Boolean {
        return (context.resources.configuration.screenLayout
                and Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE
    }

    fun isConnectedToInternet(scontext: Context): Boolean {
        val connectivityManager =
            scontext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                //for other device how are able to connect with Ethernet
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                //for check internet over Bluetooth
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        } else {
            return connectivityManager.activeNetworkInfo?.isConnected ?: false
        }
    }

    fun showDiolog(context: Context, s: String):Dialog{
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(com.develop.sns.R.layout.custom_dialog)
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        val body = dialog.findViewById(com.develop.sns.R.id.alert_message) as TextView
        body.text = s
        val noBtn = dialog.findViewById(com.develop.sns.R.id.btn_no) as Button
        /*val yesBtn = dialog.findViewById(com.develop.sns.R.id.btn_yes) as Button
        yesBtn.setOnClickListener {
            boolean = true
            dialog.dismiss()
        }*/
        noBtn.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
        return dialog
    }

}