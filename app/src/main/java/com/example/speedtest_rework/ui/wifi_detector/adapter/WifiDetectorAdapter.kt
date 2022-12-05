package com.example.speedtest_rework.ui.wifi_detector.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.speedtest_rework.R
import com.example.speedtest_rework.common.utils.AppSharePreference
import com.example.speedtest_rework.common.utils.clickWithDebounce
import com.example.speedtest_rework.databinding.ItemDeviceDetectorBinding
import com.example.speedtest_rework.ui.wifi_detector.interfaces.ItemDeviceHelper
import com.example.speedtest_rework.ui.wifi_detector.model.DeviceModel

class WifiDetectorAdapter(private val listener: ItemDeviceHelper) :
    RecyclerView.Adapter<WifiDetectorAdapter.WifiDetectorViewHolder>() {
    private var mList: List<DeviceModel> = mutableListOf()
    private var actionDone = true
    private var savedSetIp = AppSharePreference.INSTANCE.getIpList(hashSetOf())
    fun setData(list: List<DeviceModel>, actionDone: Boolean) {
        mList = list
        this.actionDone = actionDone
        notifyDataSetChanged()
    }

    fun setData(actionDone: Boolean) {
        this.actionDone = actionDone
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
            if (checkDevice(mList[position].device_ip)) {
                binding.imvFlag.setImageResource(R.drawable.ic_flag_active)
            } else {
                binding.imvFlag.setImageResource(R.drawable.ic_flag_inactive)
            }
            binding.tvDeviceName.text = mList[position].device_name
            binding.tvDeviceIp.text = mList[position].device_ip
            binding.imvFlag.clickWithDebounce {
                if (checkDevice(mList[position].device_ip)) {
                    savedSetIp.remove(mList[position].device_ip)
                } else {
                    savedSetIp.add(mList[position].device_ip)
                }
                AppSharePreference.INSTANCE.saveIpList(savedSetIp)
                notifyItemChanged(position)
            }
            binding.imvFlag.isEnabled = actionDone
        }
    }

    override fun getItemCount(): Int =
        mList.size

    private fun checkDevice(ip: String): Boolean =
        savedSetIp.contains(ip)
}