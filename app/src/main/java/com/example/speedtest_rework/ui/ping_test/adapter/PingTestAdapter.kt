package com.example.speedtest_rework.ui.ping_test.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.speedtest_rework.R
import com.example.speedtest_rework.databinding.ItemPingTestContentBinding
import com.example.speedtest_rework.databinding.ItemPingTestTitleBinding
import com.example.speedtest_rework.ui.ping_test.interfaces.ItemHelper
import com.example.speedtest_rework.ui.ping_test.model.ContentPingTest
import com.example.speedtest_rework.ui.ping_test.model.ItemPingTest
import com.example.speedtest_rework.ui.ping_test.model.TitlePingTest

class PingTestAdapter(val context: Context, private val listener: ItemHelper) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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
                val itemTitle = mData[position] as TitlePingTest
                with(holder as PingTestTitleViewHolder) {
                    binding.tvTitle.text = itemTitle.title
                    binding.tvValue.text = itemTitle.value
                }
            }
            1 -> {
                val itemContent = mData[position] as ContentPingTest
                with(holder as PingTestContentViewHolder) {
                    binding.root.setOnClickListener {
                        listener.onClickItemPing(itemContent)
                    }
                    binding.tvValue.visibility = View.VISIBLE

                    binding.tvTitle.text = itemContent.title
                    if (itemContent.normal) {
                        binding.tvValue.text =
                            if (itemContent.value == 0) "_ _" else itemContent.value.toString()
                        when (itemContent.value) {
                            0 -> {
                                binding.tvValue.setTextColor(
                                    ContextCompat.getColor(
                                        context,
                                        R.color.gray_100
                                    )
                                )
                            }
                            in 1..50 -> {
                                binding.tvValue.setTextColor(
                                    ContextCompat.getColor(
                                        context,
                                        R.color.gradient_green_start
                                    )
                                )
                            }
                            in 51..100 -> {
                                binding.tvValue.setTextColor(
                                    ContextCompat.getColor(
                                        context,
                                        R.color.gradient_yellow_start
                                    )
                                )
                            }
                            else -> {
                                binding.tvValue.setTextColor(
                                    ContextCompat.getColor(
                                        context,
                                        R.color.gradient_red_start
                                    )
                                )
                            }


                        }

                    } else {
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