package com.example.speedtest_rework.ui.permissison

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.example.speedtest_rework.R
import com.example.speedtest_rework.base.fragment.BaseFragment
import com.example.speedtest_rework.common.utils.clickWithDebounce
import com.example.speedtest_rework.databinding.FragmentPermisisonRequestBinding
import com.example.speedtest_rework.viewmodel.SpeedTestViewModel
import com.gianghv.libads.InterstitialSingleReqAdManager

class FragmentPermission : BaseFragment() {
    private lateinit var binding: FragmentPermisisonRequestBinding
    private val viewModel: SpeedTestViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPermisisonRequestBinding.inflate(inflater, container, false)
        handlePermissionFlow()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(), arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )[0]
            )
        ) {
            binding.btnAccept.text = getString(R.string.open_setting_permission)
        }
        binding.btnAccept.clickWithDebounce {
            try {
                handleLocationService()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun handlePermissionFlow() {
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            viewModel.setIsPermissionGranted(true)
            navigateToPage(R.id.fragmentPermission, R.id.fragmentMain)
        }
    }


    private fun handleLocationService() {

        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(), arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )[0]
                )
            ) {
                InterstitialSingleReqAdManager.isShowingAds = true
                checkLocationPermissionSetting.launch(
                    Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", requireActivity().packageName, null)
                    )
                )
            } else {
                InterstitialSingleReqAdManager.isShowingAds = true
                checkLocationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                )
            }
        }

    }


    private val checkLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true && permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
                viewModel.setIsPermissionGranted(true)
                navigateToPage(R.id.fragmentPermission, R.id.fragmentMain)
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(), arrayOf(
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )[0]
                    )
                ) {
                    binding.btnAccept.text = getString(R.string.open_setting_permission)
                }
            }
            Handler().postDelayed({
                InterstitialSingleReqAdManager.isShowingAds = false
            }, 1000)
        }

    private val checkLocationPermissionSetting =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                viewModel.setIsPermissionGranted(true)
                navigateToPage(R.id.fragmentPermission, R.id.fragmentMain)
            }
            Handler().postDelayed({
                InterstitialSingleReqAdManager.isShowingAds = false
            }, 1000)
        }


}