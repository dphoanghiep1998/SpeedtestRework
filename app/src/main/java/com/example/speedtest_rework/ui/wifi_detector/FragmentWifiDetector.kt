package com.example.speedtest_rework.ui.wifi_detector

import android.annotation.SuppressLint
import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.speedtest_rework.base.fragment.BaseFragment
import com.example.speedtest_rework.databinding.FragmentWifiDetectorBinding
import com.example.speedtest_rework.ui.wifi_detector.adapter.WifiDetectorAdapter
import com.example.speedtest_rework.ui.wifi_detector.detector.DeviceDetector
import com.example.speedtest_rework.ui.wifi_detector.interfaces.ItemDeviceHelper
import com.example.speedtest_rework.ui.wifi_detector.model.DeviceModel
import java.io.IOException
import java.net.ServerSocket


class FragmentWifiDetector : BaseFragment(), ItemDeviceHelper {
    private lateinit var binding: FragmentWifiDetectorBinding
    private lateinit var adapter: WifiDetectorAdapter

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

        initButton()
        initRecycleView()
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
            scanDevice(requireContext())
        }
    }

    private fun scanDevice(context: Context) {
        val nsdManager = (context.getSystemService(Context.NSD_SERVICE) as NsdManager)
        nsdManager.discoverServices(
            "_services._dns-sd._udp",
            NsdManager.PROTOCOL_DNS_SD,
            object : NsdManager.DiscoveryListener {
                override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
                    Log.e("TAG", "onStartDiscoveryFailed: $errorCode")
                }

                override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
                    Log.e("TAG", "onStopDiscoveryFailed: $errorCode")
                }

                override fun onDiscoveryStarted(serviceType: String) {
                    Log.d("TAG", "onDiscoveryStarted: "+ serviceType)
                }

                override fun onDiscoveryStopped(serviceType: String) {
                    Log.d("TAG", "onDiscoveryStopped: ")
                }

                override fun onServiceFound(serviceInfo: NsdServiceInfo) {
                    Log.d(
                        "TAG", """
                        onServiceFound: ${serviceInfo.serviceName}
                                    type:	${serviceInfo.serviceType}
                                                                    """.trimIndent()
                    )
                    if (serviceInfo.serviceName.contains("Dhome")) {
                        Log.d("TAG", "onServiceFound1: " + serviceInfo.serviceName)
                        nsdManager.resolveService(serviceInfo, object : NsdManager.ResolveListener {
                            override fun onResolveFailed(
                                serviceInfo: NsdServiceInfo,
                                errorCode: Int
                            ) {
                            }

                            override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
                                Log.d(
                                    "TAG",
                                    "onServiceResolved: " + serviceInfo.host + ":" + serviceInfo.port
                                )

                            }
                        })
                    }
                }

                override fun onServiceLost(serviceInfo: NsdServiceInfo) {}
            })
    }



    override fun onClickFlag(item: DeviceModel) {
    }
}