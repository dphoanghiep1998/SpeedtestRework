package com.example.speedtest_rework.ui.main.analyzer.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.speedtest_rework.R
import com.example.speedtest_rework.common.utils.clickWithDebounce
import com.example.speedtest_rework.ui.main.analyzer.band.WiFiBand
import com.example.speedtest_rework.ui.main.analyzer.model.WiFiConnection
import com.example.speedtest_rework.ui.main.analyzer.model.WiFiData
import com.example.speedtest_rework.ui.main.analyzer.model.WiFiDetail
import com.example.speedtest_rework.ui.main.analyzer.model.getSecure
import okhttp3.internal.filterList
import java.util.*

class WifiChannelAdapter(
    val context: Context,
    helper: ItemTouchHelper,
    listener: ListSizeListener
) :
    RecyclerView.Adapter<WifiChannelAdapter.WifiChannelViewHolder>() {
    private lateinit var wifiData: WiFiData
    private var minFreq: Int = 0
    private var maxFreq: Int = 0
    private val helper: ItemTouchHelper
    private val listener: ListSizeListener
    private var clickedPosition = -1


    fun setData(wifiData: WiFiData, maxFreq: Int, minFreq: Int) {
        this.maxFreq = maxFreq
        this.minFreq = minFreq
        this.wifiData = wifiData
        reIndex0(this.wifiData)
        notifyDataSetChanged()
    }

    private fun reIndex0(data: WiFiData) {

        val mList = data.wiFiDetails.filter {
            it.wiFiSignal.centerFrequency in minFreq..maxFreq
        }
        listener.onListChange(mList.size)

        mList.forEachIndexed { index, item ->
            if (item.wiFiIdentifier.ssidRaw == data.wiFiConnection.wiFiIdentifier.ssidRaw) {
                Collections.swap(mList, 0, index)
                return@forEachIndexed
            }
        }
        data.wiFiDetails = mList

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WifiChannelViewHolder {
        return WifiChannelViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_wifi_channel, parent, false)
        )
    }

    override fun onBindViewHolder(holder: WifiChannelViewHolder, position: Int) {
        val wiFiDetail: WiFiDetail = wifiData.wiFiDetails[position]
        val wiFiConnection: WiFiConnection = wifiData.wiFiConnection
        val level: Int = wiFiDetail.wiFiSignal.level
        val source: Int =
            if (level >= -60) R.drawable.ic_signal_good_wifi else if (level < -60 && level >= -90) R.drawable.ic_signal_normal_wifi else R.drawable.ic_signal_low_wifi
        holder.itemView.background = if (position == clickedPosition) ContextCompat.getDrawable(
            context,
            R.drawable.infor_container_wifi_selected
        ) else ContextCompat.getDrawable(
            context,
            R.drawable.infor_container_wifi
        )
        holder.imvWifi.setImageResource(source)
        holder.tvStrength.text = level.toString() + context.resources.getString(R.string.dbm)
        holder.tvSecureType.text = getSecure(wiFiDetail.capabilities)
        holder.tvWifiName.text = wiFiDetail.wiFiIdentifier.ssid
        if (wiFiDetail.wiFiIdentifier.ssid == wiFiConnection.wiFiIdentifier.ssid) {
            holder.tvConnected.visibility = View.VISIBLE
            holder.tvConnected.text = context.getString(R.string.connected)
        } else {
            holder.tvConnected.visibility = View.GONE
        }
        holder.itemView.clickWithDebounce {
            if (this.clickedPosition == holder.adapterPosition) {
                this.clickedPosition = -1
            } else {
                this.clickedPosition = holder.adapterPosition
            }
            notifyDataSetChanged()
            helper.onClickItemWifi(
                wiFiDetail,
                this.clickedPosition == -1
            )
        }
    }

    override fun getItemCount(): Int {
        return if (::wifiData.isInitialized) {
            wifiData.wiFiDetails.size
        } else {
            0
        }
    }

    inner class WifiChannelViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var imvWifi: ImageView
        var tvWifiName: TextView
        var tvSecureType: TextView
        var tvStrength: TextView
        var tvConnected: TextView

        init {
            imvWifi = itemView.findViewById(R.id.imv_wifi)
            tvWifiName = itemView.findViewById(R.id.tv_wifi_name)
            tvSecureType = itemView.findViewById(R.id.tv_security_type)
            tvStrength = itemView.findViewById(R.id.tv_signal_strength)
            tvConnected = itemView.findViewById(R.id.tv_connected)
        }
    }

    init {
        this.helper = helper
        this.listener = listener
    }
}