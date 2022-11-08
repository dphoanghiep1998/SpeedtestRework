package com.example.speedtest_rework.ui.main.tools

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.speedtest_rework.base.fragment.BaseFragment
import com.example.speedtest_rework.databinding.FragmentToolsBinding

class FragmentTools : BaseFragment() {
    private lateinit var binding: FragmentToolsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentToolsBinding.inflate(layoutInflater)
        return binding.root
    }
}