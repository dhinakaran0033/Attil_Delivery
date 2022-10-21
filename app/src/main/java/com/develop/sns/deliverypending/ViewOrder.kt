package com.develop.sns.deliverypending

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.develop.sns.databinding.ActivityViewOrderBinding
import com.develop.sns.databinding.FragmentDeliveryPendingBinding
import com.develop.sns.utils.PreferenceHelper

class ViewOrder: Activity() {

    private var preferenceHelper: PreferenceHelper? = null
    private val binding by lazy { ActivityViewOrderBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

    }

}