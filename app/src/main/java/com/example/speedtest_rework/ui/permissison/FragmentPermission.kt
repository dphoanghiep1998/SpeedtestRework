package com.example.speedtest_rework.ui.permissison

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.example.speedtest_rework.R
import com.example.speedtest_rework.common.Constant
import com.example.speedtest_rework.databinding.FragmentPermissionBinding

class FragmentPermission : Fragment() {
    private lateinit var binding: FragmentPermissionBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPermissionBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }
    private fun initView() {
        binding.btnAccept.setOnClickListener {
            try {
                requestLocationPermission()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            Constant.REQUEST_CODE_LOCATION_PERMISSION
        )
    }


}