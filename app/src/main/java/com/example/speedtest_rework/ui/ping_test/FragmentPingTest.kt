package com.example.speedtest_rework.ui.ping_test

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.speedtest_rework.R
import com.example.speedtest_rework.base.fragment.BaseFragment
import com.example.speedtest_rework.databinding.FragmentPingTestBinding
import com.example.speedtest_rework.ui.ping_test.adapter.PingTestAdapter
import com.example.speedtest_rework.ui.ping_test.model.ContentPingTest
import com.example.speedtest_rework.ui.ping_test.model.TitlePingTest
import com.example.speedtest_rework.viewmodel.SpeedTestViewModel


class FragmentPingTest : BaseFragment() {
    private lateinit var binding: FragmentPingTestBinding
    private lateinit var adapter: PingTestAdapter
    private val viewModel: SpeedTestViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPingTestBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        getData()
    }


    private fun initView() {
        initRecycleView()
        initButton()
    }

    private fun initButton() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initRecycleView() {
        val data = mutableListOf(
            TitlePingTest(
                getString(R.string.customized),
                getString(R.string.latency_ping_test)
            ),
            ContentPingTest(getString(R.string.advanced_ping), 0, false),
            TitlePingTest(
                getString(R.string.game),
                getString(R.string.latency_ping_test)
            ),
            ContentPingTest(getString(R.string.epic_game)),
            ContentPingTest(getString(R.string.playstation)),
            ContentPingTest(getString(R.string.steam)),
            ContentPingTest(getString(R.string.minecraft)),
            TitlePingTest(
                getString(R.string.video),
                getString(R.string.latency_ping_test)
            ),
            ContentPingTest(getString(R.string.youtube)),
            ContentPingTest(getString(R.string.netflix)),
            ContentPingTest(getString(R.string.tiktok)),
            TitlePingTest(
                getString(R.string.social),
                getString(R.string.latency_ping_test)
            ),
            ContentPingTest(getString(R.string.facebook)),
            ContentPingTest(getString(R.string.twitter)),
            ContentPingTest(getString(R.string.instagram)),
            TitlePingTest(
                getString(R.string.others),
                getString(R.string.latency_ping_test)
            ),
            ContentPingTest(getString(R.string.router))

        )
        val linearLayoutManager = LinearLayoutManager(requireContext())
        binding.rcvPingView.layoutManager = linearLayoutManager
        adapter = PingTestAdapter()
        binding.rcvPingView.adapter = adapter
        adapter.setData(data)
    }

    private fun getData() {
        viewModel.result.observe(viewLifecycleOwner) {
            Log.d("TAG1245", "getData: " + it)
        }
        binding.btnReload.setOnClickListener {

                viewModel.getPingResult("google.com")

        }
    }

}