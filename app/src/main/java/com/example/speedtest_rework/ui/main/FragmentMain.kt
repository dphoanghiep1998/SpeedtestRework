package com.example.speedtest_rework.ui.main

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.GravityCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.example.speedtest_rework.R
import com.example.speedtest_rework.base.dialog.PermissionDialog
import com.example.speedtest_rework.base.dialog.RateCallBack
import com.example.speedtest_rework.base.dialog.RateDialog
import com.example.speedtest_rework.base.fragment.BaseFragment
import com.example.speedtest_rework.common.utils.*
import com.example.speedtest_rework.common.utils.AppSharePreference.Companion.INSTANCE
import com.example.speedtest_rework.databinding.FragmentMainBinding
import com.example.speedtest_rework.services.AppForegroundService
import com.example.speedtest_rework.services.ServiceType
import com.example.speedtest_rework.ui.main.languages.FragmentLanguage
import com.example.speedtest_rework.ui.main.result_history.FragmentResults
import com.example.speedtest_rework.ui.main.speedtest.FragmentSpeedTest
import com.example.speedtest_rework.ui.main.tools.FragmentTools
import com.example.speedtest_rework.ui.viewpager.ViewPagerAdapter
import com.example.speedtest_rework.viewmodel.ScanStatus
import com.example.speedtest_rework.viewmodel.SpeedTestViewModel
import java.util.*
import kotlin.math.roundToInt

