package com.example.speedtest_rework.ui.ping_test.advanced_ping_test.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.speedtest_rework.databinding.ItemContentRecentBinding
import com.example.speedtest_rework.databinding.ItemHeaderRecentBinding
import com.example.speedtest_rework.ui.ping_test.advanced_ping_test.interfaces.RecentHelper
import com.example.speedtest_rework.ui.ping_test.advanced_ping_test.model.RecentModel

class RecentAdapter(private val listener: RecentHelper) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mList: List<RecentModel> = mutableListOf()
    fun setList(list: List<RecentModel>) {
        this.mList = list
    }

    inner class RecentHeaderViewHolder(val binding: ItemHeaderRecentBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class RecentContentViewHolder(val binding: ItemContentRecentBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            0 -> {
                val headerBinding =
                    ItemHeaderRecentBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                return RecentHeaderViewHolder(headerBinding)
            }
            else -> {
                val contentBinding =
                    ItemContentRecentBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                return RecentContentViewHolder(contentBinding)
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            0 -> {
                with(holder as RecentHeaderViewHolder) {
                    binding.imvDelete.setOnClickListener {
                        listener.onDeleteAllRecent()
                    }
                }
            }
            else -> {
                with(holder as RecentContentViewHolder) {
                    binding.tvContent.text = mList[position].content
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }
}