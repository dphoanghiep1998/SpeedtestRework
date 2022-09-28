package com.example.speedtest_rework.ui.main.analyzer

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.speedtest_rework.base.fragment.BaseFragment
import com.example.speedtest_rework.databinding.FragmentAnalyzerBinding
import com.example.speedtest_rework.viewmodel.SpeedTestViewModel


class FragmentAnalyzer : BaseFragment() {
    private lateinit var binding: FragmentAnalyzerBinding
    private val viewModel:SpeedTestViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAnalyzerBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun observeWifiEnabled() {
        viewModel._isWifiEnabled.observe(viewLifecycleOwner, Observer {
            Log.d("observeWifiEnabled", "observeWifiEnabled: " + it)
        })
    }


}
