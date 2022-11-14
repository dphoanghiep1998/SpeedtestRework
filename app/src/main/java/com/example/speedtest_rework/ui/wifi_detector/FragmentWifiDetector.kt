package com.example.speedtest_rework.ui.wifi_detector

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.speedtest_rework.base.fragment.BaseFragment
import com.example.speedtest_rework.databinding.FragmentWifiDetectorBinding
import com.example.speedtest_rework.ui.wifi_detector.adapter.WifiDetectorAdapter
import com.example.speedtest_rework.ui.wifi_detector.interfaces.ItemDeviceHelper
import com.example.speedtest_rework.ui.wifi_detector.model.DeviceModel
import com.stealthcopter.networktools.SubnetDevices
import com.stealthcopter.networktools.subnet.Device


class FragmentWifiDetector : BaseFragment(), ItemDeviceHelper {
    private lateinit var binding: FragmentWifiDetectorBinding
    private lateinit var adapter: WifiDetectorAdapter
    private var list: MutableList<DeviceModel> = mutableListOf()
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
    }


    private fun initView() {
        initAnimation()
        initButton()
        initRecycleView()
        discoveryDevice()
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
        rotate.duration = 2000
        rotate.repeatCount = Animation.INFINITE
        rotate.interpolator = LinearInterpolator()
    }

    private fun initRecycleView() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rcvDevice.layoutManager = linearLayoutManager
        adapter = WifiDetectorAdapter(this)
        binding.rcvDevice.adapter = adapter
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
        list = mutableListOf()
        adapter.setData(list, false)
        binding.tvPlaceholder.visibility = View.GONE
        binding.tvNumbers.visibility = View.GONE
        binding.tvDetecting.visibility = View.VISIBLE
        binding.btnReload.startAnimation(rotate)
        binding.refreshLayout.isEnabled = false

        SubnetDevices.fromLocalAddress().findDevices(object : SubnetDevices.OnSubnetDeviceFound {
            override fun onDeviceFound(device: Device?) {
                list.add((DeviceModel("Unknown Device", device!!.ip)))
                requireActivity().runOnUiThread {
                    adapter.setData(list, false)
                }
            }

            override fun onFinished(devicesFound: ArrayList<Device>?) {
                requireActivity().runOnUiThread {
                    binding.tvNumbers.text = list.size.toString()
                    binding.tvPlaceholder.visibility = View.VISIBLE
                    binding.tvNumbers.visibility = View.VISIBLE
                    binding.tvDetecting.visibility = View.GONE
                    binding.refreshLayout.isEnabled = true
                    binding.btnReload.clearAnimation()
                    adapter.setData(true)
                }
            }

        })

    }


    override fun onClickFlag(item: DeviceModel) {
    }
}