package com.example.speedtest_rework.ui.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.example.speedtest_rework.R
import com.example.speedtest_rework.base.fragment.BaseFragment
import com.example.speedtest_rework.common.Constant
import com.example.speedtest_rework.databinding.FragmentMainBinding
import com.example.speedtest_rework.ui.main.analyzer.FragmentAnalyzer
import com.example.speedtest_rework.ui.main.result_history.FragmentResults
import com.example.speedtest_rework.ui.main.speedtest.FragmentSpeedTest
import com.example.speedtest_rework.ui.main.vpn.FragmentRoot
import com.example.speedtest_rework.ui.viewpager.ViewPagerAdapter
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
        initView()
        observeIsScanning()
        return binding.root
    }


    private fun initView() {
        initViewPager()
        initBottomNavigation()
        initDrawerAction()
        initButton()
    }

    private fun initButton() {
        binding.imvStop.setOnClickListener{
            viewModel.setIsScanning(false)
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
                R.id.vpn -> {
                    binding.viewPager.currentItem = 2
                    return@setOnItemSelectedListener true
                }
                R.id.history -> {
                    binding.viewPager.currentItem = 3
                    return@setOnItemSelectedListener true
                }
            }
            true
        }
    }

    private fun initDrawerAction() {
        binding.menu.setOnClickListener {
            with(binding) {
                drawerContainer.openDrawer(
                    GravityCompat.START,
                    true
                )
            }
        }
        binding.imvBack.setOnClickListener { binding.drawerContainer.close() }
        binding.containerFeedback.setOnClickListener { openLink("http://www.google.com") }
        binding.containerPolicy.setOnClickListener { openLink("http://www.facebook.com") }
        binding.containerShare.setOnClickListener { this.shareApp() }
        binding.containerRate.setOnClickListener { this.rateApp() }
    }

    private fun initViewPager() {
        val fragmentList = arrayListOf(
            FragmentSpeedTest(),
            FragmentAnalyzer(),
            FragmentRoot(),
            FragmentResults()
        )
        val adapter = ViewPagerAdapter(
            fragmentList, requireActivity().supportFragmentManager,
            lifecycle
        )
        binding.viewPager.adapter = adapter
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.navBottom.menu.getItem(position).isChecked = true
                showMenu()
                when (position) {
                    0 -> binding.tvTitle.text = getString(R.string.speedtest_title)
                    1 -> binding.tvTitle.text = getString(R.string.wifi_analyzer_title)
                    2 -> binding.tvTitle.text = getString(R.string.vpn_title)
                    3 -> binding.tvTitle.text = getString(R.string.results_title)
                    else -> binding.tvTitle.text = getString(R.string.speedtest_title)
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
        YoYo.with(Techniques.SlideOutDown).onEnd {
            binding.navBottom.visibility = View.GONE
        }.playOn(binding.navBottom)
    }

    private fun showBottomTabAfterScan() {
        binding.viewPager.isUserInputEnabled = true
        YoYo.with(Techniques.SlideInUp).onStart {
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
        viewModel._isScanning.observe(viewLifecycleOwner) {
            if (it) {
                hideBottomTabWhenScan()
                showStopBtn()
            }else {
                showBottomTabAfterScan()
                showVipBtn()
            }
        }
    }



}