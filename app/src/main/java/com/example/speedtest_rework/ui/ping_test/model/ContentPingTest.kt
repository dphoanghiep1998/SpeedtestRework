package com.example.speedtest_rework.ui.ping_test.model

class ContentPingTest(
    val title: String,
    var value: Int = -1,
    var normal: Boolean = true,
) :
    ItemPingTest() {
    override val type: Int
        get() = 1
}