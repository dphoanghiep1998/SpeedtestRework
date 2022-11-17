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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.speedtest_rework.R
import com.example.speedtest_rework.base.fragment.BaseFragment
import com.example.speedtest_rework.common.utils.Constant
import com.example.speedtest_rework.databinding.FragmentAdvancedPingBinding
import com.example.speedtest_rework.ui.ping_test.advanced_ping_test.adapter.RecentAdapter
import com.example.speedtest_rework.ui.ping_test.model.ContentPingTest
import com.example.speedtest_rework.viewmodel.SpeedTestViewModel
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry


class FragmentAdvancedPing : BaseFragment() {
    private lateinit var binding: FragmentAdvancedPingBinding
    private val viewMoDel: SpeedTestViewModel by activityViewModels()
    private var itemContentPingTest: ContentPingTest? = null
    private var barChart = ArrayList<BarEntry>()
    private var packetReceived = 0
    private var packetLoss = 0
    private var maxValue = 0
    private var colorsText: MutableList<Int> = mutableListOf()
    private lateinit var adapter: RecentAdapter
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
        observeListRecent()

    }

    private fun initBarArrayData() {
        barChart = ArrayList()
        colorsText = ArrayList()
        for (i in 0..9) {
            barChart.add(BarEntry((i + 1).toFloat(), 0f))
            colorsText.add(getColor(R.color.transparent))
        }

    }

    private fun initView() {
        binding.containerValue.visibility = View.GONE
        binding.tvStart.visibility = View.VISIBLE

        initRcv()
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
            binding.btnStart.isEnabled = false
            binding.pbLoading.progress = 0
            binding.containerValue.visibility = View.VISIBLE
            binding.tvStart.visibility = View.GONE
            val barDataSet = BarDataSet(barChart, null)
            val data = BarData(barDataSet)
            packetLoss = 0
            packetReceived = 0
            maxValue = 0
            data.barWidth = .5f
            binding.graphView.data = data
            binding.graphView.invalidate()
            viewMoDel.getPingResultAdvanced(itemContentPingTest!!.url)
        }

        binding.imvDelete.setOnClickListener {
            viewMoDel.deleteAllRecentAction()
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
        binding.graphView.animateY(1000, Easing.EaseOutBack);
        binding.graphView.setNoDataText("")

    }

    private fun initRcv() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        adapter = RecentAdapter()
        binding.rcvEdit.layoutManager = linearLayoutManager
        binding.rcvEdit.adapter = adapter
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
                if (it.size == 10) {
                    binding.btnStart.isEnabled = true
                }
                binding.tvPacketSentValue.text = (it.size).toString()
                setProgressAnimate(binding.pbLoading, it.size)
                if (it[it.size - 1].isReachable) {
                    binding.pingValue.text = it[it.size - 1].ping_value.toString()
                    if (it[it.size - 1].ping_value > maxValue) {
                        maxValue = it[it.size - 1].ping_value
                    }
                    binding.tvMs.visibility = View.VISIBLE
                    binding.tvPacketReceivedValue.text = (++packetReceived).toString()

                    binding.graphView.renderer = BarChartCustomRender(
                        binding.graphView,
                        binding.graphView.animator,
                        binding.graphView.viewPortHandler,
                        colorsText
                    )


                } else if (!it[it.size - 1].isReachable && it.size == 1) {
                    binding.pingValue.text = "_ _"
                    binding.tvMs.visibility = View.GONE
                    packetLoss++
                    countPacketLoss(it.size, packetLoss)
                } else {
                    packetLoss++
                    countPacketLoss(it.size, packetLoss)
                }
                val color = when (it[it.size - 1].ping_value) {
                    in 0..50 -> {
                        getColor(R.color.gradient_green_start)
                    }
                    in 51..100 -> {
                        getColor(R.color.gradient_yellow_end)
                    }
                    else -> {
                        getColor(R.color.gradient_red_start)
                    }
                }
                colorsText[it.size - 1] = color
                val data = BarData()
                data.barWidth = .5f
                binding.graphView.axisLeft.axisMaximum = (maxValue + 10).toFloat()
                binding.graphView.data = data

                barChart[it.size - 1] =
                    BarEntry(it.size.toFloat(), it[it.size - 1].ping_value.toFloat())
                val barDataSet = BarDataSet(barChart, null)
                barDataSet.setGradientColor(
                    getColor(R.color.gradient_green_start_zero),
                    getColor(R.color.gradient_green_start)
                )
                barDataSet.valueFormatter = ValueBarFormatter()
                if (it[it.size - 1].isReachable) {
                    barDataSet.setValueTextColors(colorsText)
                }

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

    private fun observeListRecent() {
        viewMoDel.getListRecent().observe(viewLifecycleOwner) {
            adapter.setList(it)
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
        viewMoDel.pingNormal?.cancel()
        viewMoDel.pingAdvanced?.cancel()
        viewMoDel.setDataPingResult(mutableListOf())
    }

    private fun getDataFromBundle() {
        val bundle: Bundle? = this.arguments
        if (bundle != null) {
            val item: ContentPingTest = bundle.getParcelable(Constant.KEY_ITEM_PING)!!
            binding.title.text = item.title
            itemContentPingTest = item
            if (item.normal) {
                binding.containerEdit.visibility = View.GONE
            } else {
                handleFlowItemAdvance()
            }
        }
    }

    private fun handleFlowItemAdvance() {
        binding.tvShowUrl.text = itemContentPingTest?.url
        binding.containerShowUrl.visibility = View.VISIBLE

        binding.containerShowUrl.setOnClickListener {
            it.visibility = View.GONE
            binding.containerEdit.visibility = View.VISIBLE
            binding.edtUrl.isFocusable = true
        }
    }

}