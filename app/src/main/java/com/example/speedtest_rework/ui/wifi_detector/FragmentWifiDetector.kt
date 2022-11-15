package com.example.speedtest_rework.ui.wifi_detector

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.speedtest_rework.R
import com.example.speedtest_rework.base.fragment.BaseFragment
import com.example.speedtest_rework.databinding.FragmentWifiDetectorBinding
import com.example.speedtest_rework.ui.wifi_detector.adapter.WifiDetectorAdapter
import com.example.speedtest_rework.ui.wifi_detector.interfaces.ItemDeviceHelper
import com.example.speedtest_rework.ui.wifi_detector.model.DeviceModel
import com.example.speedtest_rework.viewmodel.SpeedTestViewModel


class FragmentWifiDetector : BaseFragment(), ItemDeviceHelper {
    private lateinit var binding: FragmentWifiDetectorBinding
    private lateinit var adapter: WifiDetectorAdapter
    private val viewModel: SpeedTestViewModel by activityViewModels()
    private lateinit var rotate: RotateAnimation
    private var handler: Handler = Handler(Looper.getMainLooper())
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
        observeWifiDeviceList()
        observeWifiDetectDone()
        initView()
    }


    private fun initView() {
        initAnimation()
        initButton()
        initRecycleView()
        handler.postDelayed({
            discoveryDevice()
        }, 2000)

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
        handler.removeCallbacksAndMessages(null);
    }


    @SuppressLint("MissingPermission")
    private fun initButton() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnReload.setOnClickListener {
            discoveryDevice()
        }
        binding.refreshLayout.setOnRefreshListener {
            discoveryDevice()
            binding.refreshLayout.isRefreshing = false
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