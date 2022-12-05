package com.example.speedtest_rework.ui.main.analyzer

import android.content.Context
import android.content.Intent
import android.graphics.Insets
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.speedtest_rework.R
import com.example.speedtest_rework.base.fragment.BaseFragment
import com.example.speedtest_rework.common.utils.buildMinVersionR
import com.example.speedtest_rework.common.utils.clickWithDebounce
import com.example.speedtest_rework.databinding.FragmentAnalyzerBinding
import com.example.speedtest_rework.databinding.LayoutChangeFrequencyBinding
import com.example.speedtest_rework.ui.main.analyzer.adapter.ItemTouchHelper
import com.example.speedtest_rework.ui.main.analyzer.adapter.ListSizeListener
import com.example.speedtest_rework.ui.main.analyzer.adapter.WifiChannelAdapter
import com.example.speedtest_rework.ui.main.analyzer.band.WiFiBand
import com.example.speedtest_rework.ui.main.analyzer.graph.ChannelGraphAdapter
import com.example.speedtest_rework.ui.main.analyzer.model.Cache
import com.example.speedtest_rework.ui.main.analyzer.model.Transformer
import com.example.speedtest_rework.ui.main.analyzer.model.WiFiData
import com.example.speedtest_rework.ui.main.analyzer.model.WiFiDetail
import com.example.speedtest_rework.viewmodel.SpeedTestViewModel


