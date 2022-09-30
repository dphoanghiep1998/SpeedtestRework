package com.example.speedtest.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.speedtest_rework.R
import com.example.speedtest_rework.ui.main.analyzer.adapter.ItemTouchHelper
import com.example.speedtest_rework.ui.main.analyzer.model.WifiModel

class WifiChannelAdapter(helper: ItemTouchHelper) :
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
                if (level >= -60) R.drawable.ic_signal_good else if (level < -60 && level >= -90) R.drawable.ic_signal_normal else R.drawable.ic_signal_low
            holder.imv_wifi.setImageResource(source)
            holder.tv_frequency.text = wifiModel.wifi_frequency.toString() + " MHz"
            holder.tv_strength.text = wifiModel.wifi_level.toString() + " dBm"
            holder.tv_secureType.text = wifiModel.wifi_secure_type
            holder.tv_wifiName.text = wifiModel.wifi_name
            if (wifiModel.isWifi_isConnected) {
                holder.tv_internalIp.visibility = View.GONE
                holder.tv_frequency.visibility = View.GONE
                holder.tv_connected.visibility = View.VISIBLE
                //setdata
                holder.tv_connected.text = "Đã kết nối"
            } else {
                holder.itemView.background =
                    holder.itemView.resources.getDrawable(R.drawable.infor_container)
                holder.tv_internalIp.visibility = View.GONE
                holder.tv_frequency.visibility = View.GONE
                holder.tv_connected.visibility = View.GONE
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
        var imv_wifi: ImageView
        var tv_wifiName: TextView
        var tv_internalIp: TextView
        var tv_secureType: TextView
        var tv_strength: TextView
        var tv_frequency: TextView
        var tv_connected: TextView

        init {
            imv_wifi = itemView.findViewById(R.id.imv_wifi)
            tv_wifiName = itemView.findViewById(R.id.tv_wifi_name)
            tv_internalIp = itemView.findViewById(R.id.tv_internal_ip)
            tv_secureType = itemView.findViewById(R.id.tv_security_type)
            tv_strength = itemView.findViewById(R.id.tv_signal_strength)
            tv_frequency = itemView.findViewById(R.id.tv_hz)
            tv_connected = itemView.findViewById(R.id.tv_connected)
        }
    }

    init {
        this.helper = helper
    }
}