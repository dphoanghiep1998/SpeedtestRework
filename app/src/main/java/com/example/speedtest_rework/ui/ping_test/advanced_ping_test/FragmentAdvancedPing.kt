package com.example.speedtest_rework.ui.ping_test.advanced_ping_test

import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.animation.LinearInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.speedtest_rework.R
import com.example.speedtest_rework.base.dialog.BackPressBottomSheetDialogCallback
import com.example.speedtest_rework.base.dialog.BaseBottomDialogCallBack
import com.example.speedtest_rework.base.dialog.ConfirmDialog
import com.example.speedtest_rework.base.dialog.PingInfoDialog
import com.example.speedtest_rework.common.utils.AppSharePreference
import com.example.speedtest_rework.common.utils.clickWithDebounce
import com.example.speedtest_rework.databinding.FragmentAdvancedPingBinding
import com.example.speedtest_rework.ui.ping_test.advanced_ping_test.adapter.RecentAdapter
import com.example.speedtest_rework.ui.ping_test.advanced_ping_test.interfaces.RecentHelper
import com.example.speedtest_rework.ui.ping_test.model.ContentPingTest
import com.example.speedtest_rework.viewmodel.FragmentPingTestViewModel
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


open class FragmentAdvancedPing(private val itemContentPingTest: ContentPingTest) :
    BottomSheetDialogFragment(), RecentHelper, ConfirmDialog.ConfirmCallback {

    private lateinit var binding: FragmentAdvancedPingBinding
    private var normalType = true
    private val viewMoDel: FragmentPingTestViewModel by viewModels()
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
            colorsText.add(getColor(R.color.transparent))
        }
        val barDataSet = BarDataSet(barChart, null)
        val data = BarData(barDataSet)
        data.barWidth = .3f
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
        binding.btnBack.clickWithDebounce {
            dismiss()
        }
        binding.btnStart.clickWithDebounce {
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
            packetSent = 0
            binding.tvPacketLossValue.text = "0%"
            binding.tvPacketSentValue.text = "0"
            binding.tvPacketReceivedValue.text = "0"
            data.barWidth = .3f
            binding.graphView.data = data
            binding.graphView.invalidate()
            if (normalType) {
                viewMoDel.getPingResultAdvanced(itemContentPingTest.url)
            } else {
                viewMoDel.getPingResultAdvanced(cUrl)
            }


        }

        binding.imvDelete.clickWithDebounce {
            if (viewMoDel.listRecent.value!!.isEmpty()) {
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
        binding.graphView.xAxis.axisMaximum =
           10f

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


    private fun observeDataPing() {
        viewMoDel.listPingResultLive.observe(viewLifecycleOwner) {
            lifecycleScope.launch(Dispatchers.Main) {
                if (it.isNotEmpty()) {
                    setProgressAnimate(binding.pbLoading, 9)
                    packetSent = it.size
                    binding.tvPacketSentValue.text = packetSent.toString()
                    if (it[it.size - 1].isReachable) {
                        binding.pingValue.text = it[it.size - 1].ping_value.toString()
                        avgLatency += it[it.size - 1].ping_value.toInt()
                        if (it[it.size - 1].ping_value > maxValue) {
                            maxValue = it[it.size - 1].ping_value.toInt()
                        }
                        if (it[it.size - 1].ping_value < minLatency) {
                            minLatency = it[it.size - 1].ping_value.toInt()
                        }
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
                    binding.graphView.renderer = BarChartCustomRender(
                        binding.graphView,
                        binding.graphView.animator,
                        binding.graphView.viewPortHandler,
                        colorsText
                    )
                    val color = when (it[it.size - 1].ping_value) {
                        in 0f..50f -> {
                            getColor(R.color.gradient_green_start)
                        }
                        in 51f..100f -> {
                            getColor(R.color.gradient_yellow_end)
                        }
                        else -> {
                            getColor(R.color.gradient_red_start)
                        }
                    }
                    colorsText[it.size - 1] = color
                    val data = BarData()
                    if (maxValue == 0) {
                        binding.graphView.axisLeft.axisMaximum = (maxValue + 100).toFloat()
                    } else {
                        binding.graphView.axisLeft.axisMaximum = (maxValue + 20).toFloat()
                    }

                    binding.graphView.data = data

                    barChart.add(
                        BarEntry(it.size.toFloat(), it[it.size - 1].ping_value + 10)
                    )
                    data.barWidth = .3f
-
                    BarDataSet(barChart, null).also { it1 ->
                        it1.setGradientColor(
                            getColor(R.color.gradient_green_start_zero),
                            getColor(R.color.gradient_green_start)
                        )
                        it1.valueFormatter = ValueBarFormatter()
                        data.addDataSet(it1)
                    }
                    with(binding.graphView) {
                        this.notifyDataSetChanged()
                        this.invalidate()
                    }
                    if (it.size == 10) {
                        setProgressAnimate(binding.pbLoading, 10, 1000)
                        binding.tvStart.visibility = View.VISIBLE
                        binding.containerShowUrl.isEnabled = true
                        binding.containerValue.visibility = View.GONE
                        with(binding.btnStart) {
                            this.isEnabled = true
                            this.visibility = View.VISIBLE
                        }
                        if (packetReceived == 0) {
                            val dialogPing = PingInfoDialog(
                                requireContext(),
                                cUrl,
                                packetLoss.toString(),
                                packetSent.toString(),
                                packetReceived.toString(),
                                "_ _",
                                "_ _",
                                "_ _"
                            )
                            dialogPing.show()
                        } else {
                            val dialogPing = PingInfoDialog(
                                requireContext(),
                                cUrl,
                                packetLoss.toString(),
                                packetSent.toString(),
                                packetReceived.toString(),
                                minLatency.toString(),
                                (avgLatency / packetReceived).toString(),
                                maxValue.toString()
                            )
                            dialogPing.show()
                        }

                    }

                } else {
                    resetData()
                }
            }

        }
    }


    private fun setProgressAnimate(pb: ProgressBar, progressTo: Int, duration: Long = 6000) {
        val animation: ObjectAnimator =
            ObjectAnimator.ofInt(pb, "progress", pb.progress, progressTo * 100)
        animation.duration = duration
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
            viewMoDel.flagChangeBack.postValue(true)
        }
    }

    private fun checkUrl(input: String): Boolean {
        return android.util.Patterns.WEB_URL.matcher(input).matches()
    }

    private fun observeFlagChangeBack() {
        viewMoDel.flagChangeBack.observe(viewLifecycleOwner) {

            if (it) {
                binding.btnBack.clickWithDebounce {
                    binding.containerEdit.visibility = View.GONE
                    binding.containerShowUrl.visibility = View.VISIBLE
                    hideKeyboard()
                    viewMoDel.flagChangeBack.postValue(false)
                }
            } else {
                binding.btnBack.clickWithDebounce {
                    dismiss()
                }

            }
        }
    }


    private val callback = object : BackPressBottomSheetDialogCallback {
        override fun shouldInterceptBackPress(): Boolean {
            return viewMoDel.flagChangeBack.value!!
        }

        override fun onBackPressIntercepted() {
            binding.containerEdit.visibility = View.GONE
            binding.containerShowUrl.visibility = View.VISIBLE
            hideKeyboard()
            viewMoDel.flagChangeBack.postValue(false)
        }

    }


    private fun observeListRecent() {
        viewMoDel.listRecent.observe(viewLifecycleOwner) {
            adapter.setList(it)
            AppSharePreference.INSTANCE.saveRecentList(it)
        }
    }


    private fun doCheckPing(input: String) {
        binding.tvShowUrl.text = input
        if (checkUrl(input)) {
            resetData()
            binding.containerShowUrl.isEnabled = false
            viewMoDel.flagChangeBack.postValue(false)
            viewMoDel.stopPing = false
            binding.containerValue.visibility = View.VISIBLE
            binding.tvStart.visibility = View.GONE
            if (!input.startsWith("http://") || !input.startsWith("https://")) {
                val address = "http://$input"
                cUrl = address
                viewMoDel.getPingResultAdvanced(address)
            } else {
                cUrl = input
                viewMoDel.getPingResultAdvanced(input)
            }

            handleInsertNewRecent(input)
        } else {
            Toast.makeText(
                requireContext(), getString(R.string.wrong_website_format), Toast.LENGTH_SHORT
            ).show()
        }

    }

    private fun handleInsertNewRecent(input: String) {
        val mList = mutableListOf<String>()
        mList.addAll(viewMoDel.listRecent.value!!)
        if (input in mList) {
            mList.remove(input)
        }
        mList.add(0, input)
        if (mList.size > 5) {
            mList.removeAt(5)
        }
        viewMoDel.listRecent.postValue(mList)

    }

    override fun onClickItem(input: String) {
        hideKeyboard()
        binding.tvShowUrl.text = input
        resetData()
        binding.containerValue.visibility = View.VISIBLE
        binding.tvStart.visibility = View.GONE
        viewMoDel.stopPing = false

        if (!input.startsWith("http://") || !input.startsWith("https://")) {
            Log.d("TAG", "onClickItem: ")
            val address = "http://$input"
            cUrl = address
            viewMoDel.getPingResultAdvanced(address)
        } else {
            Log.d("TAG", "else: ")
            cUrl = input
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
    }

    private fun getColor(resId: Int): Int {
        return ContextCompat.getColor(requireContext(), resId)
    }

    override fun negativeAction() {
    }

    override fun positiveAction() {
        viewMoDel.listRecent.postValue(mutableListOf())
    }

}