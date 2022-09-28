package com.example.speedtest_rework.ui.main.speedtest

import android.os.Build
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.speedtest_rework.R
import com.example.speedtest_rework.base.fragment.BaseFragment
import com.example.speedtest_rework.common.NetworkUtils
import com.example.speedtest_rework.common.custom_view.SpeedView
import com.example.speedtest_rework.core.getIP.AddressInfo
import com.example.speedtest_rework.core.getIP.CurrentNetworkInfo
import com.example.speedtest_rework.core.serverSelector.TestPoint
import com.example.speedtest_rework.databinding.FragmentSpeedTestBinding
import com.example.speedtest_rework.viewmodel.SpeedTestViewModel
import java.util.*


class FragmentSpeedTest : BaseFragment() {

    private lateinit var binding: FragmentSpeedTestBinding
    private val viewModel: SpeedTestViewModel by activityViewModels()
    private var testPoint: TestPoint? = null
    private var isExpanded: Boolean = false
    private var speedView: SpeedView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSpeedTestBinding.inflate(inflater, container, false)
        observeIsLoading()
//        observeConnectivityChanged()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observerServerList()
        initView()
    }

    private fun initView() {
        initExpandView()
        viewModel.doMultiTask()
    }

    private fun initExpandView() {
        binding.containerExpandView.setOnClickListener { view ->
            TransitionManager.beginDelayedTransition(
                view as ViewGroup,
                AutoTransition()
            )
            val layoutParams = view.getLayoutParams()
            if (!isExpanded) {
                layoutParams.width = ConstraintLayout.LayoutParams.MATCH_PARENT
                view.setLayoutParams(layoutParams)
                isExpanded = true
            } else {
                layoutParams.width = ConstraintLayout.LayoutParams.WRAP_CONTENT
                view.setLayoutParams(layoutParams)
                isExpanded = false
            }
        }
    }


    private fun loadServerAndNetWorkInfo() {
        try {
            binding.clSpeedview.initData(testPoint!!, viewModel._isScanning.value ?: false)

        } catch (e: Exception) {
        }
    }

    private fun observeIsLoading() {
        viewModel.isLoading.observe(viewLifecycleOwner, Observer {
            if (it.peekContent()) {
                showLoadingPanel()
            } else {
                hideLoadingPanel()
            }
        })
    }


    private fun createTestPoint(addressInfo: List<AddressInfo>) {
        if (addressInfo.isNotEmpty()) {
            val server = addressInfo[0]
            testPoint = TestPoint(
                server.name,
                server.host,
                server.downloadUrl,
                server.uploadUrl,
                server.pingUrl
            )

        }
    }

    private fun setIspName(network: CurrentNetworkInfo) {
        if (network.selfIsp != "") {
            binding.tvIspName.text = network.selfIsp
        }
    }

    private fun observerServerList() {

        viewModel.isMultiTaskSuccess.observe(viewLifecycleOwner) {
            if (it) {
                createTestPoint(viewModel.addressInfoList)
                setIspName(viewModel.currentNetworkInfo)
                loadServerAndNetWorkInfo()
            }
        }
    }

    private fun showLoadingPanel() {
        binding.containerInforWifi.visibility = View.GONE
        binding.containerLoading.visibility = View.VISIBLE
    }

    private fun hideLoadingPanel() {
        binding.containerInforWifi.visibility = View.VISIBLE
        binding.containerLoading.visibility = View.GONE
    }


    private fun observeConnectivityChanged() {
        viewModel._isConnectivityChanged.observe(viewLifecycleOwner, Observer {
            onConnectivityChange()
        })
    }

    private fun onConnectivityChange() {
        Log.e("", "onConnectivityChange: ")
        if (NetworkUtils.isWifiConnected(requireContext())) {
            binding.tvWifiName.text = NetworkUtils.getNameWifi(requireContext())
        } else if (NetworkUtils.isMobileConnected(requireContext())) {
            val info = NetworkUtils.getInforMobileConnected(requireContext())
            val name = if (info != null) info?.typeName + " - " + info?.subtypeName else "Mobile"
            binding.tvWifiName.text = name
        } else {
            binding.tvWifiName.text = getString(R.string.no_connection)
            binding.tvIspName.text = getString(R.string.no_connection_isp)

        }
    }

    private fun observeIsScanning() {
        viewModel._isScanning.observe(viewLifecycleOwner, Observer {

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