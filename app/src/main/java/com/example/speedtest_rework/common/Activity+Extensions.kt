package com.example.speedtest_rework.common

import android.app.Activity
import com.example.speedtest_rework.CustomApplication

val Activity.customApplication: CustomApplication
get() = application as CustomApplication