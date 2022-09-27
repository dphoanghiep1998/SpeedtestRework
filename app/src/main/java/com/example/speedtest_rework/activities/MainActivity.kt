package com.example.speedtest_rework.activities

import android.Manifest
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle

import androidx.activity.viewModels
import androidx.core.content.ContextCompat

import androidx.navigation.fragment.NavHostFragment
import com.example.speedtest_rework.R
import com.example.speedtest_rework.base.activity.BaseActivity
import com.example.speedtest_rework.common.Constant
import com.example.speedtest_rework.databinding.ActivityMainBinding
import com.example.speedtest_rework.receivers.ConnectivityListener
import com.example.speedtest_rework.viewmodel.SpeedTestViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    private var navHostFragment: NavHostFragment? = null
    val viewModel: SpeedTestViewModel by viewModels()
    private lateinit var connectivityListener: ConnectivityListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        registerConnectivityListener()
        initNavController()
        handlePermissionFlow()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterConnectivityListener()
    }

    private fun initNavController() {
        navHostFragment =
            supportFragmentManager.findFragmentById(binding.navHostFragment.id) as NavHostFragment
    }

    private fun handlePermissionFlow() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            navHostFragment?.navController?.navigate(R.id.action_fragmentSplash_to_fragmentPermission)
        } else {
            navHostFragment?.navController?.navigate(R.id.action_fragmentSplash_to_fragmentMain)
        }
    }

    private fun registerConnectivityListener() {
        connectivityListener = ConnectivityListener(applicationContext)
        val filter = IntentFilter()
        filter.addAction(Constant.INTENT_FILER_SCAN_RESULT)
        filter.addAction(Constant.INTENT_FILER_CONNECTIVITYCHANGE)
        viewModel.addIsConnectivityChangedSource(connectivityListener.isConnectivityChanged)
        viewModel.addScanResultsSource(connectivityListener.scanResults)
        registerReceiver(connectivityListener, filter)
    }

    private fun unregisterConnectivityListener() {
        viewModel.removeIsConnectivityChangedSource(connectivityListener.isConnectivityChanged)
        viewModel.removeScanResultsSource(connectivityListener.scanResults)
        unregisterReceiver(connectivityListener)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constant.REQUEST_CODE_LOCATION_PERMISSION) {
            if (grantResults.size > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                navHostFragment?.navController?.navigate(R.id.action_fragmentPermission_to_fragmentMain)
                viewModel.setIsPermissionGranted(true)


            }
        }
    }


}