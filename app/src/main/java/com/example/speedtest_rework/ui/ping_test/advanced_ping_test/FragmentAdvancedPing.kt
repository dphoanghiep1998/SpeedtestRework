package com.example.speedtest_rework.ui.ping_test.advanced_ping_test

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ProgressBar
import androidx.activity.OnBackPressedCallback
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
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler


class FragmentAdvancedPing : BaseFragment() {
    private lateinit var binding: FragmentAdvancedPingBinding
    private val viewMoDel: SpeedTestViewModel by activityViewModels()
    private var itemContentPingTest: ContentPingTest? = null
    private var barChart = ArrayList<BarEntry>()
    private var packetReceived = 0
    private var packetLoss = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
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
            barChart.add(BarEntry((i + 1).toFloat(), 0f))
        }

    }

    private fun initView() {
        binding.containerValue.visibility = View.GONE
        binding.tvStart.visibility = View.VISIBLE

        initButton()
        initChart()
        changeBackPressCallBack()
    }

    private fun initButton() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnStart.setOnClickListener {
            initBarArrayData()
            binding.pbLoading.progress = 0
            binding.containerValue.visibility = View.VISIBLE
            binding.tvStart.visibility = View.GONE
            val barDataSet = BarDataSet(barChart, null)
            val data = BarData(barDataSet)
            packetLoss = 0
            packetReceived = 0
            data.barWidth = .5f
            binding.graphView.data = data
            binding.graphView.invalidate()
            viewMoDel.getPingResultAdvanced(itemContentPingTest!!.url)
        }
    }

    private fun initChart() {

        binding.graphView.setTouchEnabled(false)
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
        binding.graphView.axisLeft.axisMinimum = 0f
        binding.graphView.setNoDataText("")

    }

    private fun changeBackPressCallBack() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()

                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }

    private fun observeDataPing() {
        viewMoDel.listPingResultLive.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                binding.tvPacketSentValue.text = (it.size).toString()
                setProgressAnimate(binding.pbLoading, it.size)
                if (it[it.size - 1].isReachable) {
                    binding.pingValue.text = it[it.size - 1].ping_value.toString()
                    binding.tvMs.visibility = View.VISIBLE
                    binding.tvPacketReceivedValue.text = (++packetReceived).toString()
                } else if (!it[it.size - 1].isReachable && it.size == 1) {
                    binding.pingValue.text = "_ _"
                    binding.tvMs.visibility = View.GONE
                    packetLoss++
                    countPacketLoss(it.size, packetLoss)
                } else {
                    packetLoss++
                    countPacketLoss(it.size, packetLoss)
                }
                val data = BarData()
                data.barWidth = .5f
                binding.graphView.data = data

                barChart[it.size - 1] =
                    BarEntry(it.size.toFloat(), it[it.size - 1].ping_value.toFloat())
                val barDataSet = BarDataSet(barChart, null)
                barDataSet.setGradientColor(
                    getColor(R.color.gradient_green_start_zero),
                    getColor(R.color.gradient_green_start)
                )
                barDataSet.valueFormatter = ValueBarFormatter()

                data.addDataSet(barDataSet)
                binding.graphView.notifyDataSetChanged()
                binding.graphView.invalidate()
            } else {
                val data = BarData()
                binding.graphView.data = data
                binding.graphView.notifyDataSetChanged()
                binding.graphView.invalidate()
            }
        }
    }

    private fun setProgressAnimate(pb: ProgressBar, progressTo: Int) {
        val animation: ObjectAnimator =
            ObjectAnimator.ofInt(pb, "progress", pb.progress, progressTo * 100)
        animation.duration = 3000
        animation.interpolator = DecelerateInterpolator()
        animation.start()
    }

    private fun countPacketLoss(packetSent: Int, lossNumber: Int) {
        val percent = (lossNumber.toFloat() / packetSent.toFloat()) * 100
        binding.tvPacketLossValue.text = "${percent.toInt()} %"

    }

    override fun onDestroy() {
        super.onDestroy()
        viewMoDel.setDataPingResult(mutableListOf())
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