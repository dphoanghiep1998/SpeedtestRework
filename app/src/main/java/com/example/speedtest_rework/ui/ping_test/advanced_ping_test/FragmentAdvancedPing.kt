package com.example.speedtest_rework.ui.ping_test.advanced_ping_test

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.speedtest_rework.R
import com.example.speedtest_rework.base.fragment.BaseFragment
import com.example.speedtest_rework.common.utils.AppSharePreference
import com.example.speedtest_rework.common.utils.Constant
import com.example.speedtest_rework.databinding.FragmentAdvancedPingBinding
import com.example.speedtest_rework.ui.ping_test.advanced_ping_test.adapter.RecentAdapter
import com.example.speedtest_rework.ui.ping_test.advanced_ping_test.interfaces.RecentHelper
import com.example.speedtest_rework.ui.ping_test.model.ContentPingTest
import com.example.speedtest_rework.viewmodel.SpeedTestViewModel
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry


class FragmentAdvancedPing : BaseFragment(), RecentHelper {
    private lateinit var binding: FragmentAdvancedPingBinding
    private var normalType = true
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
        viewMoDel.setDataPingResult(mutableListOf())
        getDataFromBundle()
        initBarArrayData()
        initView()
        observeDataPing()
        observeListRecent()
        observeFlagChangeBack()

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
        resetData()
        initEditText()
        initRcv()
        initButton()
        initChart()
        changeBackPressCallBack()
    }

    private fun initEditText() {
        binding.edtUrl.setOnKeyListener { _, keyCode, keyEvent ->
            if ((keyEvent.action == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                doCheckPing(binding.edtUrl.text.toString())
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
    }

    private fun initButton() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()

        }
        binding.btnStart.setOnClickListener {
            viewMoDel.stopPing = false
            initBarArrayData()
            binding.btnStart.isEnabled = false
            binding.pbLoading.progress = 0
            binding.containerValue.visibility = View.VISIBLE
            binding.tvStart.visibility = View.GONE
            binding.containerShowUrl.isEnabled = false

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
            viewMoDel.listRecent.value = listOf()
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
        adapter = RecentAdapter(this)
        binding.rcvEdit.layoutManager = linearLayoutManager
        binding.rcvEdit.adapter = adapter
    }

    private fun changeBackPressCallBack() {
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()

            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }

    private fun observeDataPing() {
        viewMoDel.listPingResultLive.observe(viewLifecycleOwner) {
            Log.d("TAG", "observeDataPing: " + it.size)
            if (it.isNotEmpty()) {
                if (it.size == 10) {
                    binding.tvStart.visibility = View.VISIBLE
                    binding.containerShowUrl.isEnabled = true
                    binding.containerValue.visibility = View.GONE
                    with(binding.btnStart) {
                        this.isEnabled = true
                        this.visibility = View.VISIBLE
                    }
                }
                setProgressAnimate(binding.pbLoading, it.size)
                binding.tvPacketSentValue.text = (it.size).toString()
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

                BarDataSet(barChart, null).also { it1 ->
                    it1.setGradientColor(
                        getColor(R.color.gradient_green_start_zero),
                        getColor(R.color.gradient_green_start)
                    )
                    it1.valueFormatter = ValueBarFormatter()
                    if (it[it.size - 1].isReachable) {
                        it1.setValueTextColors(colorsText)
                    }
                    data.addDataSet(it1)
                }

                with(binding.graphView) {
                    this.notifyDataSetChanged()
                    this.invalidate()
                }

            } else {
                resetData()
            }
        }
    }


    private fun setProgressAnimate(pb: ProgressBar, progressTo: Int) {
        val animation: ObjectAnimator =
            ObjectAnimator.ofInt(pb, "progress", pb.progress, progressTo * 100)
        animation.duration = 2000
        animation.interpolator = LinearInterpolator()
        animation.setAutoCancel(true)
        animation.start()
    }

    private fun countPacketLoss(packetSent: Int, lossNumber: Int) {
        val percent = (lossNumber.toFloat() / packetSent.toFloat()) * 100
        binding.tvPacketLossValue.text = "${percent.toInt()} %"

    }


    override fun onDestroy() {
        super.onDestroy()
        viewMoDel.stopPing = true
        viewMoDel.pingNormal?.cancel()
        viewMoDel.pingAdvanced?.cancel()
    }

    private fun getDataFromBundle() {
        val bundle: Bundle? = this.arguments
        if (bundle != null) {
            val item: ContentPingTest = bundle.getParcelable(Constant.KEY_ITEM_PING)!!
            binding.title.text = item.title
            itemContentPingTest = item
            if (item.normal) {
                normalType = true
                binding.containerEdit.visibility = View.GONE
            } else {
                normalType = false
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
            binding.edtUrl.requestFocus()
            viewMoDel.flagChangeBack.postValue(true)
        }
    }

    private fun checkUrl(input: String): Boolean {
        return android.util.Patterns.WEB_URL.matcher(input).matches()
    }

    private fun observeFlagChangeBack() {
        viewMoDel.flagChangeBack.observe(viewLifecycleOwner) {
            if (it) {
                binding.btnBack.setOnClickListener {
                    binding.containerEdit.visibility = View.GONE
                    binding.containerShowUrl.visibility = View.VISIBLE
                    viewMoDel.flagChangeBack.postValue(false)
                }
            } else {
                binding.btnBack.setOnClickListener {
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun observeListRecent() {
        viewMoDel.listRecent.value = AppSharePreference.INSTANCE.getRecentList(
            R.string.key_recent_list, listOf()
        )
        viewMoDel.listRecent.observe(viewLifecycleOwner) {
            adapter.setList(it)
            AppSharePreference.INSTANCE.saveRecentList(R.string.key_recent_list, it)
        }
    }


    private fun doCheckPing(input: String) {
        binding.tvShowUrl.text = input
        if (checkUrl(input)) {
            if (!input.startsWith("http://") || !input.startsWith("https://")) {
                val address = "http://$input"
                viewMoDel.getPingResultAdvanced(address)
            } else {
                viewMoDel.getPingResultAdvanced(input)
            }
            resetData()
            binding.containerShowUrl.isEnabled = false

            viewMoDel.flagChangeBack.postValue(false)
            handleInsertNewRecent(input)
        } else {
            Toast.makeText(
                requireContext(), getString(R.string.wrong_website_format), Toast.LENGTH_SHORT
            ).show()
        }

    }

    private fun handleInsertNewRecent(input: String) {
        viewMoDel.listRecent.value?.let {
            val mList = it.toMutableList()
            var flag = true
            mList.forEach { i ->
                if (i == input) {
                    mList.remove(i)
                    mList.add(0, i)
                    flag = false
                }
            }
            if (flag) {
                mList.add(0, input)
            }
            if (mList.size > 4) {
                mList.removeAt(5)
            }
            viewMoDel.listRecent.value = mList
        }

    }

    override fun onClickItem(input: String) {
        hideKeyboard()
        binding.tvShowUrl.text = input
        resetData()
        binding.containerValue.visibility = View.VISIBLE
        binding.tvStart.visibility = View.GONE
        viewMoDel.stopPing = false
        if (!input.startsWith("http://") || !input.startsWith("https://")) {
            val address = "http://$input"
            viewMoDel.getPingResultAdvanced(address)
        } else {
            viewMoDel.getPingResultAdvanced(input)
        }
        binding.containerEdit.visibility = View.GONE
        binding.containerShowUrl.visibility = View.VISIBLE
        binding.containerShowUrl.isEnabled = false
        viewMoDel.flagChangeBack.postValue(false)
        handleInsertNewRecent(input)
    }

    private fun hideKeyboard() {
        val imm: InputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        imm.hideSoftInputFromWindow(
            requireView().windowToken, InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

    private fun resetData() {
        hideKeyboard()
        if (!normalType) {
            binding.containerEdit.visibility = View.GONE
            binding.containerShowUrl.visibility = View.VISIBLE
            binding.containerShowUrl.isEnabled = true
        }
        binding.btnStart.isEnabled = true
        binding.containerValue.visibility = View.GONE
        binding.tvStart.visibility = View.VISIBLE
        setProgressAnimate(binding.pbLoading,0)
        binding.tvPacketLossValue.text = "0 %"
        binding.tvPacketSentValue.text = "0"
        binding.tvPacketReceivedValue.text = "0"

        barChart = ArrayList()
        packetReceived = 0
        packetLoss = 0
        maxValue = 0
        colorsText = mutableListOf()
        initBarArrayData()
        val barDataSet = BarDataSet(barChart, null)
        val data = BarData(barDataSet)
        data.barWidth = .5f
        binding.graphView.data = data
        binding.graphView.invalidate()
    }

}