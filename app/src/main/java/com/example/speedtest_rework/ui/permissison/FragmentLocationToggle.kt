package com.example.speedtest_rework.ui.permissison

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import com.example.speedtest_rework.R
import com.example.speedtest_rework.base.fragment.BaseFragment
import com.example.speedtest_rework.common.utils.NetworkUtils
import com.example.speedtest_rework.common.utils.clickWithDebounce
import com.example.speedtest_rework.databinding.FragmentLocationToggleBinding
import com.example.speedtest_rework.viewmodel.SpeedTestViewModel
import com.gianghv.libads.InterstitialSingleReqAdManager

class FragmentLocationToggle : BaseFragment() {
    private lateinit var binding: FragmentLocationToggleBinding
    private val viewModel: SpeedTestViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentLocationToggleBinding.inflate(inflater, container, false)
        handlePermissionFlow()
        changeBackPressCallBack()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        binding.btnAccept.clickWithDebounce {
            try {
                handleLocationService()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun handlePermissionFlow() {
        if (NetworkUtils.canGetLocation(requireContext())) {
            navigateToPage(R.id.fragmentLocation, R.id.fragmentPermission)
        }
    }


    private fun handleLocationService() {
        if (!NetworkUtils.canGetLocation(requireContext())) {
            InterstitialSingleReqAdManager.isShowingAds = true
            checkLocationLauncher.launch(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
    }


    private val checkLocationLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (NetworkUtils.canGetLocation(requireContext())) {
                navigateToPage(R.id.fragmentLocation, R.id.fragmentPermission)
            }
            Handler().postDelayed({
                InterstitialSingleReqAdManager.isShowingAds = false
            }, 1000)
        }

    private fun changeBackPressCallBack() {
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finishAffinity()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }



}