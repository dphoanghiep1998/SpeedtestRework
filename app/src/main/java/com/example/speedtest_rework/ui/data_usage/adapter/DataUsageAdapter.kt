package com.example.speedtest_rework.ui.data_usage.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.speedtest_rework.common.DateTimeUtils
import com.example.speedtest_rework.databinding.ItemDataUsageBinding
import com.example.speedtest_rework.ui.data_usage.model.DataUsageModel
import java.math.RoundingMode

class DataUsageAdapter(private var data: List<DataUsageModel>) :
    RecyclerView.Adapter<DataUsageAdapter.DataUsageViewHolder>() {
    fun setData(data: List<DataUsageModel>) {
        this.data = data
        notifyDataSetChanged()
    }

    inner class DataUsageViewHolder(val binding: ItemDataUsageBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataUsageViewHolder {
        val binding =
            ItemDataUsageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DataUsageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DataUsageViewHolder, position: Int) {
        with(holder) {
            with(data[position]) {
                binding.tvDate.text = DateTimeUtils.convertToDateMonth(this.day)
                binding.tvMobileUsage.text = convertData(this.mobile_usage)
                binding.tvWifiUsage.text = convertData(this.wifi_usage)
                binding.tvTotal.text = convertData(this.total)
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
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

    private fun round(value: Double): Double {
        return value.toBigDecimal().setScale(1, RoundingMode.UP).toDouble()
    }
}