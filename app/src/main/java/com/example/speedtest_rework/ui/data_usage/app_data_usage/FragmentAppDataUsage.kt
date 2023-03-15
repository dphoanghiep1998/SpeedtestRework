package com.example.speedtest_rework.ui.data_usage.app_data_usage

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.speedtest_rework.R
import com.example.speedtest_rework.base.fragment.BaseFragment
import com.example.speedtest_rework.common.extensions.showBannerAds
import com.example.speedtest_rework.common.utils.clickWithDebounce
import com.example.speedtest_rework.data.model.UsagePackageModel
import com.example.speedtest_rework.databinding.FragmentAppDataUsageBinding
import com.example.speedtest_rework.ui.data_usage.app_data_usage.adapter.AppDataUsageAdapter
import com.example.speedtest_rework.ui.data_usage.model.DataUsageModel
import com.example.speedtest_rework.viewmodel.SpeedTestViewModel
import java.math.RoundingMode

class FragmentAppDataUsage : DialogFragment() {
    private lateinit var binding: FragmentAppDataUsageBinding
    private lateinit var adapter: AppDataUsageAdapter
    private val viewModel: SpeedTestViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val root = ConstraintLayout(requireContext())
        root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        )
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(root)
        dialog.window!!.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    requireContext(), R.color.background_main
                )
            )
        )
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        )

        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAppDataUsageBinding.inflate(inflater, container, false)
        showBannerAds(binding.bannerAds)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        changeBackPressCallBack()
        observeListDataUsage()
    }

    private fun initView() {
        showLoading()
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rcvDataUsage.layoutManager = linearLayoutManager
        adapter = AppDataUsageAdapter()
        binding.rcvDataUsage.adapter = adapter
        binding.btnBack.clickWithDebounce {
            findNavController().popBackStack()
        }

        viewModel.getListAppDataUsage()
    }

    private fun observeListDataUsage() {
        viewModel.getListAppDataUsage().observe(viewLifecycleOwner) {
            adapter.setData(it)
            hideLoading()

        }
    }

    private fun showLoading() {
        binding.containerLoading.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.containerLoading.visibility = View.GONE
    }

    private fun changeBackPressCallBack() {
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

}