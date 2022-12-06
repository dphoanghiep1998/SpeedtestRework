package com.example.speedtest_rework.ui.data_usage.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.speedtest_rework.ui.data_usage.model.DataUsageModel

class DiffDataUsageCallBack(
    private val oldList: List<DataUsageModel>,
    var newList: List<DataUsageModel>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem.day == newItem.day
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem.total == newItem.total
    }

}