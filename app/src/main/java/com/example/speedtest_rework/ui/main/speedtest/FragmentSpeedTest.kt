package com.example.speedtest_rework.ui.main.speedtest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.example.speedtest_rework.R
import com.example.speedtest_rework.base.fragment.BaseFragment
import com.example.speedtest_rework.common.Constant
import com.example.speedtest_rework.common.NetworkUtils
import com.example.speedtest_rework.common.custom_view.ConnectionType
import com.example.speedtest_rework.common.custom_view.UnitType
import com.example.speedtest_rework.core.getIP.AddressInfo
import com.example.speedtest_rework.core.getIP.CurrentNetworkInfo
import com.example.speedtest_rework.core.serverSelector.TestPoint
import com.example.speedtest_rework.data.model.HistoryModel
import com.example.speedtest_rework.databinding.FragmentSpeedTestBinding
import com.example.speedtest_rework.viewmodel.SpeedTestViewModel
import java.util.*


class FragmentSpeedTest : BaseFragment() {

    private lateinit var binding: FragmentSpeedTestBinding
    private val viewModel: SpeedTestViewModel by activityViewModels()
    private var testPoint: TestPoint? = null
    private var isExpanded: Boolean = false
    private var maxValue: Float = 100f

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
        binding.containerConfig.setOnClickListener {
            val layoutParams = binding.containerExpandView.layoutParams
            if (!isExpanded) {
                YoYo.with(Techniques.SlideInLeft).duration(400L).onStart {
                    binding.containerExpandView.setBackgroundResource(R.drawable.background_gradient_config)
                    layoutParams.width = ConstraintLayout.LayoutParams.MATCH_PARENT
                    binding.containerExpandView.layoutParams = layoutParams
                    isExpanded = true
                }.playOn(binding.containerConfig2)

            } else {
                YoYo.with(Techniques.SlideInRight).duration(400L).onStart {
                    layoutParams.width = ConstraintLayout.LayoutParams.WRAP_CONTENT
                    binding.containerExpandView.setBackgroundResource(R.drawable.background_gradient_config_sizing)

                    binding.containerExpandView.layoutParams = layoutParams
                    isExpanded = false
                }.playOn(binding.containerConfig2)

            }
        }


        val groupUnit = listOf(binding.tvMbpsType, binding.tvMbsType, binding.tvKbsType)
        val groupValue = listOf(binding.smallValue, binding.mediumValue, binding.highestValue)
        //init
        selectView(binding.tvMbpsType)
        selectView(binding.smallValue)
        setUnitType(UnitType.values()[0])
        setMaxValue(groupValue[0].text.toString())

        //select unit type and max value speed view
        groupUnit.forEachIndexed { index, item ->
            item.setOnClickListener {
                //highlight text type
                selectView(it)
                //highlight text value index 0
                selectView(groupValue[0])
                //change text value to type of unitText
                valueWhenUnitSelected(UnitType.values()[index])
                //change unitType
                setUnitType(UnitType.values()[index])
                //setMaxvalue index 0
                setMaxValue(groupValue[0].text.toString())
                //unselect other value
                groupValue.filter { fItem ->
                    fItem != groupValue[0]
                }.forEach { it1 ->
                    unSelectView(it1)
                }
                //unselect other type
                groupUnit.filter { fItem ->
                    fItem != item
                }.forEach { it1 ->
                    unSelectView(it1)
                }
            }
        }
        groupValue.forEachIndexed { index, item ->
            item.setOnClickListener {
                selectViewValue(it)
                setMaxValue(groupValue[index].text.toString())
                groupValue.filter { fItem ->
                    fItem != item
                }.forEach { it1 ->
                    unSelectView(it1)
                }
            }
        }

    }

    private fun valueWhenUnitSelected(unit: UnitType) {
        when (unit) {
            UnitType.MBPS -> {
                binding.smallValue.text = getString(R.string.val_100)
                binding.mediumValue.text = getString(R.string.val_500)
                binding.highestValue.text = getString(R.string.val_1000)

            }

            UnitType.MBS -> {
                binding.smallValue.text = getString(R.string.val_10)
                binding.mediumValue.text = getString(R.string.val_50)
                binding.highestValue.text = getString(R.string.val_100)
            }
            UnitType.KBS -> {
                binding.smallValue.text = getString(R.string.val_5000)
                binding.mediumValue.text = getString(R.string.val_10000)
                binding.highestValue.text = getString(R.string.val_15000)
            }
        }
    }

    private fun setMaxValue(text: String) {
        maxValue = text.toFloat()
        binding.clSpeedview.setData(maxValue)
    }

    private fun setUnitType(unitType: UnitType) {
        viewModel.unitType.value = unitType
    }

    private fun selectView(view: View) {
        view.setBackgroundResource(R.drawable.background_selected_unit)
    }

    private fun selectViewValue(view: View) {
        view.setBackgroundResource(R.drawable.background_selected_unit)

    }

    private fun unSelectView(view: View) {
        view.setBackgroundResource(0)
    }

    private fun loadServerAndNetWorkInfo() {
        try {
            if (viewModel.isError.value == true) {
                binding.clSpeedview.setData(testPoint!!, ConnectionType.UNKNOWN)
                if (viewModel._isScanning.value == true) {
                    binding.clSpeedview.resetView()
                }
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
                binding.clSpeedview.setData(testPoint!!, ConnectionType.WIFI, testModel, viewModel)

            } else if (NetworkUtils.isMobileConnected(requireContext())) {
                val testModel = HistoryModel(
                    -1,
                    binding.tvWifiName.text.toString(),
                    "0.0.0.0",
                    viewModel.currentNetworkInfo.selfIspIp,
                    viewModel.currentNetworkInfo.selfIsp,
                    0.0, 0.0, "mobile", 0.0, Date(), 0.0, 0.0
                )
                binding.clSpeedview.setData(
                    testPoint!!,
                    ConnectionType.MOBILE,
                    testModel,
                    viewModel
                )

            } else {
                binding.clSpeedview.setData(ConnectionType.UNKNOWN)

            }

        } catch (e: Exception) {
            e.printStackTrace()
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
            binding.tvIspNameHidden.text = network.selfIsp
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
            binding.tvWifiNameHidden.text = name
            loadServer()
        } else {
            binding.tvWifiName.text = getString(R.string.no_connection)
            binding.tvIspName.text = getString(R.string.no_connection_isp)
            binding.clSpeedview.setData(ConnectionType.UNKNOWN)
            if (viewModel._isScanning.value == true) {
                viewModel.setIsScanning(false)
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
                YoYo.with(Techniques.SlideInLeft).duration(300L).onStart {
                    YoYo.with(Techniques.FadeOut).duration(100L).onEnd {
                        binding.inforHidden.visibility = View.GONE

                    }.playOn(binding.inforHidden)
                }
                    .playOn(binding.containerExpandView)
            } else {
                YoYo.with(Techniques.SlideOutLeft).duration(300L).onEnd {
                    YoYo.with(Techniques.FadeIn).duration(100L).onStart {
                        binding.inforHidden.visibility = View.VISIBLE
                    }.playOn(binding.inforHidden)
                }
                    .playOn(binding.containerExpandView)
            }
        }
    }


}