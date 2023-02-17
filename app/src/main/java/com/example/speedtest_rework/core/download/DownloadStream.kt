package com.example.speedtest_rework.core.download

import Downloader
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.speedtest_rework.core.base.Connection
import com.example.speedtest_rework.core.base.Utils
import com.example.speedtest_rework.core.config.SpeedtestConfig
import kotlinx.coroutines.*

abstract class DownloadStream(
    private val path: String,
    private val ckSize: Int,
    errorHandlingMode: String,
    connectTimeout: Int,
    soTimeout: Int,
    recvBuffer: Int,
    sendBuffer: Int
) {
    private val connectTimeout: Int
    private val soTimeout: Int
    private val recvBuffer: Int
    private val sendBuffer: Int
    private var downloader: Downloader? = null
    private var errorHandlingMode = SpeedtestConfig.ONERROR_ATTEMPT_RESTART
    private var currentDownloaded: Long = 0
    private var previouslyDownloaded: Long = 0
    private var job: Job? = null
    private var coroutineScope: CoroutineScope? = null


    init {
        this.errorHandlingMode = errorHandlingMode
        this.connectTimeout = connectTimeout
        this.soTimeout = soTimeout
        this.recvBuffer = recvBuffer
        this.sendBuffer = sendBuffer
        initLmao()
    }

    private fun initLmao() {
        coroutineScope = CoroutineScope(Dispatchers.IO)
        job = coroutineScope?.launch {
            downloader?.cancelDownload()
            currentDownloaded = 0
            try {
                downloader = object : Downloader(path, ckSize) {
                    override fun onProgress(downloaded: Long) {
                        currentDownloaded = downloaded
                    }

                    override fun onError(err: String?) {
                        if (errorHandlingMode == SpeedtestConfig.ONERROR_FAIL) {
                            this@DownloadStream.onError(err)
                            return
                        }
                        if (errorHandlingMode == SpeedtestConfig.ONERROR_ATTEMPT_RESTART || errorHandlingMode == SpeedtestConfig.ONERROR_MUST_RESTART) {
                            previouslyDownloaded += currentDownloaded
                            initLmao()
                        }
                    }
                }
                downloader?.startDownload()
            } catch (t: Throwable) {
                if (errorHandlingMode == SpeedtestConfig.ONERROR_MUST_RESTART) {
                    Utils.sleep(100)
                    initLmao()
                } else onError(t.toString())
            }
        }
    }

    abstract fun onError(err: String?)

    fun stopASAP() {
        coroutineScope?.cancel()
        job?.cancel()
        downloader?.cancelDownload()
    }

    val totalDownloaded: Long
        get() = previouslyDownloaded + currentDownloaded

    fun resetDownloadCounter() {
        previouslyDownloaded = 0
        currentDownloaded = 0
        downloader?.resetDownloadCounter()
    }


}