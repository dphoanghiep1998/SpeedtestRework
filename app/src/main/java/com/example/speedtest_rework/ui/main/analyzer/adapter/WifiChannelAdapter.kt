package com.example.speedtest_rework.ui.main.analyzer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.speedtest_rework.R
import com.example.speedtest_rework.ui.main.analyzer.model.WifiModel

class WifiChannelAdapter(val context: Context, helper: ItemTouchHelper) :
    RecyclerView.Adapter<WifiChannelAdapter.WifiChannelViewHolder>() {
    private var mList: List<WifiModel>? = null
    private val helper: ItemTouchHelper
    fun setData(mList: List<WifiModel>?) {
        this.mList = mList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WifiChannelViewHolder {
        return WifiChannelViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_wifi_channel, parent, false)
        )
    }

    override fun onBindViewHolder(holder: WifiChannelViewHolder, position: Int) {
        val wifiModel: WifiModel = mList!![position]
        if (wifiModel != null) {
            val level: Int = wifiModel.wifi_level
            val source: Int =
                if (level >= -60) R.drawable.ic_signal_good_wifi else if (level < -60 && level >= -90) R.drawable.ic_signal_normal_wifi else R.drawable.ic_signal_low_wifi
            holder.imvWifi.setImageResource(source)
            holder.tvFrequency.text = wifiModel.wifi_frequency.toString() + " MHz"
            holder.tvStrength.text = wifiModel.wifi_level.toString() + " dBm"
            holder.tvSecureType.text = wifiModel.wifi_secure_type
            holder.tvWifiName.text = wifiModel.wifi_name
            if (wifiModel.isWifi_isConnected) {
                holder.tvInternalIp.visibility = View.GONE
                holder.tvFrequency.visibility = View.GONE
                holder.tvConnected.visibility = View.VISIBLE
                holder.tvConnected.text = context.getString(R.string.connected)
            } else {
                holder.itemView.background =
                    holder.itemView.resources.getDrawable(R.drawable.infor_container)
                holder.tvInternalIp.visibility = View.GONE
                holder.tvFrequency.visibility = View.GONE
                holder.tvConnected.visibility = View.GONE
            }
            holder.itemView.setOnClickListener {
                helper.onClickItemWifi(
                    wifiModel
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return if (mList != null) {
            mList!!.size
        } else 0
    }

    inner class WifiChannelViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var imvWifi: ImageView
        var tvWifiName: TextView
        var tvInternalIp: TextView
        var tvSecureType: TextView
        var tvStrength: TextView
        var tvFrequency: TextView
        var tvConnected: TextView

        init {
            imvWifi = itemView.findViewById(R.id.imv_wifi)
            tvWifiName = itemView.findViewById(R.id.tv_wifi_name)
            tvInternalIp = itemView.findViewById(R.id.tv_internal_ip)
            tvSecureType = itemView.findViewById(R.id.tv_security_type)
            tvStrength = itemView.findViewById(R.id.tv_signal_strength)
            tvFrequency = itemView.findViewById(R.id.tv_hz)
            tvConnected = itemView.findViewById(R.id.tv_connected)
        }
    }

    init {
        this.helper = helper
    }
}