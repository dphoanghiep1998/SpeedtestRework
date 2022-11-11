package com.example.speedtest_rework.ui.wifi_detector.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.speedtest_rework.databinding.ItemDeviceDetectorBinding
import com.example.speedtest_rework.ui.wifi_detector.interfaces.ItemDeviceHelper
import com.example.speedtest_rework.ui.wifi_detector.model.DeviceModel

class WifiDetectorAdapter(private val listener: ItemDeviceHelper) :
    RecyclerView.Adapter<WifiDetectorAdapter.WifiDetectorViewHolder>() {
    private var mList: List<DeviceModel> = mutableListOf()
    fun setData(list: List<DeviceModel>) {
        mList = list
        notifyDataSetChanged()
    }

    inner class WifiDetectorViewHolder(val binding: ItemDeviceDetectorBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WifiDetectorViewHolder {
        val binding =
            ItemDeviceDetectorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WifiDetectorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WifiDetectorViewHolder, position: Int) {
        with(holder) {
            binding.tvDeviceName.text = mList[position].device_name
            binding.tvDeviceIp.text = mList[position].device_ip
            binding.imvFlag.setOnClickListener {
                listener.onClickFlag(mList[position])
            }
        }
    }

    override fun getItemCount(): Int =
        mList.size

}