package com.example.speedtest_rework.ui.data_usage.app_data_usage.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.speedtest_rework.data.model.UsagePackageModel
import com.example.speedtest_rework.databinding.ItemAppDataUsageBinding
import java.math.RoundingMode

class AppDataUsageAdapter : RecyclerView.Adapter<AppDataUsageAdapter.AppDataUsageViewHolder>() {
    private var mList = mutableListOf<UsagePackageModel>()
    fun setData(list: List<UsagePackageModel>) {
        mList = list.toMutableList()
        notifyDataSetChanged()
    }

    inner class AppDataUsageViewHolder(val binding: ItemAppDataUsageBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppDataUsageViewHolder {
        val binding =
            ItemAppDataUsageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AppDataUsageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AppDataUsageViewHolder, position: Int) {
        with(holder) {
            binding.imvAppIcon.setImageDrawable(mList[position].iconDrawable)
            binding.tvMobileUsage.text = convertData(mList[position].totalMobile.toDouble())
            binding.tvWifiUsage.text = convertData(mList[position].totalWifi.toDouble())
            binding.tvTotal.text = convertData(mList[position].total.toDouble())
        }
    }

    override fun getItemCount(): Int {
        return mList.size
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