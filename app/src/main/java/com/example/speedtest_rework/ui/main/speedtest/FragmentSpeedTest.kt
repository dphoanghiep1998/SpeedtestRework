package com.example.speedtest_rework.ui.main.speedtest

import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.speedtest_rework.base.fragment.BaseFragment
import com.example.speedtest_rework.common.NetworkUtils
import com.example.speedtest_rework.core.SpeedTest
import com.example.speedtest_rework.core.getIP.GetIP
import com.example.speedtest_rework.core.serverSelector.TestPoint
import com.example.speedtest_rework.databinding.FragmentSpeedTestBinding
import com.example.speedtest_rework.viewmodel.SpeedTestViewModel
import java.util.*


class FragmentSpeedTest : BaseFragment() {

    private lateinit var binding: FragmentSpeedTestBinding

    private val viewModel: SpeedTestViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSpeedTestBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    private fun loadServer() {
        Log.d("TAG", "loadServer: vao day")
        object : Thread() {
            override fun run() {
                try {
                    requireActivity().runOnUiThread(Runnable { showLoadingPanel() })
                    getIP = GetIP()
                    getIP.run()
                    getIP.join()
                    val mapKey: HashMap<Int, String> = getIP.getMapKey()
                    val mapValue: HashMap<Int, List<String>> = getIP.getMapValue()
                    val selfLat: Double = getIP.getSelfLat()
                    val selfLon: Double = getIP.getSelfLon()
                    var tmp = 19349458.0
                    var findServerIndex = 0
                    for (index in mapKey.keys) {
                        if (tempBlackList.contains(mapValue[index]!![5])) {
                            continue
                        }
                        val source = Location("Source")
                        source.latitude = selfLat
                        source.longitude = selfLon
                        val ls = mapValue[index]!!
                        val dest = Location("Dest")
                        dest.latitude = ls[0].toDouble()
                        dest.longitude = ls[1].toDouble()
                        val distance = source.distanceTo(dest).toDouble()
                        if (tmp > distance) {
                            tmp = distance
                            findServerIndex = index
                        }
                    }
                    val info = mapValue[findServerIndex]
                    if (info == null) {
                        requireActivity().runOnUiThread(Runnable {
                            binding.tvIspName.setText("No connection")
                            hideLoadingPanel()
                            isLoadedServer = false
                        })
                        return
                    } else {
                        testPoint = TestPoint(
                            info[3],
                            "http://" + info[6],
                            "speedtest/",
                            "speedtest/upload",
                            ""
                        )
                        requireActivity().runOnUiThread(Runnable {
                            if (type == "wifi") {
                                wifi.setWifi_external_ip(getIP.getSelfIspIp())
                                wifi.setWifi_ISP(getIP.getSelfIsp())
                            } else if (type == "mobile") {
                                mobile.setMobile_external_ip(getIP.getSelfIspIp())
                                mobile.setMobile_isp(getIP.getSelfIsp())
                            }
                            binding.tvIspName.setText(getIP.getSelfIsp())
                            hideLoadingPanel()
                        })
                        isLoadedServer = true
                    }
                } catch (e: Exception) {
                    Log.d("TAG", "run: to Exception ")
                    e.printStackTrace()
                }
            }
        }.start()
    }

    private fun showLoadingPanel() {
        binding.containerInforWifi.visibility = View.GONE
        binding.containerLoading.visibility = View.VISIBLE
    }

    private fun hideLoadingPanel() {
        binding.containerInforWifi.visibility = View.VISIBLE
        binding.containerLoading.visibility = View.GONE
    }


    private fun ObserveConnectivityChanged() {
        viewModel._isConnectivityChanged.observe(this, Observer {
            if (it) {
                NetworkUtils.isWifiConnected(requireContext())
            }
        })
    }

    private fun ObserveIsScanning() {
        viewModel._isScanning.observe(this, Observer {

        })
    }


    private fun format(d: Double): String? {
        val l: Locale
        l = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            resources.configuration.locales[0]
        } else {
            resources.configuration.locale
        }
        return if (d < 200) String.format(l, "%.2f", d) else "" + Math.round(d)
    }


}