class FragmentAnalyzer : BaseFragment(), ItemTouchHelper, ListSizeListener {
    private lateinit var binding: FragmentAnalyzerBinding
    private val viewModel: SpeedTestViewModel by activityViewModels()
    private lateinit var adapter: WifiChannelAdapter
    private var mainWifi: WifiManager? = null
    private lateinit var channelGraphAdapter: ChannelGraphAdapter
    private lateinit var wiFiData: WiFiData
    private lateinit var popupWindow: PopupWindow
    private var wiFiChannelPair = WiFiBand.GHZ2.wiFiChannels.wiFiChannelPairs()[0]


    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentAnalyzerBinding.inflate(inflater, container, false)
        adapter = WifiChannelAdapter(requireContext(), this, this)
        initView()
        mainWifi?.startScan()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeScanResults()
        observePermissionChange()
        observeWifiBand()
        observeWifiEnabled()
    }


    private fun rcvWifiInit() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rcvWifi.layoutManager = linearLayoutManager
        binding.rcvWifi.adapter = adapter
    }

    private fun observePermissionChange() {
        viewModel.mPermissionGranted.observe(viewLifecycleOwner) { _isPermissionGranted ->
            if (_isPermissionGranted) {
                hideRequestPermissionLayout()
            } else {
                showRequestPermissionLayout()
            }
        }
    }

    private fun observeWifiEnabled() {
        viewModel.mWifiEnabled.observe(viewLifecycleOwner) { isWifiEnabled ->
            if (isWifiEnabled) {
                hideRequestWifiEnable()
            } else {
                showRequestWifiEnable()
            }
        }
    }

    private fun observeWifiBand() {
        val navButton = listOf(
            binding.graphNavigationSet1,
            binding.graphNavigationSet2,
            binding.graphNavigationSet3
        )


        viewModel.wiFiBand.observe(viewLifecycleOwner) {
            val adapterAction = listOf(
                { adapter.setData(wiFiData.copy(), 5320, 4900) },
                { adapter.setData(wiFiData.copy(), 5720, 5500) },
                { adapter.setData(wiFiData.copy(), 5885, 5745) }
            )
            if (::wiFiData.isInitialized) {
                when (it) {
                    WiFiBand.GHZ5 -> {
                        binding.graphNavigation.visibility = View.VISIBLE
                        binding.tvInfo.text = getString(R.string.GHZ5)
                        wiFiChannelPair = it.wiFiChannels.wiFiChannelPairs()[0]
                        channelGraphAdapter = ChannelGraphAdapter(
                            requireContext(),
                            it,
                            wiFiChannelPair
                        )
                        adapter.setData(wiFiData.copy(), 5320, 4900)
                        navButton[0].isSelected = true
                        navButton[0].setTextColor(getColor(R.color.gray_900))
                        updateView()
                        navButton.forEachIndexed { index, item ->
                            kotlin.run {
                                item.setOnClickListener { _ ->
                                    item.isSelected = true
                                    item.setTextColor(getColor(R.color.gray_900))
                                    adapterAction[index]()
                                    navButton.forEach { item1 ->
                                        if (item1 != item) {
                                            item1.isSelected = false
                                            item1.setTextColor(getColor(R.color.gray_100))
                                        }
                                    }

                                    wiFiChannelPair = it.wiFiChannels.wiFiChannelPairs()[index]
                                    channelGraphAdapter = ChannelGraphAdapter(
                                        requireContext(),
                                        it,
                                        wiFiChannelPair
                                    )
                                    updateView()
                                }
                            }
                        }
                    }
                    WiFiBand.GHZ2 -> {
                        binding.tvInfo.text = getString(R.string.GHZ2)
                        wiFiChannelPair = it.wiFiChannels.wiFiChannelPairs()[0]
                        adapter.setData(wiFiData.copy(), 2499, 2400)
                        channelGraphAdapter = ChannelGraphAdapter(
                            requireContext(),
                            it,
                            wiFiChannelPair
                        )
                        updateView()
                        binding.graphNavigation.visibility = View.GONE
                        navButton.forEach { item ->
                            item.clickWithDebounce {
                            }
                        }
                    }
                }
            }

        }
    }

    private fun observeScanResults() {
        viewModel.mDataCache.observe(viewLifecycleOwner) {
            if (it.first.isEmpty()) return@observe
            val cache = Cache()
            cache.add(it.first, it.second)
            val transformer = Transformer(cache)
            wiFiData = transformer.transformToWiFiData()
            if (viewModel.wiFiBand.value == WiFiBand.GHZ2) {
                adapter.setData(wiFiData.copy(), 2499, 2400)
            } else if (viewModel.wiFiBand.value == WiFiBand.GHZ5 && wiFiChannelPair == WiFiBand.GHZ5.wiFiChannels.wiFiChannelPairs()[0]) {
                adapter.setData(wiFiData.copy(), 5320, 4900)
            } else if (viewModel.wiFiBand.value == WiFiBand.GHZ5 && wiFiChannelPair == WiFiBand.GHZ5.wiFiChannels.wiFiChannelPairs()[1]) {
                adapter.setData(wiFiData.copy(), 5720, 5500)
            } else if (viewModel.wiFiBand.value == WiFiBand.GHZ5 && wiFiChannelPair == WiFiBand.GHZ5.wiFiChannels.wiFiChannelPairs()[2]) {
                adapter.setData(wiFiData.copy(), 5885, 5745)
            }
            channelGraphAdapter = ChannelGraphAdapter(
                requireContext(),
                viewModel.wiFiBand.value!!,
                wiFiChannelPair
            )
            updateView()
            hideLoadingMain()
        }


    }


    private fun updateView() {
        binding.graph.removeAllViews()
        channelGraphAdapter.update(wiFiData.copy())
        binding.graph.addView(channelGraphAdapter.graphViews())
    }

    fun initView() {
        showLoadingMain()
        initPopupWindow()
        mainWifi =
            requireContext().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        rcvWifiInit()

        binding.btnPermission.clickWithDebounce {
            val intent =
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri =
                Uri.fromParts("package", requireActivity().packageName, null)
            intent.data = uri
            requireActivity().startActivity(intent)
        }
        binding.btnSetting.clickWithDebounce {
            val intent =
                Intent(Settings.ACTION_WIFI_SETTINGS)
            startActivity(intent)
        }
        binding.tvInfo.setOnClickListener {
            popupWindow.showAsDropDown(it, 0, -100)
        }

    }

    private fun initPopupWindow() {
        val inflater: LayoutInflater =
            (requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?)!!
        val bindingLayout = LayoutChangeFrequencyBinding.inflate(inflater, null, false)

        val width = if (buildMinVersionR()) {
            val windowMetrics: WindowMetrics = requireActivity().windowManager.currentWindowMetrics
            val insets: Insets = windowMetrics.windowInsets
                .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            windowMetrics.bounds.width() - insets.left - insets.right

        } else {
            val displayMetrics = DisplayMetrics()
            requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.widthPixels
        }
        popupWindow =
            PopupWindow(bindingLayout.root, width, LinearLayout.LayoutParams.WRAP_CONTENT, true)
        bindingLayout.root.clickWithDebounce {
            popupWindow.dismiss()
        }
        bindingLayout.tv2GHZ.clickWithDebounce {
            viewModel.wiFiBand.value = WiFiBand.GHZ2
            popupWindow.dismiss()
        }
        bindingLayout.tv5GHZ.clickWithDebounce {
            viewModel.wiFiBand.value = WiFiBand.GHZ5
            popupWindow.dismiss()
        }

    }

    private fun showLoadingMain() {
        binding.loadingPanel.visibility = View.VISIBLE
    }

    private fun hideLoadingMain() {
        binding.loadingPanel.visibility = View.GONE
    }

    private fun showRequestPermissionLayout() {
        binding.requestPermissionContainer.visibility = View.VISIBLE
    }

    private fun hideRequestPermissionLayout() {
        binding.requestPermissionContainer.visibility = View.GONE
    }

    private fun showRequestWifiEnable() {
        binding.requestWifiContainer.visibility = View.VISIBLE
    }

    private fun hideRequestWifiEnable() {
        binding.requestWifiContainer.visibility = View.GONE
    }


    override fun onClickItemWifi(wiFiDetail: WiFiDetail?, released: Boolean) {
        if (released) {
            wiFiData.wiFiDetails.map {
                it.released = true
                it
            }

        } else {
            wiFiData.wiFiDetails.map {
                it.selected = false
                it.released = false
                it
            }.find {
                it == wiFiDetail
            }?.selected = true
        }
        binding.graph.removeAllViews()
        channelGraphAdapter.update(wiFiData)
        binding.graph.addView(channelGraphAdapter.graphViews())
    }

    override fun onListChange(size: Int) {
        if (size == 0) {
            binding.containerEmpty.visibility = View.VISIBLE
            binding.rcvWifi.visibility = View.GONE
        }else {
            binding.containerEmpty.visibility = View.GONE
            binding.rcvWifi.visibility = View.VISIBLE
        }
    }

}
