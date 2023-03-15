package com.example.speedtest_rework.ui.ping_test.advanced_ping_test

import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.view.animation.LinearInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.speedtest_rework.R
import com.example.speedtest_rework.base.dialog.*
import com.example.speedtest_rework.common.custom_view.ConnectionType
import com.example.speedtest_rework.common.extensions.InterAds
import com.example.speedtest_rework.common.extensions.showBannerAds
import com.example.speedtest_rework.common.extensions.showInterAds
import com.example.speedtest_rework.common.utils.AppSharePreference
import com.example.speedtest_rework.common.utils.clickWithDebounce
import com.example.speedtest_rework.databinding.FragmentAdvancedPingBinding
import com.example.speedtest_rework.ui.ping_test.advanced_ping_test.adapter.RecentAdapter
import com.example.speedtest_rework.ui.ping_test.advanced_ping_test.interfaces.RecentHelper
import com.example.speedtest_rework.ui.ping_test.model.ContentPingTest
import com.example.speedtest_rework.viewmodel.FragmentPingTestViewModel
import com.example.speedtest_rework.viewmodel.SpeedTestViewModel
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlin.math.roundToInt


open class FragmentAdvancedPing(private val itemContentPingTest: ContentPingTest) :
    BottomSheetDialogFragment(), RecentHelper, ConfirmDialog.ConfirmCallback {

    private lateinit var binding: FragmentAdvancedPingBinding
    private var normalType = true
    private val viewModel: FragmentPingTestViewModel by viewModels()
    private val mainViewModel: SpeedTestViewModel by activityViewModels()
    private var barChart = ArrayList<BarEntry>()
    private var packetReceived = 0
    private var packetLoss = 0
    private var packetSent = 0
    private var minLatency = 99999
    private var avgLatency = 0
    private var maxValue = 0
    private var colorsText: MutableList<Int> = mutableListOf()
    private var cUrl = ""
    private lateinit var adapter: RecentAdapter
    private var changeBackButton = true

    init {
        cUrl = itemContentPingTest.url
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val root = ConstraintLayout(requireContext())
        root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        )
        val dialog = BaseBottomDialogCallBack(requireContext(), callback)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(root)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(getColor(R.color.background_main)))
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        )

        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdvancedPingBinding.inflate(inflater, container, false)
        showBannerAds(binding.bannerAds)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.listPingResultLive.postValue(mutableListOf())
        getDataFromBundle()
        initBarArrayData()
        initView()
        observeDataPing()
        observeListRecent()
        observeConnectionType()
        observeErrorGetHost()
    }

    private fun observeErrorGetHost() {
        viewModel.isGetHostError.observe(viewLifecycleOwner) {
            if(it){
                resetData()
                Toast.makeText(requireContext(), getText(R.string.unknown_host), Toast.LENGTH_SHORT)
                    .show()
            }

        }
    }

    private fun observeConnectionType() {
        mainViewModel.typeNetwork.observe(viewLifecycleOwner) {
            if (it == ConnectionType.UNKNOWN) {
                binding.requestWifiContainer.visibility = View.VISIBLE
            } else {
                binding.requestWifiContainer.visibility = View.GONE
            }
        }
    }


    private fun initBarArrayData() {
        binding.graphView.clear()
        binding.graphView.notifyDataSetChanged()
        barChart = ArrayList()
        colorsText = ArrayList()
        for (i in 0..9) {
            barChart.add(BarEntry((i + 1).toFloat(), 0f))
            colorsText.add(getColor(R.color.transparent))
        }
        val barDataSet = BarDataSet(barChart, null)
        val data = BarData(barDataSet)
        data.barWidth = .5f
        binding.graphView.data = data
        binding.graphView.invalidate()

    }


    private fun initView() {
        binding.containerValue.visibility = View.GONE
        binding.tvStart.visibility = View.VISIBLE
        binding.title.text = itemContentPingTest.title
        resetData()
        initEditText()
        initRcv()
        initButton()
        initChart()
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
        binding.btnSetting.clickWithDebounce {
            val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
            startActivity(intent)
        }
        binding.btnInfo.clickWithDebounce {
            val pingInfoPopup = PingInfoPopup(requireContext())
            pingInfoPopup.show()
        }
        binding.btnBack.clickWithDebounce {
            if (changeBackButton) {
                dismiss()
            } else {
                binding.containerEdit.visibility = View.GONE
                binding.containerShowUrl.visibility = View.VISIBLE
                hideKeyboard()
                changeBackButton = true
            }
        }
        binding.btnStart.clickWithDebounce {
            resetData()
            viewModel.stopPing = false
            viewModel.listPingResultLive.postValue(mutableListOf())
            initBarArrayData()
            binding.btnStart.isEnabled = false
            binding.pbLoading.progress = 0
            binding.containerValue.visibility = View.VISIBLE
            binding.tvStart.visibility = View.GONE
            binding.containerShowUrl.isEnabled = false
            val barDataSet = BarDataSet(barChart, null)
            val data = BarData(barDataSet)
            binding.tvPacketLossValue.text = "0%"
            binding.tvPacketSentValue.text = "0"
            binding.tvPacketReceivedValue.text = "0"
            data.barWidth = .5f
            binding.graphView.data = data
            binding.graphView.invalidate()

            if (normalType) {
                viewModel.getPingResultAdvanced(itemContentPingTest.url)
            } else {
                viewModel.getPingResultAdvanced(cUrl)
            }
        }

        binding.imvDelete.clickWithDebounce {
            if (viewModel.listRecent.value!!.isEmpty()) {
                Toast.makeText(requireContext(), R.string.no_list_recet_found, Toast.LENGTH_SHORT)
                    .show()
            } else {
                val customDialog = ConfirmDialog(
                    requireActivity(),
                    this,
                    getString(R.string.delete_all_recent_title),
                    getString(R.string.delete_all_recent),
                    getString(R.string.YES),
                    getString(R.string.NO)
                )
                customDialog.show()
            }

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

    private fun countMaxMinAvg(value: Int) {
        avgLatency += value
        if (value > maxValue) {
            maxValue = value
        }
        if (value < minLatency && value != -1) {
            minLatency = value
        }
    }

    private fun showPing(value: Int) {
        binding.pingValue.text = value.toString()
        binding.tvMs.visibility = View.VISIBLE
    }

    private fun hidePing() {
        binding.pingValue.text = "_ _"
        binding.tvMs.visibility = View.GONE
    }

    private fun countPacketLoss() {
        packetLoss += 1
        countPacketLoss(packetSent, packetLoss)
    }

    private fun countPacketReceived() {
        packetReceived += 1
        binding.tvPacketReceivedValue.text = (packetReceived).toString()
    }

    private fun countPacketLoss(packetSent: Int, lossNumber: Int) {
        val percent = (lossNumber.toFloat() / packetSent.toFloat()) * 100
        binding.tvPacketLossValue.text = "${percent.toInt()} %"

    }

    private fun genColorBar(value: Int) = when (value) {
        in 0..50 -> getColor(R.color.gradient_green_start)

        in 51..100 -> getColor(R.color.gradient_yellow_end)

        in 100..99999999 -> getColor(R.color.gradient_red_start)

        else -> getColor(R.color.transparent)

    }


    private fun observeDataPing() {
        viewModel.listPingResultLive.observe(viewLifecycleOwner) {
            packetReceived = 0
            packetLoss = 0
            packetSent = 0
            avgLatency = 0

            if (it.isNotEmpty()) {
                Log.d("TAG", "observeDataPing: " + it.size)
                packetSent = it.size
                binding.tvPacketSentValue.text = packetSent.toString()

                if (it.size == 1 && it[0].isReachable) {
                    setProgressAnimate(binding.pbLoading, 98, 10000)
                } else if (it.size == 1 && !it[0].isReachable) {
                    setProgressAnimate(binding.pbLoading, 98, 19000)
                }
                val data = BarData()
                data.barWidth = .5f
                packetReceived = 0
                packetLoss = 0
                binding.graphView.data = data
                it.mapIndexed { index, item ->
                    if (!item.isReachable && index == 0) {
                        countPacketLoss()
                        hidePing()
                    } else if (!item.isReachable) {
                        countPacketLoss()
                    } else {
                        countPacketReceived()
                    }

                    colorsText[index] = genColorBar(item.ping_value.roundToInt())
                    barChart[index] = BarEntry(
                        index.toFloat(), item.ping_value.roundToInt() + 10f
                    )

                    countMaxMinAvg(it[it.size - 1].ping_value.roundToInt())
                    showPing(it[it.size - 1].ping_value.roundToInt())
                    if (maxValue == 0) {
                        binding.graphView.axisLeft.axisMaximum = (maxValue + 100).toFloat()
                    } else {
                        binding.graphView.axisLeft.axisMaximum = (maxValue + 50).toFloat()
                    }
                }
                if (it.size == 10) {
                    setProgressAnimate(binding.pbLoading, 100, 1000)
                    binding.tvStart.visibility = View.VISIBLE
                    binding.containerShowUrl.isEnabled = true
                    binding.containerValue.visibility = View.GONE
                    with(binding.btnStart) {
                        this.isEnabled = true
                        this.visibility = View.VISIBLE
                    }
                    if (packetReceived == 0) {
                        showInterAds(action = {
                            showFailedResult()
                        },InterAds.PING_TEST_SUCCESS)
                    } else {
                        showInterAds(action = {
                            showSuccessResult()
                        },InterAds.PING_TEST_SUCCESS)
                    }

                }

                BarDataSet(barChart, null).also { it1 ->
                    it1.setGradientColor(
                        getColor(R.color.gradient_green_start_zero),
                        getColor(R.color.gradient_green_start)
                    )
                    it1.valueFormatter = ValueBarFormatter()
                    data.addDataSet(it1)
                }
                binding.graphView.data = data
                binding.graphView.renderer = BarChartCustomRender(
                    binding.graphView,
                    binding.graphView.animator,
                    binding.graphView.viewPortHandler,
                    colorsText
                )
                with(binding.graphView) {
                    this.notifyDataSetChanged()
                    this.invalidate()
                }

            }
        }
    }

    private fun showFailedResult(){
        val dialogPing = PingInfoDialog(
            requireContext(),
            cUrl,
            binding.tvPacketLossValue.text.toString(),
            packetSent.toString(),
            packetReceived.toString(),
            "NaN",
            "NaN",
            "NaN"
        )
        dialogPing.show()
    }

    private fun showSuccessResult(){
        val dialogPing = PingInfoDialog(
            requireContext(),
            cUrl,
            binding.tvPacketLossValue.text.toString(),
            packetSent.toString(),
            packetReceived.toString(),
            minLatency.toString(),
            (avgLatency / packetReceived).toString(),
            maxValue.toString()
        )
        dialogPing.show()
    }


    private fun setProgressAnimate(
        pb: ProgressBar, progressTo: Int, duration: Long = 8000
    ) {
        val animation: ObjectAnimator =
            ObjectAnimator.ofInt(pb, "progress", pb.progress, progressTo * 10)
        animation.duration = duration
        animation.interpolator = LinearInterpolator()
        animation.setAutoCancel(true)
        animation.start()
    }


    override fun onDestroy() {
        super.onDestroy()
        viewModel.stopPing = true
        viewModel.pingNormal?.cancel()
        viewModel.pingAdvanced?.cancel()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        viewModel.stopPing = true
        viewModel.pingNormal?.cancel()
        viewModel.pingAdvanced?.cancel()
    }

    private fun getDataFromBundle() {
        if (itemContentPingTest.normal) {
            normalType = true
            binding.containerEdit.visibility = View.GONE
        } else {
            normalType = false
            handleFlowItemAdvance()
        }
    }

    private fun handleFlowItemAdvance() {
        binding.tvShowUrl.text = itemContentPingTest.url
        binding.containerShowUrl.visibility = View.VISIBLE
        binding.containerShowUrl.setOnClickListener {
            it.visibility = View.GONE
            binding.containerEdit.visibility = View.VISIBLE
            binding.edtUrl.requestFocus()
            showKeyboard()
            changeBackButton = false
        }
    }

    private fun checkUrl(input: String): Boolean {
        return android.util.Patterns.WEB_URL.matcher(input).matches()
    }

    private val callback = object : BackPressBottomSheetDialogCallback {
        override fun shouldInterceptBackPress(): Boolean {
            return true
        }

        override fun onBackPressIntercepted() {
            if (changeBackButton) {
                dismiss()
            } else {
                binding.containerEdit.visibility = View.GONE
                binding.containerShowUrl.visibility = View.VISIBLE
                hideKeyboard()
                changeBackButton = true
            }

        }

    }


    private fun observeListRecent() {

        viewModel.listRecent.observe(viewLifecycleOwner) {
            adapter.setList(it)
            AppSharePreference.INSTANCE.saveRecentList(it)
        }
    }


    private fun doCheckPing(input: String) {
        binding.tvShowUrl.text = input
        if (checkUrl(input)) {
            viewModel.listPingResultLive.postValue(mutableListOf())
            resetData()
            binding.containerShowUrl.isEnabled = false
            viewModel.stopPing = false
            binding.containerValue.visibility = View.VISIBLE
            binding.tvStart.visibility = View.GONE
            if (!input.startsWith("http://") || !input.startsWith("https://")) {
                val address = "http://$input"
                cUrl = address
                viewModel.getPingResultAdvanced(address)
            } else {
                cUrl = input
                viewModel.getPingResultAdvanced(input)
            }
            handleInsertNewRecent(input)
        } else {
            Toast.makeText(
                requireContext(), getString(R.string.wrong_website_format), Toast.LENGTH_SHORT
            ).show()
        }

    }

    private fun handleInsertNewRecent(input: String) {
        changeBackButton = true
        binding.btnStart.isEnabled = false
        val mList = mutableListOf<String>()
        mList.addAll(viewModel.listRecent.value!!)
        if (input in mList) {
            mList.remove(input)
        }
        mList.add(0, input)
        if (mList.size > 5) {
            mList.removeAt(5)
        }
        viewModel.listRecent.postValue(mList)

    }

    override fun onClickItem(input: String) {
        viewModel.listPingResultLive.postValue(mutableListOf())
        hideKeyboard()
        binding.tvShowUrl.text = input
        resetData()
        binding.containerValue.visibility = View.VISIBLE
        binding.tvStart.visibility = View.GONE
        viewModel.stopPing = false
        if (!input.startsWith("http://") || !input.startsWith("https://")) {
            val address = "http://$input"
            cUrl = address
            viewModel.getPingResultAdvanced(address)
        } else {
            cUrl = input
            viewModel.getPingResultAdvanced(input)
        }
        binding.containerEdit.visibility = View.GONE
        binding.containerShowUrl.visibility = View.VISIBLE
        binding.containerShowUrl.isEnabled = false
        handleInsertNewRecent(input)
    }


    private fun hideKeyboard() {
        val imm: InputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        imm.hideSoftInputFromWindow(
            requireView().windowToken, InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

    private fun showKeyboard() {
        val imm: InputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        imm.showSoftInput(
            binding.edtUrl, InputMethodManager.SHOW_IMPLICIT
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
        setProgressAnimate(binding.pbLoading, 0, 0L)
        binding.tvPacketLossValue.text = "0%"
        binding.tvPacketSentValue.text = "0"
        binding.tvPacketReceivedValue.text = "0"
        packetReceived = 0
        packetLoss = 0
        maxValue = 0
        packetSent = 0
        minLatency = 99999
        avgLatency = 0
        maxValue = 0
        initBarArrayData()
        changeBackButton = true
    }

    private fun getColor(resId: Int): Int {
        return ContextCompat.getColor(requireContext(), resId)
    }

    override fun negativeAction() {
    }

    override fun positiveAction() {
        viewModel.listRecent.value = listOf()
    }

}