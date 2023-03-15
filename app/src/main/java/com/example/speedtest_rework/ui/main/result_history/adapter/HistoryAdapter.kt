package com.example.speedtest_rework.ui.main.result_history.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.speedtest_rework.R
import com.example.speedtest_rework.common.custom_view.UnitType
import com.example.speedtest_rework.common.utils.DateTimeUtils
import com.example.speedtest_rework.common.utils.clickWithDebounce
import com.example.speedtest_rework.common.utils.format
import com.example.speedtest_rework.data.model.HistoryModel
import com.example.speedtest_rework.databinding.ItemNativeAdsBinding
import com.example.speedtest_rework.databinding.ItemResultBinding
import com.google.android.gms.ads.nativead.NativeAd

class HistoryAdapter(resultTouchHelper: ResultTouchHelper) :
    RecyclerView.Adapter<ViewHolder>() {
    private var mList: MutableList<Any> = mutableListOf()
    private val resultTouchHelper: ResultTouchHelper
    private var unitType = UnitType.MBPS
    private var insideNativeAd: NativeAd? = null


    fun setData(newList: MutableList<HistoryModel>) {
        mList.clear()
        mList.addAll(newList)
        notifyDataSetChanged()
    }

    fun setData(unitType: UnitType) {
        this.unitType = unitType
        notifyItemRangeChanged(0, mList.size)
    }

    fun insertAds(nativeAd: NativeAd) {
        if (mList.contains("ads")) {
            return
        }
        insideNativeAd = nativeAd
        if (mList.size <= 3) {
            mList.add("ads")
        } else {
            mList.add(2, "ads")
        }
        notifyItemInserted(3)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            0 -> {
                val binding = ItemResultBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                ConnectivityTestViewHolder(binding)
            }
            else -> {
                val binding =
                    ItemNativeAdsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ConnectivityTestAdsViewHolder(binding)
            }
        }
    }
    override fun getItemViewType(position: Int): Int {
        if (mList[position] == "ads") {
            return 1
        }
        return 0
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder.itemViewType) {
            0 -> {
                with(holder as ConnectivityTestViewHolder) {
                    val model = mList[position] as HistoryModel
                    if (model.network == "wifi") {
                        val source: Int =
                            if (model.download >= 40) R.drawable.ic_signal_good else if (model.download < 40 && model.download >= 20) R.drawable.ic_signal_normal else R.drawable.ic_signal_low
                        binding.imvConnectionType.setImageResource(source)
                    } else {
                        binding.imvConnectionType.setImageResource(R.drawable.ic_mobiledata)
                    }
                    binding.tvDateScan.text = DateTimeUtils.getDateConvertedToResult(model.time)
                    binding.tvUploadRate.text = format(convert(model.upload))
                    binding.tvDownloadRate.text = format(convert(model.download))
                    holder.itemView.clickWithDebounce {
                        resultTouchHelper.onClickResultTest(
                            model
                        )
                    }
                }
            }
            else -> {
                with(holder as ConnectivityTestAdsViewHolder) {
                    if (insideNativeAd != null) {
                        with(binding.nativeAdMediumView) {
                            visibility = View.VISIBLE
                            setNativeAd(insideNativeAd!!)
                            showShimmer(false)
                        }
                    } else {
                        with(binding.nativeAdMediumView) {
                            visibility = View.GONE
                            showShimmer(true)
                        }
                    }
                }
            }
        }


    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class ConnectivityTestViewHolder(val binding: ItemResultBinding) :
        ViewHolder(binding.root) {
    }

    inner class ConnectivityTestAdsViewHolder(val binding: ItemNativeAdsBinding) :
        ViewHolder(binding.root)

    init {
        this.resultTouchHelper = resultTouchHelper
    }

    private fun convertMbpsToMbs(value: Double): Double {
        return value * .125
    }

    private fun convertMbpsToKbs(value: Double): Double {
        return value * 125
    }

    private fun convert(value: Double): Double {
        if (unitType == UnitType.MBS) {
            return convertMbpsToMbs(value)
        }
        if (unitType == UnitType.KBS) {
            return convertMbpsToKbs(value)
        }
        return value
    }

}