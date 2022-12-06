package com.example.speedtest_rework.ui.ping_test.advanced_ping_test.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.speedtest_rework.common.utils.clickWithDebounce
import com.example.speedtest_rework.databinding.ItemContentRecentBinding
import com.example.speedtest_rework.ui.ping_test.advanced_ping_test.interfaces.RecentHelper

class RecentAdapter(private val listener: RecentHelper) :
    RecyclerView.Adapter<RecentAdapter.RecentContentViewHolder>() {
    private var mList: MutableList<String> = mutableListOf()

    fun setList(list: List<String>) {
        val diffRecent = DiffRecent(mList, list)
        val diffResult = DiffUtil.calculateDiff(diffRecent)
        mList.clear()
        mList.addAll(list)
        diffResult.dispatchUpdatesTo(this)
    }

    inner class RecentContentViewHolder(val binding: ItemContentRecentBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentContentViewHolder {

        val contentBinding =
            ItemContentRecentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return RecentContentViewHolder(contentBinding)


    }

    override fun onBindViewHolder(holder: RecentContentViewHolder, position: Int) {

        with(holder) {
            binding.tvContent.text = mList[position]
            binding.tvContent.clickWithDebounce {
                listener.onClickItem(mList[position])
            }
        }

    }

    override fun getItemCount(): Int {
        return mList.size
    }
}