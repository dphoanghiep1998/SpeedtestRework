package com.example.speedtest_rework.ui.signal_test.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.speedtest_rework.databinding.ItemSignalLocationBinding

class SignalLocationAdapter : RecyclerView.Adapter<SignalLocationAdapter.SignalViewHolder>() {
    private var mList: List<Pair<String, String>> = listOf()
    fun setData(mListPassed: List<Pair<String, String>>) {
        mList = mListPassed
        notifyItemRangeChanged(0,mList.size)
    }

    inner class SignalViewHolder(val binding: ItemSignalLocationBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SignalViewHolder {
        val binding =
            ItemSignalLocationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SignalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SignalViewHolder, position: Int) {
        with(holder) {
            binding.tvLocation.text = mList[position].first
            binding.tvSignalValue.text = mList[position].second
        }
    }

    override fun getItemCount(): Int =
        mList.size

}