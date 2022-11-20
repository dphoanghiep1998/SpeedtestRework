package com.example.speedtest_rework.ui.signal_test

import android.content.Context
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.speedtest_rework.R
import com.example.speedtest_rework.base.fragment.BaseFragment
import com.example.speedtest_rework.common.utils.NetworkUtils
import com.example.speedtest_rework.databinding.FragmentSignalTestBinding
import com.example.speedtest_rework.ui.signal_test.adapter.SignalLocationAdapter
import com.example.speedtest_rework.viewmodel.FragmentSignalTestViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class FragmentSignalTest : BaseFragment() {
    private lateinit var binding: FragmentSignalTestBinding
    private val viewModel: FragmentSignalTestViewModel by viewModels()
    private lateinit var adapter: SignalLocationAdapter
    private var currentValue = 0
    private lateinit var job: Job


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignalTestBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeSignalScanning()
        initView()
        changeBackPressCallBack()
    }

    private fun initView() {
        binding.tvWifi.text = NetworkUtils.getNameWifi(requireContext())
        initButton()
        initRecycleView()
        observeListLocationSignal()
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
                binding.tvStart.setOnClickListener {
                    if (::job.isInitialized) {
                        job.cancel()
                        currentValue = 0
                        binding.signalMeter.initSignalView()
                        viewModel.setSignalScanning(false)
                    }

                }
            } else {
                binding.tvRecord.isEnabled = false
                binding.tvStart.setText(R.string.start)
                binding.tvStart.setOnClickListener {
                    calculateWifi()
                    viewModel.setSignalScanning(true)

                }
            }
        }
    }

    private fun changeBackPressCallBack() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                    if (::job.isInitialized) {
                        job.cancel()
                        currentValue = 0
                        binding.signalMeter.initSignalView()
                        viewModel.setSignalScanning(false)
                        viewModel.setListSignalLocation(mutableListOf())
                    }
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun initButton() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
            if (::job.isInitialized) {
                job.cancel()
                currentValue = 0
                binding.signalMeter.initSignalView()
                viewModel.setSignalScanning(false)
                viewModel.setListSignalLocation(mutableListOf())
            }
        }

        binding.tvStart.setOnClickListener {
            calculateWifi()
        }
        binding.tvRecord.setOnClickListener {
            var mList = viewModel.mListSignalLocation.value
            mList?.let {
                it.add(
                    (Pair(
                        "Location ${adapter.itemCount + 1}",
                        "$currentValue dBm"
                    ))
                )
                viewModel.setListSignalLocation(it)
            }
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