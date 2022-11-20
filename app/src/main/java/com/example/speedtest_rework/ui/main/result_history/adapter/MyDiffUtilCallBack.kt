package com.example.speedtest_rework.ui.main.result_history.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.speedtest_rework.data.model.HistoryModel


class MyDiffUtilCallBack(newList: List<HistoryModel>?, oldList: List<HistoryModel>?) :
    DiffUtil.Callback() {
    var newList: List<HistoryModel>?
    var oldList: List<HistoryModel>?

    init {
        this.newList = newList
        this.oldList = oldList
    }

    override fun getOldListSize(): Int {
        return if (oldList != null) oldList!!.size else 0
    }

    override fun getNewListSize(): Int {
        return if (newList != null) newList!!.size else 0
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return newList!![newItemPosition].id === oldList!![oldItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return newList!![newItemPosition] == (oldList!![oldItemPosition])
    }


}