package com.example.speedtest_rework.ui.data_usage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.speedtest_rework.base.fragment.BaseFragment
import com.example.speedtest_rework.databinding.FragmentDataUsageBinding
import com.example.speedtest_rework.ui.data_usage.adapter.DataUsageAdapter

class FragmentDataUsage : BaseFragment() {
    private lateinit var binding: FragmentDataUsageBinding
    private lateinit var adapter: DataUsageAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDataUsageBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }

    private fun initView() {
//        adapter = DataUsageAdapter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}