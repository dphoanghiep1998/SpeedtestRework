package com.example.speedtest_rework.activities

import android.Manifest
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.speedtest_rework.R
import com.example.speedtest_rework.common.Constant
import com.example.speedtest_rework.databinding.ActivityMainBinding
import com.example.speedtest_rework.receivers.ConnectivityListener
import com.example.speedtest_rework.viewmodel.SpeedTestViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var navHostFragment: NavHostFragment ?= null
    private val connectivityListener =  ConnectivityListener()

    val viewmodel : SpeedTestViewModel by viewModels()
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

    private fun initNavController(){
        navHostFragment = supportFragmentManager.findFragmentById(binding.navHostFragment.id) as NavHostFragment
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
    private fun registerConnectivityListener(){
        val filter = IntentFilter(Constant.INTENTFILER_CONNECTIVITYCHANGE)
        registerReceiver(connectivityListener,filter)
    }

    private fun unregisterConnectivityListener(){
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
               viewmodel.isPermissionGrandted.postValue(true)
            }
        }
    }
}