package com.example.speedtest_rework.ui.main.tools

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.example.speedtest_rework.CustomApplication
import com.example.speedtest_rework.R
import com.example.speedtest_rework.base.dialog.PermissionDialog
import com.example.speedtest_rework.base.fragment.BaseFragment
import com.example.speedtest_rework.common.extensions.InterAds
import com.example.speedtest_rework.common.extensions.NativeType
import com.example.speedtest_rework.common.extensions.showInterAds
import com.example.speedtest_rework.common.extensions.showNativeAds
import com.example.speedtest_rework.common.utils.buildMinVersionQ
import com.example.speedtest_rework.common.utils.clickWithDebounce
import com.example.speedtest_rework.databinding.FragmentToolsBinding
import com.example.speedtest_rework.services.AppForegroundService
import com.example.speedtest_rework.services.ServiceType

class FragmentTools : BaseFragment(), PermissionDialog.ConfirmCallback {
    private lateinit var binding: FragmentToolsBinding
    private lateinit var app: CustomApplication

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentToolsBinding.inflate(inflater, container, false)
        app = requireActivity().application as CustomApplication
        showAdsBottomNav()
        showNativeAds(binding.nativeAdMediumView,null,null,null,NativeType.TOOL)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        initButton()
    }

    private fun showAdsBottomNav() {
        if (!app.showAdsClickBottomNav) {
            app.showAdsClickBottomNav = true
            showInterAds(action = {
            }, InterAds.SWITCH_TAB)
        }
    }

    private fun initButton() {
        binding.containerDataUsage.clickWithDebounce {
            if (checkAccessSettingPermission(requireContext())) {
                navigateToPage(R.id.fragmentMain, R.id.action_fragmentMain_to_fragmentDataUsage)
            } else {
                val permissionDialog = PermissionDialog(requireContext(), this)
                permissionDialog.window?.attributes?.windowAnimations =
                    R.style.PermissionDialogAnimation
                permissionDialog.show()
            }
        }
        binding.containerSignalTest.clickWithDebounce {
            navigateToPage(R.id.fragmentMain, R.id.action_fragmentMain_to_fragmentSignalTest)
        }

        binding.containerWifiDetector.clickWithDebounce {
            navigateToPage(R.id.fragmentMain, R.id.action_fragmentMain_to_fragmentWifiDetector)
        }
        binding.containerPingTest.clickWithDebounce {
            navigateToPage(R.id.fragmentMain, R.id.action_fragmentMain_to_fragmentPingTest)
        }
        binding.containerWifiAnalyzer.clickWithDebounce {
            navigateToPage(R.id.fragmentMain, R.id.action_fragmentMain_to_fragmentAnalyzer)
        }
    }

    private fun checkAccessSettingPermission(context: Context): Boolean =
        try {
            val packageManager: PackageManager = context.packageManager
            val applicationInfo: ApplicationInfo =
                packageManager.getApplicationInfo(context.packageName, 0)
            val appOpsManager: AppOpsManager =
                context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            val mode: Int = if (buildMinVersionQ()) {
                appOpsManager.unsafeCheckOpNoThrow(
                    "android:get_usage_stats",
                    applicationInfo.uid,
                    applicationInfo.packageName
                )
            } else {
                appOpsManager.checkOpNoThrow(
                    "android:get_usage_stats",
                    applicationInfo.uid,
                    applicationInfo.packageName
                )
            }
            mode == AppOpsManager.MODE_ALLOWED
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }


    override fun negativeAction() {
        //do nothing
    }

    override fun positiveAction() {
        requestAccessSettingPermission(requireContext())
    }

    private val settingPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (checkAccessSettingPermission(requireContext())) {
                actionWhenPermissionGranted()
            }
        }

    private fun actionWhenPermissionGranted() {
        navigateToPage(R.id.fragmentMain, R.id.action_fragmentMain_to_fragmentDataUsage)
    }

    private fun requestAccessSettingPermission(context: Context) {
        try {
            val intent = Intent()
            intent.action = Settings.ACTION_USAGE_ACCESS_SETTINGS
            intent.data = Uri.parse("package:${context.packageName}")
            settingPermissionLauncher.launch(intent)
        } catch (e: Exception) {
            val intent = Intent()
            intent.action = Settings.ACTION_USAGE_ACCESS_SETTINGS
            settingPermissionLauncher.launch(intent)
        }

    }
}