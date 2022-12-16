package com.example.speedtest_rework.ui.ping_test

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.speedtest_rework.R
import com.example.speedtest_rework.base.fragment.BaseFragment
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


class FragmentPingTest : BaseFragment(), ItemHelper {
    private lateinit var binding: FragmentPingTestBinding
    private lateinit var adapter: PingTestAdapter
    private val viewModel: FragmentPingTestViewModel by viewModels()
    private lateinit var data: List<ItemPingTest>
    private lateinit var rotate: RotateAnimation


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPingTestBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        changeBackPressCallBack()
        observePingDone()

    }

    private fun changeBackPressCallBack() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }

    private fun initView() {
        data = mutableListOf(
            TitlePingTest(
                getString(R.string.customized),
                getString(R.string.latency_ping_test)
            ),
            ContentPingTest(getString(R.string.advanced_ping), "https://www.google.com/", 0, false),
            TitlePingTest(
                getString(R.string.game),
                getString(R.string.latency_ping_test)
            ),
            ContentPingTest(getString(R.string.epic_game), "https://store.epicgames.com/"),
            ContentPingTest(getString(R.string.playstation), "https://www.playstation.com/"),
            ContentPingTest(getString(R.string.steam), "https://store.steampowered.com/"),
            ContentPingTest(getString(R.string.minecraft), "https://www.minecraft.net"),
            TitlePingTest(
                getString(R.string.video),
                getString(R.string.latency_ping_test)
            ),
            ContentPingTest(getString(R.string.youtube), "https://www.youtube.com"),
            ContentPingTest(getString(R.string.netflix), "https://www.netflix.com"),
            ContentPingTest(getString(R.string.tiktok), "https://www.tiktok.com"),
            TitlePingTest(
                getString(R.string.social),
                getString(R.string.latency_ping_test)
            ),
            ContentPingTest(getString(R.string.facebook), "https://www.facebook.com"),
            ContentPingTest(getString(R.string.twitter), "https://www.twitter.com"),
            ContentPingTest(getString(R.string.instagram), "https://www.instagram.com"),
            TitlePingTest(
                getString(R.string.others),
                getString(R.string.latency_ping_test)
            ),
            ContentPingTest(
                getString(R.string.router),
                AppSharePreference.INSTANCE.getSavedIpRouter("https://www.google.com")
            )

        )
        initAnimation()
        initRecycleView()
        initButton()
        startAction()
    }

    private fun startAction(){
        binding.btnReload.startAnimation(rotate)
        viewModel.getPingResult(data)
    }

    private fun initButton() {
        binding.btnBack.clickWithDebounce {
            findNavController().popBackStack()
        }
        binding.btnReload.setOnClickListener {
            viewModel.getPingResult(data)
            it.startAnimation(rotate)
        }

    }

    private fun initAnimation() {
        rotate = RotateAnimation(
            0f,
            180f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
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
        viewModel.pingStatus.observe(viewLifecycleOwner) {
            if (it == ScanStatus.DONE) {
                adapter.setData(data)
                binding.btnReload.clearAnimation()
            } else {
                adapter.setData(data)
            }
        }
    }

    override fun onClickItemPing(item: ContentPingTest) {
        val fragmentAdvancedPing = FragmentAdvancedPing(item)
        fragmentAdvancedPing.show(childFragmentManager,"")
    }

}