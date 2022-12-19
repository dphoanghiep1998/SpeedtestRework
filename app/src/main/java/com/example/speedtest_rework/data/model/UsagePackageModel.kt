package com.example.speedtest_rework.data.model

import android.graphics.drawable.Drawable

class UsagePackageModel(
    val uid: Int,
    val iconDrawable: Drawable?,
    val totalMobile: Long,
    val totalWifi: Long,
    var total: Long

)
