package com.example.speedtest_rework.ui.ping_test.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
class ContentPingTest(
    val title: String,
    var url: String,
    var value: Int = 0,
    var normal: Boolean = true,
) :
    ItemPingTest(), Parcelable {
    override val type: Int
        get() = 1
}