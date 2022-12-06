package com.example.speedtest_rework.ui.ping_test.advanced_ping_test.adapter

import androidx.recyclerview.widget.DiffUtil


class DiffRecent(newList: List<String>, oldList: List<String>) :
    DiffUtil.Callback() {
    var newList: List<String>

    var oldList: List<String>

    init {
        this.newList = newList
        this.oldList = oldList
    }

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return newList[newItemPosition] === oldList[oldItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return newList[newItemPosition] == (oldList[oldItemPosition])
    }


}