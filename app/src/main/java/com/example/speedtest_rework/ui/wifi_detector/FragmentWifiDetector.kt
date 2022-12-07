package com.example.speedtest_rework.ui.wifi_detector

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.speedtest_rework.R
import com.example.speedtest_rework.base.fragment.BaseFragment
import com.example.speedtest_rework.common.utils.NetworkUtils
import com.example.speedtest_rework.common.utils.clickWithDebounce
import com.example.speedtest_rework.databinding.FragmentWifiDetectorBinding
import com.example.speedtest_rework.ui.wifi_detector.adapter.WifiDetectorAdapter
import com.example.speedtest_rework.ui.wifi_detector.interfaces.ItemDeviceHelper
import com.example.speedtest_rework.ui.wifi_detector.model.DeviceModel
import com.example.speedtest_rework.viewmodel.FragmentWifiDetectViewModel
import com.example.speedtest_rework.viewmodel.SpeedTestViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentWifiDetector : BaseFragment(), ItemDeviceHelper {
    private lateinit var binding: FragmentWifiDetectorBinding
    private lateinit var adapter: WifiDetectorAdapter
    private val viewModel: FragmentWifiDetectViewModel by viewModels()
    private val shareViewModel: SpeedTestViewModel by activityViewModels()
    private lateinit var rotate: RotateAnimation
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWifiDetectorBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observeWifiEnabled()
        observeWifiDeviceList()
        observeWifiDetectDone()
        changeBackPressCallBack()
    }

    private fun observeWifiEnabled() {
        shareViewModel.mWifiEnabled.observe(viewLifecycleOwner) {
            if (it) {
                discoveryDevice()
                binding.requestWifiContainer.visibility = View.GONE
                binding.btnReload.visibility = View.VISIBLE
            } else {
                binding.requestWifiContainer.visibility = View.VISIBLE
                binding.btnReload.visibility = View.GONE

            }

        }
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

    private fun initView() {
        initAnimation()
        initButton()
        initRecycleView()
    }


    private fun initAnimation() {
        rotate = RotateAnimation(
            0f,
            180f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        rotate.duration = 1000
        rotate.repeatCount = Animation.INFINITE
        rotate.interpolator = LinearInterpolator()
    }

    private fun initRecycleView() {
        binding.tvPlaceholder.visibility = View.GONE
        binding.tvNumbers.visibility = View.GONE
        binding.tvDetecting.visibility = View.GONE
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rcvDevice.layoutManager = linearLayoutManager
        adapter = WifiDetectorAdapter(this)
        binding.rcvDevice.adapter = adapter
        adapter.setData(mutableListOf(), false)

    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.wifiDetect?.cancel()
    }


    @SuppressLint("MissingPermission")
    private fun initButton() {
        binding.btnBack.clickWithDebounce {
            findNavController().popBackStack()
        }
        binding.btnReload.clickWithDebounce {
            discoveryDevice()
        }
        binding.refreshLayout.setOnRefreshListener {
            discoveryDevice()
            binding.refreshLayout.isRefreshing = false
        }
        binding.btnSetting.clickWithDebounce {
            val intent =
                Intent(Settings.ACTION_WIFI_SETTINGS)
            startActivity(intent)
        }
    }


    private fun discoveryDevice() {
        onScanning()
        viewModel.getDeviceListWifi()

    }

    private fun onScanning() {
        adapter.setData(mutableListOf(), false)
        binding.tvPlaceholder.visibility = View.GONE
        binding.tvNumbers.visibility = View.GONE
        binding.tvDetecting.visibility = View.VISIBLE
        binding.btnReload.startAnimation(rotate)
        binding.refreshLayout.isEnabled = false
        binding.btnReload.isEnabled = false
        binding.imvWifiDetector.setImageDrawable(getDrawable(R.drawable.ic_wifi_detect_inactive))
    }

    private fun onScanFinished() {
        binding.imvWifiDetector.setImageDrawable(getDrawable(R.drawable.ic_wifi_detector))
        binding.tvPlaceholder.visibility = View.VISIBLE
        binding.tvNumbers.visibility = View.VISIBLE
        binding.tvDetecting.visibility = View.GONE
        binding.refreshLayout.isEnabled = true
        binding.btnReload.clearAnimation()
        binding.btnReload.isEnabled = true
        adapter.setData(true)

    }

    private fun observeWifiDeviceList() {
        viewModel.listDevice.observe(viewLifecycleOwner) {
            adapter.setData(it, false)
            binding.tvNumbers.text = it.size.toString()

        }
    }

    private fun observeWifiDetectDone() {
        viewModel.isWifiDetectorDone.observe(viewLifecycleOwner) {
            if (it) {
                onScanFinished()
            }
        }
    }

    override fun onClickFlag(item: DeviceModel) {

    }
}