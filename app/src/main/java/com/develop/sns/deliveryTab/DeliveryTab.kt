package com.develop.sns.deliveryTab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.develop.sns.R
import com.develop.sns.databinding.FragmentDeliveryTabBinding
import com.develop.sns.utils.PreferenceHelper
import com.google.android.material.tabs.TabLayout


class DeliveryTab : Fragment() {


    private var preferenceHelper: PreferenceHelper? = null
    private var _binding: FragmentDeliveryTabBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceHelper = PreferenceHelper(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDeliveryTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTabLayout()
        setupViewPager()
    }

    private fun setupViewPager() {
        binding.viewPager.apply {
            adapter = activity?.let { ViewPagerAdapter(childFragmentManager, binding.tabLayout.tabCount) }
            addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout))
        }
    }

    private fun setupTabLayout() {
        binding.tabLayout.apply {
            addTab(this.newTab().setText(context.getString(R.string.delivery_pending)))
            addTab(this.newTab().setText(context.getString(R.string.completed_delivery)))

            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tab?.position?.let {
                        binding.viewPager.currentItem = it
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                }
            })
        }
    }
}