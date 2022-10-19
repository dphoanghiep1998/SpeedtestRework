package com.example.speedtest_rework.ui.data_usage

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.speedtest_rework.base.fragment.BaseFragment
import com.example.speedtest_rework.databinding.FragmentDataUsageBinding
import com.example.speedtest_rework.ui.data_usage.adapter.DataUsageAdapter
import com.example.speedtest_rework.ui.data_usage.adapter.Listenter
import com.example.speedtest_rework.viewmodel.SpeedTestViewModel
import java.math.RoundingMode

class FragmentDataUsage : BaseFragment() {
    private lateinit var binding: FragmentDataUsageBinding
    private lateinit var adapter: DataUsageAdapter
    private val viewModel: SpeedTestViewModel by activityViewModels()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDataUsageBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        observeListDataUsage()
        super.onViewCreated(view, savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun initView() {
        showLoading()
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rcvDataUsage.layoutManager = linearLayoutManager
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    private fun showLoading() {
        binding.containerLoading.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.containerLoading.visibility = View.GONE

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun observeListDataUsage() {
        viewModel.getListOfDataUsage().observe(viewLifecycleOwner) {
            if (it.size >= 0) {
                var totalMobile = 0.0
                var totalWifi = 0.0
                var totalAll = 0.0
                it.forEach { item ->
                    totalMobile += item.mobile_usage
                    totalWifi += item.wifi_usage
                    totalAll += totalMobile + totalWifi
                }
                adapter = DataUsageAdapter(it)
                binding.rcvDataUsage.adapter = adapter
                binding.tvTotalMobile.text = convertData(totalMobile)
                binding.tvTotalWifi.text = convertData(totalWifi)
                binding.totalAll.text = convertData(totalAll)
                hideLoading()
            }
        }
    }

    private fun convertData(value: Double): String {
        return when {
            value <= 0 -> "0 MB"
            value < 1024 -> "${round(value)} B"
            value < 1024 * 1024 -> "${round(value / 1024)} KB"
            value < 1024 * 1024 * 1024 -> "${round(value / (1024 * 1024))} MB"
            else -> "${round(value / (1024 * 1024 * 1024))} GB"
        }
    }

    private fun round(value: Double): Double {
        return value.toBigDecimal().setScale(1, RoundingMode.UP).toDouble()
    }

}