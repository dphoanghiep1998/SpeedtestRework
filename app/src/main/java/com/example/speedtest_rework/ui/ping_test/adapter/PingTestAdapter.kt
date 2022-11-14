package com.example.speedtest_rework.ui.ping_test.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.speedtest_rework.databinding.ItemPingTestContentBinding
import com.example.speedtest_rework.databinding.ItemPingTestTitleBinding
import com.example.speedtest_rework.ui.ping_test.model.ContentPingTest
import com.example.speedtest_rework.ui.ping_test.model.ItemPingTest
import com.example.speedtest_rework.ui.ping_test.model.TitlePingTest

class PingTestAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mData: List<ItemPingTest> = mutableListOf()

    fun setData(list: List<ItemPingTest>) {
        this.mData = list
        notifyDataSetChanged()
    }

    inner class PingTestTitleViewHolder(val binding: ItemPingTestTitleBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class PingTestContentViewHolder(val binding: ItemPingTestContentBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int): Int {
        return mData[position].type

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            0 -> {
                val binding = ItemPingTestTitleBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                PingTestTitleViewHolder(binding)
            }
            1 -> {
                val binding = ItemPingTestContentBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                PingTestContentViewHolder(binding)
            }
            else -> {
                val binding = ItemPingTestTitleBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                PingTestTitleViewHolder(binding)
            }
        }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            0 -> {
                with(holder as PingTestTitleViewHolder) {
                    binding.tvTitle.text = (mData[position] as TitlePingTest).title
                    binding.tvValue.text = (mData[position] as TitlePingTest).value
                }
            }
            1 -> {
                with(holder as PingTestContentViewHolder) {
                    binding.tvValue.visibility = View.VISIBLE

                    binding.tvTitle.text = (mData[position] as ContentPingTest).title
                    if((mData[position] as ContentPingTest).normal){
                        binding.tvValue.text =
                            if ((mData[position] as ContentPingTest).value == -1) "_ _" else (mData[position] as ContentPingTest).value.toString()
                    }else{
                        binding.tvValue.visibility = View.GONE
                    }

                }
            }
        }
    }

    override fun getItemCount(): Int {
        return mData.size
    }


}