package com.develop.sns.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.develop.sns.R
import com.develop.sns.databinding.ActivityHomeNewBinding
import com.develop.sns.deliveryTab.DeliveryTab
import com.develop.sns.home.profile.fragment.ProfileFragment
import com.develop.sns.map.MapFragment
import com.develop.sns.notification.NotificationFragment
import com.google.android.material.badge.BadgeDrawable


class HomeActivityNew : AppCompatActivity() {

    private lateinit var binding : ActivityHomeNewBinding
    private val mapFragment = MapFragment()
    private val notificationFragment = NotificationFragment()
    private val profileFragment = ProfileFragment()
    private val delivery = DeliveryTab()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeNewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        launchMapFragment()

        binding.bottomNavigationView.setOnItemSelectedListener {

            when(it.itemId){
                R.id.home -> launchMapFragment()
                R.id.profile -> launchProfileFragment()
                R.id.delivery -> launchDeliveryFragment()
                R.id.notity -> launchNotificationFragment()
                else ->{
                    launchMapFragment()
                }
            }
            true
        }


        val badge = binding.bottomNavigationView.getOrCreateBadge(R.id.notity);
        badge.isVisible = true;
        badge.number = 99;


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

}