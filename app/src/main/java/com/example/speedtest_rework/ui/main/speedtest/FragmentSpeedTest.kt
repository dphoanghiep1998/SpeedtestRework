package com.example.speedtest_rework.ui.main.speedtest

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.example.speedtest_rework.R
import com.example.speedtest_rework.base.fragment.BaseFragment
import com.example.speedtest_rework.common.custom_view.ConnectionType
import com.example.speedtest_rework.common.custom_view.SpeedView
import com.example.speedtest_rework.common.custom_view.UnitType
import com.example.speedtest_rework.common.utils.AppSharePreference.Companion.INSTANCE
import com.example.speedtest_rework.common.utils.Constant
import com.example.speedtest_rework.common.utils.NetworkUtils
import com.example.speedtest_rework.common.utils.clickWithDebounce
import com.example.speedtest_rework.core.getIP.AddressInfo
import com.example.speedtest_rework.core.getIP.CurrentNetworkInfo
import com.example.speedtest_rework.core.serverSelector.TestPoint
import com.example.speedtest_rework.data.model.HistoryModel
import com.example.speedtest_rework.databinding.FragmentSpeedTestBinding
import com.example.speedtest_rework.viewmodel.ScanStatus
import com.example.speedtest_rework.viewmodel.SpeedTestViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class FragmentSpeedTest : BaseFragment() {

    private lateinit var binding: FragmentSpeedTestBinding
    private val viewModel: SpeedTestViewModel by activityViewModels()
    private var testPoint: TestPoint? = null
    private var isExpanded: Boolean = false
    private var maxValue: Float = 100f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSpeedTestBinding.inflate(inflater, container, false)
        observeIsLoading()
        observerMultiTaskDone()
        observeScanStatus()
        observeWifiName()
        observeUnitType()
        handleWhenNoConnection()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }


    private fun initView() {
        initExpandView()
        initSpeedView()
    }

    private fun initSpeedView() {
        binding.clSpeedview.onCallBackListener(object : SpeedView.OnCallbackListener {
            override fun onEnd(historyModel: HistoryModel?) {
                if (historyModel != null) {
                    viewModel.insertNewHistoryAction(historyModel)
                    viewModel.setScanStatus(ScanStatus.DONE)
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(1000)
                        val bundle = Bundle()
                        bundle.putParcelable(Constant.KEY_TEST_MODEL, historyModel)
                        bundle.putBoolean(Constant.KEY_FROM_SPEED_TEST_FRAGMENT, true)
                        navigateToPage(R.id.action_fragmentMain_to_fragmentResultDetail, bundle)
                        viewModel.speedTestDone = true
                    }

                }
            }

            override fun onError() {
                viewModel.setScanStatus(ScanStatus.HARD_RESET)
            }

            override fun onStart() {
                if (viewModel.speedTestDone){
                    viewModel.setScanStatus(ScanStatus.SCANNING)
                    viewModel.speedTestDone = false
                }
            }

        })
    }


    private fun observeUnitType() {
        viewModel.unitType.observe(viewLifecycleOwner) {
            binding.clSpeedview.setUniType(it)
        }
    }

    private fun initExpandView() {
        binding.containerConfig.clickWithDebounce {
            val layoutParams = binding.containerExpandView.layoutParams
            if (!isExpanded) {
                YoYo.with(Techniques.SlideInLeft).duration(400L).onStart {
                    binding.containerExpandView.setBackgroundResource(R.drawable.background_gradient_config)
                    layoutParams.width = 0
                    binding.containerExpandView.layoutParams = layoutParams
                    binding.line2.visibility = View.VISIBLE
                    isExpanded = true
                }.playOn(binding.containerConfig2)

            } else {
                YoYo.with(Techniques.SlideInRight).duration(400L).onStart {
                    layoutParams.width = ConstraintLayout.LayoutParams.WRAP_CONTENT
                    binding.containerExpandView.setBackgroundResource(R.drawable.background_gradient_config_sizing)
                    binding.line2.visibility = View.GONE
                    binding.containerExpandView.layoutParams = layoutParams
                    isExpanded = false
                }.playOn(binding.containerConfig2)

            }
        }

        val groupUnit = listOf(binding.tvMbpsType, binding.tvMbsType, binding.tvKbsType)
        val groupValue = listOf(binding.smallValue, binding.mediumValue, binding.highestValue)
        //init
        binding.tvMbpsType.textValue.text = getString(R.string.Mbps)
        binding.tvMbsType.textValue.text = getString(R.string.Mbs)
        binding.tvKbsType.textValue.text = getString(R.string.Kbs)
        binding.smallValue.textValue.text = getString(R.string.val_100)
        binding.mediumValue.textValue.text = getString(R.string.val_500)
        binding.highestValue.textValue.text = getString(R.string.val_1000)

        //setUpFirstTime

        groupUnit.forEachIndexed { index, item ->
            run {
                if (item.textValue.text == getUnitTypeFromPref()) {
                    selectView(item.line)
                    setUnitType(UnitType.values()[index])
                    valueWhenUnitSelected(UnitType.values()[index])
                } else {
                    unSelectView(item.line)
                }
            }

        }
        groupValue.forEach { textView ->
            run {
                if (textView.textValue.text == getUnitValueFromPref()) {
                    selectView(textView.line)
                    setMaxValue(textView.textValue.text.toString())
                } else {
                    unSelectView(textView.line)
                }
            }

        }
        //select unit type and max value speed view
        groupUnit.forEachIndexed { index, item ->
            run {
                item.root.clickWithDebounce {
                    saveUnitTypeToPref(item.textValue.text.toString())
                    //highlight text type
                    selectView(item.line)
                    //highlight text value index 0
                    selectView(groupValue[0].line)
                    //change text value to type of unitText
                    valueWhenUnitSelected(UnitType.values()[index])
                    //change unitType
                    setUnitType(UnitType.values()[index])
                    //setMaxvalue index 0
                    setMaxValue(groupValue[0].textValue.text.toString())
                    saveUnitValueToPref(groupValue[0].textValue.text.toString())
                    //unselect other value
                    groupValue.filter { fItem ->
                        fItem != groupValue[0]
                    }.forEach { it1 ->
                        unSelectView(it1.line)
                    }
                    //unselect other type
                    groupUnit.filter { fItem ->
                        fItem != item
                    }.forEach { it1 ->
                        unSelectView(it1.line)
                    }
                }
            }

        }
        groupValue.forEachIndexed { index, item ->
            run {
                item.root.clickWithDebounce {
                    selectViewValue(item.line)
                    setMaxValue(groupValue[index].textValue.text.toString())
                    saveUnitValueToPref(item.textValue.text.toString())
                    groupValue.filter { fItem ->
                        fItem != item
                    }.forEach { it1 ->
                        unSelectView(it1.line)
                    }
                }
            }
        }


    }

    private fun getUnitTypeFromPref(): String {
        return INSTANCE.getUnitType(getString(R.string.Mbps))
    }

    private fun saveUnitTypeToPref(value: String) {
        INSTANCE.saveUnitType(value)
    }

    private fun getUnitValueFromPref(): String {
        return INSTANCE.getUnitValue(getString(R.string.val_100))
    }

    private fun saveUnitValueToPref(value: String) {
        INSTANCE.saveUnitValue(value)
    }

    private fun valueWhenUnitSelected(unit: UnitType) {
        when (unit) {
            UnitType.MBPS -> {
                binding.smallValue.textValue.text = getString(R.string.val_100)
                binding.mediumValue.textValue.text = getString(R.string.val_500)
                binding.highestValue.textValue.text = getString(R.string.val_1000)

            }

            UnitType.MBS -> {
                binding.smallValue.textValue.text = getString(R.string.val_10)
                binding.mediumValue.textValue.text = getString(R.string.val_50)
                binding.highestValue.textValue.text = getString(R.string.val_100)
            }
            UnitType.KBS -> {
                binding.smallValue.textValue.text = getString(R.string.val_5000)
                binding.mediumValue.textValue.text = getString(R.string.val_10000)
                binding.highestValue.textValue.text = getString(R.string.val_15000)
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
                if (viewModel.mScanStatus.value == ScanStatus.SCANNING) {
                    binding.clSpeedview.forceStop()
                    binding.clSpeedview.resetView()
                }
                return
            }
            when (viewModel.typeNetwork.value) {
                ConnectionType.WIFI -> {
                    val testModel = HistoryModel(
                        name_network = binding.tvWifiName.text.toString(),
                        isp = binding.tvIspName.text.toString(),
                        externalIP = viewModel.currentNetworkInfo.selfIspIp,
                        internalIP = NetworkUtils.wifiIpAddress()
                    )
                    binding.clSpeedview.setData(testPoint!!, ConnectionType.WIFI, testModel)

                }
                ConnectionType.MOBILE -> {
                    val testModel = HistoryModel(
                        network = "mobile",
                        name_network = binding.tvWifiName.text.toString(),
                        isp = binding.tvIspName.text.toString(),
                        externalIP = viewModel.currentNetworkInfo.selfIspIp,
                    )
                    binding.clSpeedview.setData(
                        testPoint!!, ConnectionType.MOBILE, testModel
                    )
                }
                else -> {
                    binding.tvWifiName.text = getString(R.string.no_connection)
                    binding.tvIspName.text = getString(R.string.no_connection_isp)
                    binding.clSpeedview.setData(ConnectionType.UNKNOWN)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleWhenNoConnection() {
        viewModel.typeNetwork.observe(viewLifecycleOwner) {
            if (viewModel.typeNetwork.value == ConnectionType.UNKNOWN) {
                binding.tvWifiName.text = getString(R.string.no_connection)
                binding.tvIspName.text = getString(R.string.no_connection_isp)
                binding.clSpeedview.setData(ConnectionType.UNKNOWN)
                if (viewModel.mScanStatus.value == ScanStatus.SCANNING) {
                    viewModel.setScanStatus(ScanStatus.HARD_RESET)
                    binding.clSpeedview.forceStop()
                }
            }
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
                server.name, "https://" + server.host, server.downloadUrl, "speedtest/upload", ""
            )

        }
    }

    private fun setIspName(network: CurrentNetworkInfo) {
        if (network.selfIsp != "") {
            binding.tvIspName.text = network.selfIsp
            binding.tvIspNameHidden.text = network.selfIsp
        } else {
            binding.tvIspNameHidden.text = getString(R.string.no_connection_isp)
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

    private fun observeWifiName() {
        viewModel.networkName.observe(viewLifecycleOwner) {
            binding.tvWifiName.text = it
            binding.tvWifiNameHidden.text = it
        }

    }

    private fun observeScanStatus() {
        viewModel.mScanStatus.observe(viewLifecycleOwner) {
            when (it) {
                ScanStatus.DONE -> {
                    binding.clSpeedview.onScanningDone()
                    YoYo.with(Techniques.SlideInLeft).duration(300L).onStart {
                        YoYo.with(Techniques.FadeOut).duration(100L).onEnd {
                            binding.inforHidden.visibility = View.GONE
                        }.playOn(binding.inforHidden)
                    }.playOn(binding.containerExpandView)
                }
                ScanStatus.SCANNING -> {
                    binding.clSpeedview.prepareViewSpeedTest()
                    YoYo.with(Techniques.SlideOutLeft).duration(300L).onEnd {
                        YoYo.with(Techniques.FadeIn).duration(100L).onStart {
                            binding.inforHidden.visibility = View.VISIBLE
                        }.playOn(binding.inforHidden)
                    }.playOn(binding.containerExpandView)
                }

                else -> {
                    binding.clSpeedview.forceStop()
                    binding.clSpeedview.resetView()
                    YoYo.with(Techniques.SlideInLeft).duration(300L).onStart {
                        YoYo.with(Techniques.FadeOut).duration(100L).onEnd {
                            binding.inforHidden.visibility = View.GONE

                        }.playOn(binding.inforHidden)
                    }.playOn(binding.containerExpandView)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            binding.clSpeedview.forceStop()
        } catch (e: java.lang.Exception) {

        }
    }

}