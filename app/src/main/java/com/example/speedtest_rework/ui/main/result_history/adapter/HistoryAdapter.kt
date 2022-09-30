package com.example.speedtest.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.speedtest_rework.R
import com.example.speedtest_rework.common.DateTimeUtils

import com.example.speedtest_rework.data.model.HistoryModel
import com.example.speedtest_rework.ui.main.result_history.adapter.ResultTouchHelper

class HistoryAdapter(resultTouchHelper: ResultTouchHelper) :
    RecyclerView.Adapter<HistoryAdapter.ConnectivityTestViewHolder>() {
    private var mList: List<HistoryModel>? = null
    private val resultTouchHelper: ResultTouchHelper
    fun setData(mList: List<HistoryModel>?) {
        this.mList = mList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConnectivityTestViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_result, parent, false)
        return ConnectivityTestViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConnectivityTestViewHolder, position: Int) {
        val model: HistoryModel = mList!![position]
        if (model != null) {
            if (model.network == "wifi") {
                val source: Int =
                    if (model.download >= -60) R.drawable.ic_signal_good else if (model.download < -60 && model.download >= -90) R.drawable.ic_signal_normal else R.drawable.ic_signal_low
                holder.connectionType.setImageResource(source)
            } else {
                holder.connectionType.setImageResource(R.drawable.ic_mobiledata)
            }
            holder.date.text = DateTimeUtils.getDateConverted(model.time)
            holder.uploadRate.text = model.upload.toString()
            holder.downloadRate.text = model.download.toString()
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
}