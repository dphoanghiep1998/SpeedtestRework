package com.example.speedtest_rework.ui.main.speedtest

import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import com.example.speedtest_rework.R
import com.example.speedtest_rework.base.fragment.BaseFragment
import com.example.speedtest_rework.common.Constant
import com.example.speedtest_rework.common.NetworkUtils
import com.example.speedtest_rework.core.getIP.AddressInfo
import com.example.speedtest_rework.core.getIP.CurrentNetworkInfo
import com.example.speedtest_rework.core.serverSelector.TestPoint
import com.example.speedtest_rework.data.model.HistoryModel
import com.example.speedtest_rework.databinding.FragmentSpeedTestBinding
import com.example.speedtest_rework.viewmodel.SpeedTestViewModel
import kotlinx.coroutines.isActive
import java.util.*


class FragmentSpeedTest : BaseFragment() {

    private lateinit var binding: FragmentSpeedTestBinding
    private val viewModel: SpeedTestViewModel by activityViewModels()
    private var testPoint: TestPoint? = null
    private var isExpanded: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSpeedTestBinding.inflate(inflater, container, false)
        observeIsLoading()
        observeConnectivityChanged()
        observerMultiTaskDone()
        observeIsScanning()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeBackStackArg()
        initView()
        initData()
    }


    private fun initView() {
        initExpandView()
    }

    private fun initData() {
        loadServer()
    }

    private fun loadServer() {
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
            if (viewModel.isError.value == true) {
                binding.clSpeedview.setData(testPoint!!, "no_connection")
                binding.clSpeedview.resetView()
                return
            }
            if (NetworkUtils.isWifiConnected(requireContext())) {
                val testModel = HistoryModel(
                    -1,
                    binding.tvWifiName.text.toString(),
                    NetworkUtils.wifiIpAddress(requireContext()),
                    viewModel.currentNetworkInfo.selfIspIp,
                    viewModel.currentNetworkInfo.selfIsp,
                    0.0, 0.0, "wifi", 0.0, Date(), 0.0, 0.0
                )
                binding.clSpeedview.setData(testPoint!!, "wifi", testModel, viewModel)

            } else if (NetworkUtils.isMobileConnected(requireContext())) {
                val testModel = HistoryModel(
                    -1,
                    binding.tvWifiName.text.toString(),
                    "0.0.0.0",
                    viewModel.currentNetworkInfo.selfIspIp,
                    viewModel.currentNetworkInfo.selfIsp,
                    0.0, 0.0, "mobile", 0.0, Date(), 0.0, 0.0
                )
                binding.clSpeedview.setData(testPoint!!, "mobile", testModel, viewModel)

            } else {
                binding.clSpeedview.setData(testPoint!!, "no_connection")

            }

        } catch (e: Exception) {
        }
    }

    private fun observeIsLoading() {
        viewModel.isLoading.observe(viewLifecycleOwner) {
            if (it.peekContent()) {
                showLoadingPanel()
            } else {
                hideLoadingPanel()
            }
        }
    }


    private fun createTestPoint(addressInfo: List<AddressInfo>) {
        if (addressInfo.isNotEmpty()) {
            val server = addressInfo[0]
            testPoint = TestPoint(
                server.name,
                "http://" + server.host,
                "speedtest/",
                "speedtest/upload",
                ""
            )

        }
    }

    private fun setIspName(network: CurrentNetworkInfo) {
        if (network.selfIsp != "") {
            binding.tvIspName.text = network.selfIsp
        }
    }

    private fun observerMultiTaskDone() {

        viewModel.isMultiTaskDone.observe(viewLifecycleOwner) {
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
        viewModel._isConnectivityChanged.observe(viewLifecycleOwner) {
            onConnectivityChange()
        }
    }

    private fun onConnectivityChange() {
        if (NetworkUtils.isWifiConnected(requireContext())) {
            binding.tvWifiName.text = NetworkUtils.getNameWifi(requireContext())
            loadServer()
        } else if (NetworkUtils.isMobileConnected(requireContext())) {
            val info = NetworkUtils.getInforMobileConnected(requireContext())
            val name = if (info != null) info.typeName + " - " + info.subtypeName else "Mobile"
            binding.tvWifiName.text = name
            loadServer()
        } else {
            binding.tvWifiName.text = getString(R.string.no_connection)
            binding.tvIspName.text = getString(R.string.no_connection_isp)
            if (viewModel._isScanning.value == true) {
                viewModel.setIsScanning(false)
                binding.clSpeedview.setData("no_connection")
            }
        }
    }


    private fun observeBackStackArg() {
        val navHostFragment = activity?.supportFragmentManager?.findFragmentById(
            R.id.nav_host_fragment
        ) as NavHostFragment
        val navController = navHostFragment.navController
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>(Constant.KEY_SCAN_AGAIN)
            ?.observe(viewLifecycleOwner) {
                if (it) {
                    viewModel.setIsScanning(true)
                    binding.clSpeedview.prepareViewSpeedTest()
                }
            }
    }

    private fun observeIsScanning() {
        viewModel._isScanning.observe(viewLifecycleOwner) {
            if (!it) {
                binding.clSpeedview.resetView()
            }
        }
    }


}