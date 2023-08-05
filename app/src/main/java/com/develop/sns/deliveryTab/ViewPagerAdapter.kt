package com.develop.sns.deliveryTab

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.develop.sns.completedDelivery.CompletedDelivery
import com.develop.sns.deliverypending.DeliveryPendingFragment

class ViewPagerAdapter(fm: FragmentManager, var tabCount: Int) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> DeliveryPendingFragment()
            1 -> CompletedDelivery()
            else -> DeliveryPendingFragment()
        }
    }

    override fun getCount(): Int {
        return tabCount
    }

    override fun getPageTitle(position: Int): CharSequence {
        return "Tab " + (position + 1)
    }
}
