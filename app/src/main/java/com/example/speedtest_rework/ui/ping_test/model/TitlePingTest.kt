package com.example.speedtest_rework.ui.ping_test.model

class TitlePingTest(val title: String, val value: String) : ItemPingTest() {
    override val type: Int
        get() = 0
}