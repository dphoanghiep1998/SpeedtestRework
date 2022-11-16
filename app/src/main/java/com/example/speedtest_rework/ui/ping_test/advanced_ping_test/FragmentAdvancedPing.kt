package com.example.speedtest_rework.ui.ping_test.advanced_ping_test

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.speedtest_rework.R
import com.example.speedtest_rework.base.fragment.BaseFragment
import com.example.speedtest_rework.common.utils.Constant
import com.example.speedtest_rework.databinding.FragmentAdvancedPingBinding
import com.example.speedtest_rework.ui.ping_test.model.ContentPingTest
import com.example.speedtest_rework.viewmodel.SpeedTestViewModel
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry


class FragmentAdvancedPing : BaseFragment() {
    private lateinit var binding: FragmentAdvancedPingBinding
    private val viewMoDel: SpeedTestViewModel by activityViewModels()
    private var itemContentPingTest: ContentPingTest? = null
    private var barChart = ArrayList<BarEntry>()
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
        initBarArrayData()
        initView()
        observeDataPing()

    }

    private fun initBarArrayData() {
        barChart = ArrayList()
        for (i in 0..9) {
            barChart.add(BarEntry(i.toFloat(), 0f))
        }
    }

    private fun initView() {
        initButton()
        initChart()
    }

    private fun initButton() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.containerProgress.setOnClickListener {
            initBarArrayData()
            val barDataSet = BarDataSet(barChart, null)
            val data = BarData(barDataSet)
            binding.graphView.data = data
            binding.graphView.invalidate()
            viewMoDel.getPingResultAdvanced(itemContentPingTest!!.url)
        }
    }

    private fun initChart() {

        binding.graphView.setTouchEnabled(true)
        binding.graphView.isClickable = false
        binding.graphView.isDoubleTapToZoomEnabled = false
        binding.graphView.isDoubleTapToZoomEnabled = false

        binding.graphView.setDrawBorders(false)
        binding.graphView.setDrawGridBackground(false)

        binding.graphView.description.isEnabled = false
        binding.graphView.legend.isEnabled = false

        binding.graphView.axisLeft.setDrawGridLines(false)
        binding.graphView.axisLeft.setDrawLabels(false)
        binding.graphView.axisLeft.setDrawAxisLine(false)

        binding.graphView.xAxis.setDrawGridLines(false)
        binding.graphView.xAxis.setDrawLabels(false)
        binding.graphView.xAxis.setDrawAxisLine(false)

        binding.graphView.axisRight.setDrawGridLines(false)
        binding.graphView.axisRight.setDrawLabels(false)
        binding.graphView.axisRight.setDrawAxisLine(false)


    }

    private fun observeDataPing() {
        viewMoDel.listPingResultLive.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                barChart[it.size - 1] =
                    BarEntry(it.size.toFloat() - 1, it[it.size - 1].ping_value.toFloat())
                val barDataSet = BarDataSet(barChart, null)
                barDataSet.setGradientColor(
                    getColor(R.color.gradient_green_start_zero),
                    getColor(R.color.gradient_green_start)
                )
                val data = BarData(barDataSet)
                data.barWidth = .5f
                binding.graphView.data = data
                binding.graphView.invalidate()

            }

        }
    }

    private fun getDataFromBundle() {
        val bundle: Bundle? = this.arguments
        if (bundle != null) {
            val item: ContentPingTest = bundle.getParcelable(Constant.KEY_ITEM_PING)!!
            binding.title.text = item.title
            itemContentPingTest = item
        }
    }
}