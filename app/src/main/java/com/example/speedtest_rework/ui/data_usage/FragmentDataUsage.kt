package com.example.speedtest_rework.ui.data_usage

import android.content.Context
import android.graphics.Insets
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.speedtest_rework.R
import com.example.speedtest_rework.base.fragment.BaseFragment
import com.example.speedtest_rework.common.utils.buildMinVersionR
import com.example.speedtest_rework.databinding.FragmentDataUsageBinding
import com.example.speedtest_rework.databinding.LayoutMenuFilterBinding
import com.example.speedtest_rework.ui.data_usage.adapter.DataUsageAdapter
import com.example.speedtest_rework.ui.data_usage.model.DataUsageModel
import com.example.speedtest_rework.viewmodel.Order
import com.example.speedtest_rework.viewmodel.SpeedTestViewModel
import java.math.RoundingMode

class FragmentDataUsage : BaseFragment() {
    private lateinit var binding: FragmentDataUsageBinding
    private lateinit var adapter: DataUsageAdapter
    private val viewModel: SpeedTestViewModel by activityViewModels()
    private lateinit var popupWindow: PopupWindow

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDataUsageBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        observeListDataUsage()
        changeBackPressCallBack()
        super.onViewCreated(view, savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun initView() {
        setPopupMenu()
        showLoading()
        val linearLayoutManager = LinearLayoutManager(requireContext())
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rcvDataUsage.layoutManager = linearLayoutManager
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.tvFilter.setOnClickListener {
            popupWindow.showAsDropDown(binding.containerHeader, 20, 0);
        }

    }

    private fun showLoading() {
        binding.containerLoading.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.containerLoading.visibility = View.GONE

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun observeListDataUsage() {
        viewModel.getListOfDataUsage().observe(viewLifecycleOwner) {
            if (it.size >= 0) {
                adapter = DataUsageAdapter(it)
                countTotal(it)
                hideLoading()
            }
        }
    }

    private fun countTotal(mList: List<DataUsageModel>) {
        var totalMobile = 0.0
        var totalWifi = 0.0
        var totalAll = 0.0
        mList.forEach { item ->
            totalMobile += item.mobile_usage
            totalWifi += item.wifi_usage
            totalAll += item.total
        }
        binding.rcvDataUsage.adapter = adapter
        binding.tvTotalMobile.text = convertData(totalMobile)
        binding.tvTotalWifi.text = convertData(totalWifi)
        binding.totalAll.text = convertData(totalAll)
    }

    private fun convertData(value: Double): String {
        return when {
            value <= 0 -> "0 MB"
            value < 1024 -> "${round(value)} B"
            value < 1024 * 1024 -> "${round(value / 1024)} KB"
            value < 1024 * 1024 * 1024 -> "${round(value / (1024 * 1024))} MB"
            else -> "${round(value / (1024 * 1024 * 1024))} GB"
        }
    }

    private fun setPopupMenu() {

        val inflater: LayoutInflater =
            (requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?)!!
        val bindingLayout = LayoutMenuFilterBinding.inflate(inflater, null, false)
        var width = 0
        width = if (buildMinVersionR()) {
            val windowMetrics: WindowMetrics = requireActivity().windowManager.currentWindowMetrics
            val insets: Insets = windowMetrics.windowInsets
                .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            windowMetrics.bounds.width() - insets.left - insets.right

        } else {
            val displayMetrics = DisplayMetrics()
            requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.widthPixels
        }
        popupWindow =
            PopupWindow(bindingLayout.root, width, LinearLayout.LayoutParams.WRAP_CONTENT, true)
        bindingLayout.root.setOnClickListener {
            popupWindow.dismiss()
        }
        bindingLayout.thirtyDay.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                adapter.setData(viewModel.reOrderListDataUsage(Order.THIRTY_DAYS)!!)
                binding.tvFilter.text = getString(R.string.thirty_day)
                countTotal(viewModel.reOrderListDataUsage(Order.THIRTY_DAYS)!!)
                popupWindow.dismiss()
            }
        }
        bindingLayout.sevenDay.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                adapter.setData(viewModel.reOrderListDataUsage(Order.SEVEN_DAYS)!!)
                binding.tvFilter.text = getString(R.string.seven_day)
                countTotal(viewModel.reOrderListDataUsage(Order.SEVEN_DAYS)!!)

                popupWindow.dismiss()
            }
        }
        bindingLayout.threeDay.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                adapter.setData(viewModel.reOrderListDataUsage(Order.THREE_DAYS)!!)
                binding.tvFilter.text = getString(R.string.three_day)
                countTotal(viewModel.reOrderListDataUsage(Order.THREE_DAYS)!!)

                popupWindow.dismiss()
            }
        }
        bindingLayout.today.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                adapter.setData(viewModel.reOrderListDataUsage(Order.TODAY)!!)
                binding.tvFilter.text = getString(R.string.today)
                countTotal(viewModel.reOrderListDataUsage(Order.TODAY)!!)
                popupWindow.dismiss()
            }
        }


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

    private fun round(value: Double): Double {
        return value.toBigDecimal().setScale(1, RoundingMode.UP).toDouble()
    }

}