class FragmentMain : BaseFragment(), PermissionDialog.ConfirmCallback, RateCallBack,
    FragmentResults.OnStartClickedListener {
    private lateinit var binding: FragmentMainBinding
    private val viewModel: SpeedTestViewModel by activityViewModels()
    private lateinit var languageDialog: FragmentLanguage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notificationHandleIntentFlow()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(layoutInflater, container, false)
        changeBackPressCallBack()
        loadLanguage()
        initView()
        observeIsScanning()
        return binding.root
    }


    private fun initView() {

        initLanguageDialog()
        initViewPager()
        initBottomNavigation()
        initDrawerAction()
        initButton()
    }


    private fun initButton() {
        binding.imvStop.clickWithDebounce {
            toastShort(getString(R.string.scan_canceled))
            viewModel.setScanStatus(ScanStatus.HARD_RESET)
        }
    }

    private fun initBottomNavigation() {
        binding.navBottom.setOnItemSelectedListener { item ->
            showMenu()
            when (item.itemId) {
                R.id.speedtest -> {
                    binding.viewPager.currentItem = 0
                    return@setOnItemSelectedListener true
                }
                R.id.analist -> {
                    binding.viewPager.currentItem = 1
                    return@setOnItemSelectedListener true
                }
                R.id.history -> {
                    binding.viewPager.currentItem = 2
                    return@setOnItemSelectedListener true
                }
            }
            true
        }
    }

    private fun initDrawerAction() {
        binding.containerFeedback.root.clickWithDebounce { feedBack() }
        binding.containerPolicy.root.clickWithDebounce { openLink("http://www.facebook.com") }
        binding.containerShare.root.clickWithDebounce { shareApp() }
        binding.containerRate.root.clickWithDebounce { rateApp() }
        val saveServiceType =
            AppSharePreference.getInstance(requireContext()).getServiceType(ServiceType.NONE)
        if (buildMinVersionM()) {
            binding.swSwitchMonitor.setOnCheckedChangeListener { _, checked ->
                if (checked) {
                    binding.tvDesMonitor.visibility = View.VISIBLE
                    if (AppForegroundService.getInstance().isServiceSpeedMonitorRunning(
                            requireContext(), AppForegroundService::class.java
                        )
                    ) {
                        return@setOnCheckedChangeListener
                    }
                    AppForegroundService.getInstance()
                        .startService(requireContext(), ServiceType.SPEED_MONITOR)
                } else {
                    AppForegroundService.getInstance()
                        .stopService(requireContext(), ServiceType.SPEED_MONITOR)
                    binding.tvDesMonitor.visibility = View.GONE

                }
            }

            binding.swSwitchDataUsage.setOnCheckedChangeListener { item, checked ->
                if (checked) {
                    when (checkAccessSettingPermission(requireContext())) {
                        true -> {
                            item.isChecked = true
                            binding.tvDesDataUsage.visibility = View.VISIBLE

                            if (AppForegroundService.getInstance().isServiceDataUsageRunning(
                                    requireContext(), AppForegroundService::class.java
                                )
                            ) {
                                return@setOnCheckedChangeListener
                            }
                            AppForegroundService.getInstance().startService(
                                requireContext(), ServiceType.DATA_USAGE
                            )

                        }
                        else -> {
                            item.isChecked = false
                            val permissionDialog = PermissionDialog(requireContext(), this)
                            permissionDialog.window?.attributes?.windowAnimations =
                                R.style.PermissionDialogAnimation
                            permissionDialog.show()
                        }
                    }
                } else {
                    AppForegroundService.getInstance()
                        .stopService(requireContext(), ServiceType.DATA_USAGE)
                    binding.tvDesDataUsage.visibility = View.GONE

                }
            }
            if (saveServiceType == ServiceType.SPEED_MONITOR || saveServiceType == ServiceType.BOTH) {
                binding.swSwitchMonitor.isChecked = true
                binding.tvDesMonitor.visibility = View.VISIBLE
            }
            if (saveServiceType == ServiceType.DATA_USAGE || saveServiceType == ServiceType.BOTH) {
                binding.swSwitchDataUsage.isChecked = true
                binding.tvDesDataUsage.visibility = View.VISIBLE

            }
            binding.menu.clickWithDebounce {
                with(binding) {
                    drawerContainer.openDrawer(
                        GravityCompat.START, true
                    )
                }
            }
            binding.containerDataUsage.visibility = View.VISIBLE
            binding.containerSpeedMonitor.visibility = View.VISIBLE


        }
        binding.containerLanguage.root.setPadding(
            resources.getDimension(com.intuit.sdp.R.dimen._16sdp).roundToInt(),
            resources.getDimension(com.intuit.sdp.R.dimen._8sdp).roundToInt(),
            resources.getDimension(com.intuit.sdp.R.dimen._16sdp).roundToInt(),
            resources.getDimension(com.intuit.sdp.R.dimen._8sdp).roundToInt()
        )
        binding.containerLanguage.root.clickWithDebounce {
            findNavController().navigate(R.id.action_fragmentMain_to_languageFragment)
        }
    }

    private fun feedBack() {
        val deviceName = Build.MODEL // returns model name
        val deviceManufacturer = Build.MANUFACTURER

        val testIntent = Intent(Intent.ACTION_VIEW)
        val data: Uri = Uri.parse(
            """mailto:?subject=Feedback ${getString(R.string.app_name)}&body=Device: $deviceManufacturer - $deviceName Android SDK ${Build.VERSION.SDK_INT} &to=${Constant.MAIL_TO}"""
        )
        testIntent.data = data
        try {
            startActivity(testIntent)

        } catch (e: Exception) {
            e.printStackTrace()
            toastShort(getString(R.string.no_provider))
        }
    }

    private fun initLanguageDialog() {
        languageDialog = FragmentLanguage()
    }

    private fun checkAccessSettingPermission(context: Context): Boolean = try {
        val packageManager: PackageManager = context.packageManager
        val applicationInfo: ApplicationInfo =
            packageManager.getApplicationInfo(context.packageName, 0)
        val appOpsManager: AppOpsManager =
            context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode: Int = if (buildMinVersionQ()) {
            appOpsManager.unsafeCheckOpNoThrow(
                "android:get_usage_stats", applicationInfo.uid, applicationInfo.packageName
            )
        } else {
            appOpsManager.checkOpNoThrow(
                "android:get_usage_stats", applicationInfo.uid, applicationInfo.packageName
            )
        }
        mode == AppOpsManager.MODE_ALLOWED
    } catch (e: PackageManager.NameNotFoundException) {
        false
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

    private fun notificationHandleIntentFlow() {
        val actionShowDataUsage =
            requireActivity().intent.extras?.getString(getString(R.string.action_show_data_usage))

        if (actionShowDataUsage != null) {
            navigateToPage(R.id.action_fragmentMain_to_fragmentDataUsage)
        }
    }


    private fun initViewPager() {
        val fragmentList = arrayListOf(
            FragmentSpeedTest(), FragmentTools(), FragmentResults(this)
        )
        val adapter = ViewPagerAdapter(
            fragmentList, childFragmentManager, lifecycle
        )
        binding.viewPager.adapter = adapter

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.navBottom.menu.getItem(position).isChecked = true
                showMenu()
                when (position) {
                    0 -> {
                        binding.tvTitle.text = getString(R.string.speed_test_title)
                        binding.imvVip.visibility = View.VISIBLE
                    }
                    1 -> {
                        binding.tvTitle.text = getString(R.string.tools_title)
                        binding.imvVip.visibility = View.GONE
                    }
                    2 -> {
                        binding.tvTitle.text = getString(R.string.results_title)
                        binding.imvVip.visibility = View.VISIBLE
                    }
                    else -> {
                        binding.tvTitle.text = getString(R.string.speed_test_title)
                        binding.imvVip.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    private fun showMenu() {
        if (binding.menu.visibility == View.VISIBLE) {
            return
        }
        binding.tvTitle.text = getString(R.string.vpn_title)
        YoYo.with(Techniques.FadeOut).duration(400).onEnd {
            binding.backBtn.visibility = View.GONE
        }.playOn(binding.backBtn)
        YoYo.with(Techniques.FadeIn).duration(400).onEnd {
            binding.menu.visibility = View.VISIBLE
        }.playOn(binding.menu)
    }

    private fun openLink(strUri: String?) {
        try {
            val uri = Uri.parse(strUri)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun rateApp() {
        val rateDialog = RateDialog(requireContext(), this)
        rateDialog.show()

    }

    private fun shareApp() {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, Constant.INTENT_VALUE_SPEED_TEST)
            startActivity(Intent.createChooser(shareIntent, "Choose one"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun hideBottomTabWhenScan() {
        binding.viewPager.isUserInputEnabled = false
        YoYo.with(Techniques.SlideOutDown).duration(400L).onEnd {
            if (buildMaxVersionN()) {
                binding.navBottom.visibility = View.GONE
                return@onEnd
            }
            binding.navBottom.visibility = View.INVISIBLE
        }.playOn(binding.navBottom)
    }

    private fun showBottomTabAfterScan() {
        binding.viewPager.isUserInputEnabled = true
        YoYo.with(Techniques.SlideInUp).duration(400L).onStart {
            binding.navBottom.visibility = View.VISIBLE
        }.playOn(binding.navBottom)
    }

    private fun showStopBtn() {
        YoYo.with(Techniques.FadeOut).duration(100).onEnd {
            binding.imvVip.visibility = View.GONE
        }.playOn(binding.imvVip)
        YoYo.with(Techniques.FadeIn).duration(100).onEnd {
            binding.imvStop.visibility = View.VISIBLE
        }.playOn(binding.imvStop)
    }

    @Suppress("DEPRECATION")
    private fun loadLanguage() {
        val language = INSTANCE.getSavedLanguage(Locale.getDefault().language)
        val locale = findByLanguageTag(language)
        Locale.setDefault(locale)
        val resources: Resources = requireActivity().resources
        val config: Configuration = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        binding.containerLanguage.tvDes.text = locale.displayLanguage
        binding.containerLanguage.tvDes.visibility = View.VISIBLE
    }

    private fun showVipBtn() {
        YoYo.with(Techniques.FadeOut).duration(100).onEnd {
            binding.imvStop.visibility = View.GONE
        }.playOn(binding.imvStop)
        YoYo.with(Techniques.FadeIn).duration(100).onEnd {
            binding.imvVip.visibility = View.VISIBLE
        }.playOn(binding.imvVip)
    }

    private fun observeIsScanning() {
        viewModel.mScanStatus.observe(viewLifecycleOwner) {
            if (it == ScanStatus.SCANNING) {
                hideBottomTabWhenScan()
                showStopBtn()
            } else {
                showBottomTabAfterScan()
                showVipBtn()
            }
        }
    }


    private fun changeBackPressCallBack() {
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.drawerContainer.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerContainer.close()
                } else if (viewModel.mScanStatus.value == ScanStatus.SCANNING) {
                    toastShort(getString(R.string.scan_canceled))
                    viewModel.setScanStatus(ScanStatus.HARD_RESET)
                } else {
                    activity?.finish()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }


    private val settingPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (checkAccessSettingPermission(requireContext())) {
                actionWhenPermissionGranted()
            }
        }

    private fun actionWhenPermissionGranted() {
        binding.swSwitchDataUsage.isChecked = true
        binding.tvDesDataUsage.visibility = View.VISIBLE

        if (AppForegroundService.getInstance().isServiceDataUsageRunning(
                requireContext(), AppForegroundService::class.java
            )
        ) {
            return
        }
        AppForegroundService.getInstance().startService(
            requireContext(), ServiceType.DATA_USAGE
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.setScanStatus(ScanStatus.HARD_RESET)
    }

    override fun negativeAction() {
        //do nothing
    }

    override fun positiveAction() {
        requestAccessSettingPermission(requireContext())
    }

    override fun onClickRateUs(star: Int) {
        viewModel.userActionRate = true
        if (star < 4) {
            return
        }
        openLink("http://www.facebook.com")
    }

    override fun onStartClicked() {
        binding.viewPager.currentItem = 0
    }

}