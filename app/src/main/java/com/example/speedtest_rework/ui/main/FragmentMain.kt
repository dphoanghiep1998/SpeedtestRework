package com.example.speedtest_rework.ui.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.GravityCompat
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.example.speedtest_rework.R
import com.example.speedtest_rework.base.fragment.BaseFragment
import com.example.speedtest_rework.common.Constant
import com.example.speedtest_rework.databinding.FragmentMainBinding
import com.example.speedtest_rework.services.AppForegroundService
import com.example.speedtest_rework.ui.main.analyzer.FragmentAnalyzer
import com.example.speedtest_rework.ui.main.result_history.FragmentResults
import com.example.speedtest_rework.ui.main.speedtest.FragmentSpeedTest
import com.example.speedtest_rework.ui.viewpager.ViewPagerAdapter
import com.example.speedtest_rework.viewmodel.ScanStatus
import com.example.speedtest_rework.viewmodel.SpeedTestViewModel

class FragmentMain : BaseFragment() {
    private lateinit var binding: FragmentMainBinding
    private val viewModel: SpeedTestViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(layoutInflater, container, false)
        changeBackPressCallBack()
        initView()
        observeIsScanning()
        observeHardReset()
        return binding.root
    }


    private fun initView() {
        initViewPager()
        initBottomNavigation()
        initDrawerAction()
        initButton()
    }

    private fun initButton() {
        binding.imvStop.setOnClickListener {
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
        if (AppForegroundService.isServiceRunning(
                requireContext(),
                AppForegroundService::class.java
            )
        ) {
            binding.containerSpeedMonitor.swSwitch.isChecked = true
        }
        binding.menu.setOnClickListener {
            with(binding) {
                drawerContainer.openDrawer(
                    GravityCompat.START,
                    true
                )
            }
        }
        binding.containerFeedback.root.setOnClickListener { openLink("http://www.google.com") }
        binding.containerPolicy.root.setOnClickListener { openLink("http://www.facebook.com") }
        binding.containerShare.root.setOnClickListener { this.shareApp() }
        binding.containerRate.root.setOnClickListener { this.rateApp() }

        binding.containerSpeedMonitor.swSwitch.setOnCheckedChangeListener { item, checked ->
            if (checked) {
                AppForegroundService.startService(requireContext())
            } else {
                AppForegroundService.killService(requireContext())
            }
        }


    }


    private fun initViewPager() {
        val fragmentList = arrayListOf(
            FragmentSpeedTest(),
            FragmentAnalyzer(),
            FragmentResults()
        )
        val adapter = ViewPagerAdapter(
            fragmentList, requireActivity().supportFragmentManager,
            lifecycle
        )
        binding.viewPager.adapter = adapter

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                Log.d("TAG", "onPageSelected: $position ")
                super.onPageSelected(position)
                binding.navBottom.menu.getItem(position).isChecked = true
                showMenu()
                when (position) {
                    0 -> binding.tvTitle.text = getString(R.string.speed_test_title)
                    1 -> binding.tvTitle.text = getString(R.string.wifi_analyzer_title)
                    2 -> binding.tvTitle.text = getString(R.string.results_title)
                    else -> binding.tvTitle.text = getString(R.string.speed_test_title)
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

    }

    private fun shareApp() {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, Constant.INTENT_VALUE_SPEEDTEST)
            startActivity(Intent.createChooser(shareIntent, "Choose one"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun hideBottomTabWhenScan() {
        binding.viewPager.isUserInputEnabled = false
        YoYo.with(Techniques.SlideOutDown).duration(400L).onEnd {
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
        YoYo.with(Techniques.FadeOut).duration(400).onEnd {
            binding.imvVip.visibility = View.GONE
        }.playOn(binding.imvVip)
        YoYo.with(Techniques.FadeIn).duration(400).onEnd {
            binding.imvStop.visibility = View.VISIBLE
        }.playOn(binding.imvStop)
    }

    private fun showVipBtn() {
        YoYo.with(Techniques.FadeOut).duration(400).onEnd {
            binding.imvStop.visibility = View.GONE
        }.playOn(binding.imvStop)
        YoYo.with(Techniques.FadeIn).duration(400).onEnd {
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

    private fun observeHardReset() {
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
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (binding.drawerContainer.isDrawerOpen(GravityCompat.START)) {
                        binding.drawerContainer.close()
                    } else {
                        activity?.finish()
                    }
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }
}