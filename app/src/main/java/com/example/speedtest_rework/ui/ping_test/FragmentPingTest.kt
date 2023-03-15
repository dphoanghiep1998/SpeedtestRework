package com.example.speedtest_rework.ui.ping_test

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.speedtest_rework.R
import com.example.speedtest_rework.base.fragment.BaseFragment
import com.example.speedtest_rework.common.custom_view.ConnectionType
import com.example.speedtest_rework.common.extensions.InterAds
import com.example.speedtest_rework.common.extensions.showBannerAds
import com.example.speedtest_rework.common.extensions.showInterAds
import com.example.speedtest_rework.common.utils.AppSharePreference
import com.example.speedtest_rework.common.utils.clickWithDebounce
import com.example.speedtest_rework.databinding.FragmentPingTestBinding
import com.example.speedtest_rework.ui.ping_test.adapter.PingTestAdapter
import com.example.speedtest_rework.ui.ping_test.advanced_ping_test.FragmentAdvancedPing
import com.example.speedtest_rework.ui.ping_test.interfaces.ItemHelper
import com.example.speedtest_rework.ui.ping_test.model.ContentPingTest
import com.example.speedtest_rework.ui.ping_test.model.ItemPingTest
import com.example.speedtest_rework.ui.ping_test.model.TitlePingTest
import com.example.speedtest_rework.viewmodel.FragmentPingTestViewModel
import com.example.speedtest_rework.viewmodel.ScanStatus
import com.example.speedtest_rework.viewmodel.SpeedTestViewModel
import com.gianghv.libads.InterstitialSingleReqAdManager


class FragmentPingTest : BaseFragment(), ItemHelper {
    private lateinit var binding: FragmentPingTestBinding
    private lateinit var adapter: PingTestAdapter
    private val viewModel: FragmentPingTestViewModel by viewModels()
    private val mainViewModel: SpeedTestViewModel by activityViewModels()
    private lateinit var data: List<ItemPingTest>
    private lateinit var rotate: RotateAnimation
    var lastClickTime: Long = 0
    var handler = Handler()
    var runnable = Runnable {
        InterstitialSingleReqAdManager.isShowingAds = false
    }

    override fun onResume() {
        super.onResume()
        if (lastClickTime > 0) {
            handler.postDelayed(runnable, 1000)
        }
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPingTestBinding.inflate(inflater,container,false)
        showBannerAds(binding.bannerAds)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        changeBackPressCallBack()
        observePingDone()
        observeConnectionType()
    }

    private fun observeConnectionType() {
        mainViewModel.typeNetwork.observe(viewLifecycleOwner) {
            if (it == ConnectionType.UNKNOWN) {
                binding.requestWifiContainer.visibility = View.VISIBLE
                binding.btnReload.visibility = View.GONE
            } else {
                binding.requestWifiContainer.visibility = View.GONE
                binding.btnReload.visibility = View.VISIBLE
            }
        }
    }

    private fun changeBackPressCallBack() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (SystemClock.elapsedRealtime() - lastClickTime < 30000 && InterstitialSingleReqAdManager.isShowingAds) return
                    else showInterAds(action = {
                        findNavController().popBackStack()
                    }, InterAds.TOOLS_FUNCTION_BACK)
                    lastClickTime = SystemClock.elapsedRealtime()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }


    private fun initView() {
        data = mutableListOf(
            TitlePingTest(
                getString(R.string.customized), getString(R.string.latency_ping_test)
            ),
            ContentPingTest(getString(R.string.advanced_ping), "https://www.google.com/", 0, false),
            TitlePingTest(
                getString(R.string.game), getString(R.string.latency_ping_test)
            ),
            ContentPingTest(getString(R.string.epic_game), "https://store.epicgames.com/"),
            ContentPingTest(getString(R.string.playstation), "https://www.playstation.com/"),
            ContentPingTest(getString(R.string.steam), "https://store.steampowered.com/"),
            ContentPingTest(getString(R.string.minecraft), "https://www.minecraft.net"),
            TitlePingTest(
                getString(R.string.video), getString(R.string.latency_ping_test)
            ),
            ContentPingTest(getString(R.string.youtube), "https://www.youtube.com"),
            ContentPingTest(getString(R.string.netflix), "https://www.netflix.com"),
            ContentPingTest(getString(R.string.tiktok), "https://www.tiktok.com"),
            TitlePingTest(
                getString(R.string.social), getString(R.string.latency_ping_test)
            ),
            ContentPingTest(getString(R.string.facebook), "https://www.facebook.com"),
            ContentPingTest(getString(R.string.twitter), "https://www.twitter.com"),
            ContentPingTest(getString(R.string.instagram), "https://www.instagram.com"),
            TitlePingTest(
                getString(R.string.others), getString(R.string.latency_ping_test)
            ),
            ContentPingTest(
                getString(R.string.router),
                AppSharePreference.INSTANCE.getSavedIpRouter("https://www.google.com")
            )

        )
        initAnimation()
        initRecycleView()
        initButton()
        if (mainViewModel.typeNetwork.value != ConnectionType.UNKNOWN) {
            startAction()
        }
    }

    private fun startAction() {
        binding.btnReload.startAnimation(rotate)
        viewModel.getPingResult(data)
    }

    private fun initButton() {
        binding.btnBack.clickWithDebounce {
            if (SystemClock.elapsedRealtime() - lastClickTime < 30000 && InterstitialSingleReqAdManager.isShowingAds) return@clickWithDebounce
            else showInterAds(action = {
                findNavController().popBackStack()
            }, InterAds.TOOLS_FUNCTION_BACK)
            lastClickTime = SystemClock.elapsedRealtime()
        }
        binding.btnReload.setOnClickListener {
            viewModel.getPingResult(data)
            it.startAnimation(rotate)
        }
        binding.btnSetting.clickWithDebounce {
            val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
            startActivity(intent)
        }
        binding.requestWifiContainer.setOnClickListener {  }

    }

    private fun initAnimation() {
        rotate = RotateAnimation(
            0f, 180f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
        )
        rotate.duration = 1000
        rotate.repeatCount = Animation.INFINITE
        rotate.interpolator = LinearInterpolator()
    }

    private fun initRecycleView() {
        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.rcvPingView.layoutManager = linearLayoutManager
        adapter = PingTestAdapter(requireContext(), this)
        binding.rcvPingView.adapter = adapter
        adapter.setData(data)

    }

    private fun observePingDone() {
        viewModel.pingListStatus.observe(viewLifecycleOwner) {
            if (it == ScanStatus.DONE) {
                adapter.setLoading(false)
                adapter.setData(data)
                binding.btnReload.clearAnimation()
            } else {
                adapter.setLoading(true)
            }
        }
    }

    override fun onClickItemPing(item: ContentPingTest) {
        val fragmentAdvancedPing = FragmentAdvancedPing(item)
        fragmentAdvancedPing.show(childFragmentManager, "")
    }

}