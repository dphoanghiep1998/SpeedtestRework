package com.example.speedtest_rework.ui.main.result_history.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.speedtest_rework.R
import com.example.speedtest_rework.common.DateTimeUtils
import com.example.speedtest_rework.common.custom_view.UnitType
import com.example.speedtest_rework.common.format

import com.example.speedtest_rework.data.model.HistoryModel

class HistoryAdapter(resultTouchHelper: ResultTouchHelper) :
    RecyclerView.Adapter<HistoryAdapter.ConnectivityTestViewHolder>() {
    private var mList: List<HistoryModel>? = null
    private val resultTouchHelper: ResultTouchHelper
    private var unitType = UnitType.MBPS
    fun setData(mList: List<HistoryModel>?) {
        this.mList = mList
        notifyDataSetChanged()
    }

    fun setData(unitType: UnitType) {
        this.unitType = unitType
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConnectivityTestViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_result, parent, false)
        return ConnectivityTestViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConnectivityTestViewHolder, position: Int) {

        mList?.let {
            val model: HistoryModel = it[position]
            if (model.network == "wifi") {
                val source: Int =
                    if (model.download >= 40) R.drawable.ic_signal_good else if (model.download < 40 && model.download >= 20) R.drawable.ic_signal_normal else R.drawable.ic_signal_low
                holder.connectionType.setImageResource(source)
            } else {
                holder.connectionType.setImageResource(R.drawable.ic_mobiledata)
            }
            holder.date.text = DateTimeUtils.getDateConvertedToResult(model.time)
            holder.uploadRate.text = format(convert(model.upload))
            holder.downloadRate.text = format(convert(model.download))
            holder.itemView.setOnClickListener {
                resultTouchHelper.onClickResultTest(
                    model
                )
            }
        }

    }

    override fun getItemCount(): Int {
        return if (mList != null) {
            mList!!.size
        } else 0
    }

    inner class ConnectivityTestViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var date: TextView
        var downloadRate: TextView
        var uploadRate: TextView
        var connectionType: ImageView

        init {
            date = itemView.findViewById(R.id.tv_dateScan)
            downloadRate = itemView.findViewById(R.id.tv_downloadRate)
            uploadRate = itemView.findViewById(R.id.tv_uploadRate)
            connectionType = itemView.findViewById(R.id.imv_connectionType)
        }
    }

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