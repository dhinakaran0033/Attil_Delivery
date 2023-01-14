package com.develop.sns.home

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.develop.sns.R
import com.develop.sns.SubModuleActivity
import com.develop.sns.databinding.ActivityHomeBinding
import com.develop.sns.deliveryTab.DeliveryTab
import com.develop.sns.deliverypending.DeliveryPending
import com.develop.sns.home.profile.fragment.ProfileFragment
import com.develop.sns.map.MapFragment
import com.develop.sns.notification.NotificationFragment
import com.develop.sns.utils.AppConstant


class HomeActivity : SubModuleActivity() {

    private val TAG = HomeActivity::class.java.simpleName
    private val context: HomeActivity = this@HomeActivity

    private val binding by lazy { ActivityHomeBinding.inflate(layoutInflater) }
    private var currentFragment = 0
    private val mapFragment = MapFragment()
    private val notificationFragment = NotificationFragment()
    private val profileFragment = ProfileFragment()
    private val delivery = DeliveryTab()

    var fa: Activity? = null
    var firstTime = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        fa = this

        selectItem(AppConstant.MAP_FRAGMENT)
        initClassReference()
        handleUiElement()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            mMessageReceiver,
            IntentFilter("custom-event-name")
        )
    }

    private fun initClassReference() {
        try {

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleUiElement() {
        try {
            binding.lnOffers.setOnClickListener(View.OnClickListener {
                if (currentFragment != AppConstant.MAP_FRAGMENT) selectItem(AppConstant.MAP_FRAGMENT)
            })

            binding.lnProducts.setOnClickListener(View.OnClickListener {
                if (currentFragment != AppConstant.DELIVERY_FRAGMENT) selectItem(AppConstant.DELIVERY_FRAGMENT)
            })

            binding.lnOrders.setOnClickListener(View.OnClickListener {
                if (currentFragment != AppConstant.NOTIFICATION_FRAGMENT) selectItem(AppConstant.NOTIFICATION_FRAGMENT)
            })

            binding.lnProfile.setOnClickListener(View.OnClickListener {
                if (currentFragment != AppConstant.PROFILE_FRAGMENT) selectItem(AppConstant.PROFILE_FRAGMENT)
            })

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun selectItem(fragment: Int) {
        try {
            currentFragment = fragment
            when (fragment) {
                AppConstant.MAP_FRAGMENT -> {
                    binding.ivOffers.setColorFilter(
                        ContextCompat.getColor(
                            context,
                            R.color.primary
                        ), PorterDuff.Mode.SRC_ATOP
                    )

                    binding.ivProducts.setColorFilter(
                        ContextCompat.getColor(context, R.color.grey),
                        PorterDuff.Mode.SRC_ATOP
                    )


                    binding.ivOrders.setColorFilter(
                        ContextCompat.getColor(context, R.color.grey),
                        PorterDuff.Mode.SRC_ATOP
                    )


                    binding.ivProfile.setColorFilter(
                        ContextCompat.getColor(context, R.color.grey),
                        PorterDuff.Mode.SRC_ATOP
                    )

                    launchMapFragment()
                }
                AppConstant.DELIVERY_FRAGMENT -> {

                    binding.ivOffers.setColorFilter(
                        ContextCompat.getColor(context, R.color.grey),
                        PorterDuff.Mode.SRC_ATOP
                    )

                    binding.ivProducts.setColorFilter(
                        ContextCompat.getColor(
                            context,
                            R.color.primary
                        ), PorterDuff.Mode.SRC_ATOP
                    )

                    binding.ivOrders.setColorFilter(
                        ContextCompat.getColor(context, R.color.grey),
                        PorterDuff.Mode.SRC_ATOP
                    )

                    binding.ivProfile.setColorFilter(
                        ContextCompat.getColor(context, R.color.grey),
                        PorterDuff.Mode.SRC_ATOP
                    )

                    launchDeliveryFragment()
                }
                AppConstant.NOTIFICATION_FRAGMENT -> {
                    binding.ivOffers.setColorFilter(
                        ContextCompat.getColor(context, R.color.grey),
                        PorterDuff.Mode.SRC_ATOP
                    )

                    binding.ivProducts.setColorFilter(
                        ContextCompat.getColor(context, R.color.grey),
                        PorterDuff.Mode.SRC_ATOP
                    )

                    binding.ivOrders.setColorFilter(
                        ContextCompat.getColor(
                            context,
                            R.color.primary
                        ), PorterDuff.Mode.SRC_ATOP
                    )

                    binding.ivProfile.setColorFilter(
                        ContextCompat.getColor(context, R.color.grey),
                        PorterDuff.Mode.SRC_ATOP
                    )

                    launchNotificationFragment()
                }
                AppConstant.PROFILE_FRAGMENT -> {
                    binding.ivOffers.setColorFilter(
                        ContextCompat.getColor(context, R.color.grey),
                        PorterDuff.Mode.SRC_ATOP
                    )

                    binding.ivProducts.setColorFilter(
                        ContextCompat.getColor(context, R.color.grey),
                        PorterDuff.Mode.SRC_ATOP
                    )

                    binding.ivOrders.setColorFilter(
                        ContextCompat.getColor(context, R.color.grey),
                        PorterDuff.Mode.SRC_ATOP
                    )

                    binding.ivProfile.setColorFilter(
                        ContextCompat.getColor(
                            context,
                            R.color.primary
                        ), PorterDuff.Mode.SRC_ATOP
                    )

                    launchProfileFragment()
                }
                else -> {
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun launchMapFragment() {
        try {
            val fragmentManager: FragmentManager = supportFragmentManager
            val transaction: FragmentTransaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.fl_fragment, mapFragment)
            transaction.commit()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun launchNotificationFragment() {
        try {
            firstTime = false
            val fragmentManager: FragmentManager = supportFragmentManager
            val transaction: FragmentTransaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.fl_fragment, notificationFragment)
            transaction.commit()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun launchDeliveryFragment() {
        try {
            firstTime = false
            val fragmentManager: FragmentManager = supportFragmentManager
            val transaction: FragmentTransaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.fl_fragment, delivery)
            transaction.commit()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }



    private fun launchProfileFragment() {
        try {
            val fragmentManager: FragmentManager = supportFragmentManager
            val transaction: FragmentTransaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.fl_fragment, profileFragment)
            transaction.commitAllowingStateLoss()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            // Get extra data included in the Intent
            val cartCount = intent.getIntExtra("cartCount", 0)
            //Log.d("receiver", "Got message: $cartCount")
            binding.ivOffers.badgeValue = cartCount
        }
    }
}