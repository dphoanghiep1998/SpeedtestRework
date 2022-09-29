package com.example.speedtest_rework.ui.main.result_history.adapter

import com.example.speedtest_rework.data.model.HistoryModel

interface ResultTouchHelper {
    fun onClickResultTest(historyModel: HistoryModel?)
}