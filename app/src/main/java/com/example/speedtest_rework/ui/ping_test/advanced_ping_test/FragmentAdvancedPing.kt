package com.example.speedtest_rework.ui.ping_test.advanced_ping_test

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.speedtest_rework.base.fragment.BaseFragment
import com.example.speedtest_rework.common.utils.Constant
import com.example.speedtest_rework.databinding.FragmentAdvancedPingBinding
import com.example.speedtest_rework.ui.ping_test.model.ContentPingTest


class FragmentAdvancedPing : BaseFragment() {
    private lateinit var binding: FragmentAdvancedPingBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdvancedPingBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getDataFromBundle()
        initView()

    }

    private fun initView() {
        initButton()
        initChart()
    }

    private fun initButton() {
        binding.btnBack.setOnClickListener{
            findNavController().popBackStack()
        }
    }

    private fun initChart(){
        val barChart = BarChart()
        barChart.initGraph(binding.graphView)
    }

    private fun getDataFromBundle() {
        val bundle: Bundle? = this.arguments
        if (bundle != null) {
            val item: ContentPingTest = bundle.getParcelable(Constant.KEY_ITEM_PING)!!
            binding.title.text = item.title
        }
    }
}