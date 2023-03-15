package com.example.speedtest_rework.ui.signal_test

import android.content.Context
import android.content.Intent
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.speedtest_rework.R
import com.example.speedtest_rework.base.dialog.SignalInfoDialog
import com.example.speedtest_rework.base.fragment.BaseFragment
import com.example.speedtest_rework.common.extensions.InterAds
import com.example.speedtest_rework.common.extensions.showBannerAds
import com.example.speedtest_rework.common.extensions.showInterAds
import com.example.speedtest_rework.common.utils.NetworkUtils
import com.example.speedtest_rework.common.utils.clickWithDebounce
import com.example.speedtest_rework.databinding.FragmentSignalTestBinding
import com.example.speedtest_rework.ui.signal_test.adapter.SignalLocationAdapter
import com.example.speedtest_rework.viewmodel.FragmentSignalTestViewModel
import com.example.speedtest_rework.viewmodel.SpeedTestViewModel
import com.gianghv.libads.InterstitialSingleReqAdManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class FragmentSignalTest : BaseFragment() {
    private lateinit var binding: FragmentSignalTestBinding
    private val viewModel: FragmentSignalTestViewModel by viewModels()
    private val shareViewModel: SpeedTestViewModel by activityViewModels()
    private lateinit var adapter: SignalLocationAdapter
    private var currentValue = 0
    private lateinit var job: Job

    var lastClickTime: Long = 0
    var handler = Handler()
    var runnable = Runnable {
        InterstitialSingleReqAdManager.isShowingAds = false
    }

    override fun onResume() {
        super.onResume()
        if (lastClickTime > 0) {
            handler.postDelayed(runnable, 1000)
        }
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignalTestBinding.inflate(inflater, container, false)
        showBannerAds(binding.bannerAds)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeSignalScanning()
        initView()
        changeBackPressCallBack()
        observeWifiName()
        observeWifiEnabled()
    }

    private fun initView() {
        binding.tvWifi.text = NetworkUtils.getNameWifi(requireContext())
        initButton()
        initRecycleView()
        observeListLocationSignal()
    }

    private fun observeWifiName() {
        shareViewModel.networkName.observe(viewLifecycleOwner) {
            binding.tvWifi.text = it
        }
    }

    private fun observeWifiEnabled() {
        shareViewModel.mWifiEnabled.observe(viewLifecycleOwner) {
            if (it) {
                binding.requestWifiContainer.visibility = View.GONE
            } else {
                binding.requestWifiContainer.visibility = View.VISIBLE
                if (::job.isInitialized) {
                    job.cancel()
                    currentValue = 0
                    binding.signalMeter.initSignalView()
                    viewModel.setSignalScanning(false)
                    viewModel.setListSignalLocation(mutableListOf())
                }
            }
        }
    }

    private fun observeListLocationSignal() {
        viewModel.mListSignalLocation.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.rcvLocation.visibility = View.GONE
                binding.containerDes.visibility = View.VISIBLE
            } else {
                binding.rcvLocation.visibility = View.VISIBLE
                binding.containerDes.visibility = View.GONE
            }
            adapter.setData(it)
        }
    }

    private fun initRecycleView() {
        adapter = SignalLocationAdapter()
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rcvLocation.layoutManager = linearLayoutManager
        binding.rcvLocation.adapter = adapter
    }

    private fun observeSignalScanning() {
        viewModel.signalScanning.observe(viewLifecycleOwner) {
            if (it) {
                binding.tvRecord.isEnabled = true
                binding.tvStart.setText(R.string.stop)
                binding.tvStart.clickWithDebounce {
                    if (::job.isInitialized) {
                        job.cancel()
                        currentValue = 0
                        binding.signalMeter.initSignalView()
                        viewModel.setSignalScanning(false)
                    }
                    showInterAds({}, InterAds.SIGNAL_TEST_STOP)

                }
            } else {
                binding.tvRecord.isEnabled = false
                binding.tvStart.setText(R.string.start)
                binding.tvStart.clickWithDebounce {
                    calculateWifi()
                    viewModel.setSignalScanning(true)

                }
            }
        }
    }

    private fun changeBackPressCallBack() {
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (SystemClock.elapsedRealtime() - lastClickTime < 30000 && InterstitialSingleReqAdManager.isShowingAds) return
                else showInterAds(action = {
                    cancelWhenDestroy()
                }, InterAds.TOOLS_FUNCTION_BACK)
                lastClickTime = SystemClock.elapsedRealtime()

            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun cancelWhenDestroy() {
        if (::job.isInitialized) {
            job.cancel()
            currentValue = 0
            binding.signalMeter.initSignalView()
            viewModel.setSignalScanning(false)
            viewModel.setListSignalLocation(mutableListOf())
        }
        findNavController().popBackStack()
    }

    private fun initButton() {
        binding.btnBack.clickWithDebounce {
            if (SystemClock.elapsedRealtime() - lastClickTime < 30000 && InterstitialSingleReqAdManager.isShowingAds) return@clickWithDebounce
            else showInterAds(action = {
                cancelWhenDestroy()
            }, InterAds.TOOLS_FUNCTION_BACK)
            lastClickTime = SystemClock.elapsedRealtime()
        }
        binding.btnSetting.clickWithDebounce {
            val intent =
                Intent(Settings.ACTION_WIFI_SETTINGS)
            startActivity(intent)
        }

        binding.tvStart.clickWithDebounce {
            calculateWifi()
        }
        binding.tvRecord.clickWithDebounce {
            val mList = viewModel.mListSignalLocation.value
            mList?.let {
                it.add(
                    (Pair(
                        "${getString(R.string.location)} ${adapter.itemCount + 1}",
                        "$currentValue dBm"
                    ))
                )
                viewModel.setListSignalLocation(it)
            }
        }
        binding.btnInfo.clickWithDebounce {
            val dialogSignalInfo = SignalInfoDialog(requireContext())
            dialogSignalInfo.show()
        }
    }

    private fun calculateWifi() {
        val wifiManager: WifiManager =
            requireContext().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        job = lifecycleScope.launch {
            while (isActive) {
                val wifiInfo: WifiInfo = wifiManager.connectionInfo
                currentValue = wifiInfo.rssi
                binding.signalMeter.setValue(wifiInfo.rssi.toFloat())
                delay(2000L)
            }
        }
    }

}