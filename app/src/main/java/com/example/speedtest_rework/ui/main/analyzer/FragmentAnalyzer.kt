package com.example.speedtest_rework.ui.main.analyzer

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.speedtest_rework.base.fragment.BaseFragment
import com.example.speedtest_rework.common.NetworkUtils
import com.example.speedtest_rework.databinding.FragmentAnalyzerBinding
import com.example.speedtest_rework.ui.main.analyzer.adapter.ItemTouchHelper
import com.example.speedtest_rework.ui.main.analyzer.adapter.WifiChannelAdapter
import com.example.speedtest_rework.ui.main.analyzer.graph.ChannelGraphAdapter
import com.example.speedtest_rework.ui.main.analyzer.graph.ChannelGraphNavigation
import com.example.speedtest_rework.ui.main.analyzer.model.Transformer
import com.example.speedtest_rework.ui.main.analyzer.model.WifiModel
import com.example.speedtest_rework.viewmodel.SpeedTestViewModel
import com.vrem.wifianalyzer.wifi.scanner.Cache


class FragmentAnalyzer : BaseFragment(), ItemTouchHelper {
    private lateinit var binding: FragmentAnalyzerBinding
    private val viewModel: SpeedTestViewModel by activityViewModels()
    private lateinit var adapter: WifiChannelAdapter
    private var mList = mutableListOf<WifiModel>()
    private var mainWifi: WifiManager? = null
    private var duplicated = false
    lateinit var channelGraphAdapter: ChannelGraphAdapter


    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentAnalyzerBinding.inflate(inflater, container, false)
        adapter = WifiChannelAdapter(requireContext(),this)
        initView()
        observeScanResults()

        mainWifi?.startScan()
        return binding.root
    }


    private fun rcvWifiInit() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rcvWifi.layoutManager = linearLayoutManager
        binding.rcvWifi.adapter = adapter
    }

    fun initView() {
        showLoadingMain()
        mainWifi =
            requireContext().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        rcvWifiInit()

        binding.btnPermission.setOnClickListener {
            val intent =
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri =
                Uri.fromParts("package", requireActivity().packageName, null)
            intent.data = uri
            requireActivity().startActivity(intent)
        }
        binding.btnSetting.setOnClickListener {
            val intent =
                Intent(Settings.ACTION_WIFI_SETTINGS)
            startActivity(intent)
        }
        viewModel._isPermissionGranted.observe(viewLifecycleOwner) { _isPermissionGranted ->
            if (_isPermissionGranted) {
                hideRequestPermissionLayout()
            } else {
                showRequestPermissionLayout()
            }
        }
        viewModel._isWifiEnabled.observe(viewLifecycleOwner) { isWifiEnabled ->
            if (isWifiEnabled) {
                hideRequestWifiEnable()
            } else {
                showRequestWifiEnable()
            }
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

    private fun observeScanResults() {
        mList = mutableListOf()
        viewModel.mDataCache.observe(viewLifecycleOwner) {
            if (it != null) {
                for (result in it.first) {
                    val level = result.level
                    val frequency = result.frequency
                    var channelWidth = 0
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        channelWidth = result.channelWidth
                    }
                    val channel: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        ScanResult.convertFrequencyMhzToChannelIfSupported(result.frequency)
                    } else {
                        NetworkUtils.convertFreqtoChannel(result.frequency)
                    }
                    val rRange: Int =
                        NetworkUtils.getRangeWifi(channel, channelWidth)[0]
                    val lRange: Int =
                        NetworkUtils.getRangeWifi(channel, channelWidth)[1]
                    val name = result.SSID.ifEmpty { "wifi" }
                    var secure = ""
                    if (result.capabilities.contains("WPA2")) {
                        secure += "WPA2 "
                    }
                    if (result.capabilities.contains("WPA")) {
                        secure = "WPA "
                    }
                    if (result.capabilities.contains("WPS")) {
                        secure = "WPS "
                    }
                    if (NetworkUtils.getNameWifi(context) != null && NetworkUtils.getNameWifi(
                            context
                        ) == result.SSID
                    ) {
                        if (!duplicated) {
                            Log.d("TAG", "onReceive: $result")
                            mList.add(
                                0,
                                WifiModel(
                                    name,
                                    secure,
                                    level,
                                    frequency,
                                    channel,
                                    true,
                                    result.BSSID,
                                    rRange,
                                    lRange
                                )
                            )
                            duplicated = true
                        }
                    } else {
                        mList.add(
                            WifiModel(
                                name,
                                secure,
                                level,
                                frequency,
                                channel,
                                false,
                                result.BSSID,
                                rRange,
                                lRange
                            )
                        )

                    }
                }
                adapter.setData(mList)
                hideLoadingMain()
            }
            val cache = Cache()
            cache.add(it.first,it.second)
            val transformer = Transformer(cache)
            val wifiData = transformer.transformToWiFiData()
            Log.d("TAG", "observeScanResults: ")
            val linearLayout: LinearLayout = binding.graphNavigation
            val channelGraphNavigation = ChannelGraphNavigation(linearLayout,requireActivity().applicationContext)
            channelGraphAdapter = ChannelGraphAdapter(requireContext(),channelGraphNavigation)
            channelGraphAdapter.update(wifiData)
            binding.graph.addView(channelGraphAdapter.graphViews())

            hideLoadingMain()
        }


    }


    override fun onClickItemWifi(wifi: WifiModel?) {
    }

    private fun setDataChart(wifiList: List<WifiModel>) {}

}
