package com.example.speedtest_rework.ui.main.analyzer.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.speedtest_rework.ui.main.analyzer.model.WiFiData

class DiffChannelCallback(
    private val oldData: WiFiData,
    var newData: WiFiData
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldData.wiFiDetails.size
    }

    override fun getNewListSize(): Int {
        return newData.wiFiDetails.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldData.wiFiDetails[oldItemPosition]
        val newItem = newData.wiFiDetails[newItemPosition]
        return oldItem.wiFiIdentifier.ssid == newItem.wiFiIdentifier.ssid
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldData.wiFiDetails[oldItemPosition]
        val newItem = newData.wiFiDetails[newItemPosition]
        return oldItem == newItem
    }

}