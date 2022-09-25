package com.example.speedtest_rework.ui.main.vpn

import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.example.speedtest_rework.R
import com.example.speedtest_rework.databinding.FragmentChangeLocationBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener

class FragmentChangeLocation : Fragment() {
    private lateinit var binding: FragmentChangeLocationBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChangeLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
        super.onViewCreated(view, savedInstanceState)
    }


    fun initView() {
        val tv = LayoutInflater.from(requireContext())
            .inflate(R.layout.custom_tabbar_item, null) as TextView
        val shader: Shader = LinearGradient(
            0f,
            0f,
            0f,
            tv.lineHeight.toFloat(),
            ContextCompat.getColor(requireContext(),R.color.gradient_text_premium_start),
            ContextCompat.getColor(requireContext(),R.color.gradient_text_premium_end),
            Shader.TileMode.REPEAT
        )
        tv.paint.shader = shader
        tv.setTextColor(resources.getColor(R.color.gradient_text_premium_start))
        tv.text = "Premium"
        binding.tabLayout.getTabAt(1)?.customView = tv
        binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        binding.viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position))
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }
        })
    }
}