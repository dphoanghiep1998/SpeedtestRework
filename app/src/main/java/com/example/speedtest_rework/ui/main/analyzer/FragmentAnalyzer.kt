package com.example.speedtest_rework.ui.main.analyzer

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.speedtest_rework.base.fragment.BaseFragment
import com.example.speedtest_rework.databinding.FragmentAnalyzerBinding
import com.example.speedtest_rework.ui.main.analyzer.adapter.ItemTouchHelper
import com.example.speedtest_rework.ui.main.analyzer.adapter.WifiChannelAdapter
import com.example.speedtest_rework.ui.main.analyzer.graph.ChannelGraphAdapter
import com.example.speedtest_rework.ui.main.analyzer.graph.ChannelGraphNavigation
import com.example.speedtest_rework.ui.main.analyzer.model.Transformer
import com.example.speedtest_rework.ui.main.analyzer.model.WiFiData
import com.example.speedtest_rework.ui.main.analyzer.model.WiFiDetail
import com.example.speedtest_rework.viewmodel.SpeedTestViewModel
import com.vrem.wifianalyzer.wifi.scanner.Cache


class FragmentAnalyzer : BaseFragment(), ItemTouchHelper {
    private lateinit var binding: FragmentAnalyzerBinding
    private val viewModel: SpeedTestViewModel by activityViewModels()
    private lateinit var adapter: WifiChannelAdapter
    private var mainWifi: WifiManager? = null
    private lateinit var channelGraphAdapter: ChannelGraphAdapter
    private lateinit var wiFiData: WiFiData


    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentAnalyzerBinding.inflate(inflater, container, false)
        adapter = WifiChannelAdapter(requireContext(), this)
        initView()
        mainWifi?.startScan()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeScanResults()
        observePermissionChange()
        observeWifiEnabled()
    }


    private fun rcvWifiInit() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rcvWifi.layoutManager = linearLayoutManager
        binding.rcvWifi.adapter = adapter
    }

    private fun observePermissionChange() {
        viewModel._isPermissionGranted.observe(viewLifecycleOwner) { _isPermissionGranted ->
            if (_isPermissionGranted) {
                hideRequestPermissionLayout()
            } else {
                showRequestPermissionLayout()
            }
        }
    }

    private fun observeWifiEnabled() {
        viewModel._isWifiEnabled.observe(viewLifecycleOwner) { isWifiEnabled ->
            if (isWifiEnabled) {
                hideRequestWifiEnable()
            } else {
                showRequestWifiEnable()
            }
        }
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
        viewModel.mDataCache.observe(viewLifecycleOwner) {
            binding.graph.removeAllViews()
            val cache = Cache()
            cache.add(it.first, it.second)
            val transformer = Transformer(cache)
            wiFiData = transformer.transformToWiFiData()
            adapter.setData(wiFiData)
            val linearLayout: LinearLayout = binding.graphNavigation
            val channelGraphNavigation =
                ChannelGraphNavigation(linearLayout, requireActivity().applicationContext)
            channelGraphAdapter = ChannelGraphAdapter(requireContext(), channelGraphNavigation)
            channelGraphAdapter.update(wiFiData)
            binding.graph.addView(channelGraphAdapter.graphViews())
            hideLoadingMain()
        }


    }

    override fun onClickItemWifi(wiFiDetail: WiFiDetail?, released: Boolean) {
        wiFiDetail?.wiFiSignal?.centerFrequency?.let {
            if (it > 2700) {
                return
            }
        }
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

}
