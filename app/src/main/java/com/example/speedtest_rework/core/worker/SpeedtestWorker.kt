package com.example.speedtest_rework.core.worker

import android.util.Log
import com.example.speedtest_rework.core.base.Utils
import com.example.speedtest_rework.core.config.SpeedtestConfig
import com.example.speedtest_rework.core.download.DownloadStream
import com.example.speedtest_rework.core.ping.PingStream
import com.example.speedtest_rework.core.serverSelector.TestPoint
import com.example.speedtest_rework.core.upload.UploadStream
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.log

abstract class SpeedtestWorker(private val backend: TestPoint, config: SpeedtestConfig?) :
    Thread() {
    private val config: SpeedtestConfig
    private var stopASAP = false
    private var dl = -1.0
    private var ul = -1.0
    private var ping = -1.0
    private var jitter = -1.0
    private var count = 0.0
    private var sumDiff = 0.0

    override fun run() {
        try {
            for (t in config.test_order.toCharArray()) {
                if (t == '_') Utils.sleep(100)
                if (t == 'D') dlTest()
                if (t == 'U') ulTest()
                if (t == 'P') pingTest()
                if (stopASAP) {
                    break
                }
            }
            if (!stopASAP) {
                onEnd()
            }

        } catch (t: Throwable) {
            onCriticalFailure(t.toString())
        }
    }

    private var dlCalled = false


    private fun dlTest() {
        dlCalled = if (dlCalled) return else true
        onDownloadUpdate(0.0, 0.0)
        val streams = arrayOfNulls<DownloadStream>(config.dl_parallelStreams)
        for (i in streams.indices) {
            streams[i] = object : DownloadStream(
                backend.dlURL,
                config.dl_ckSize,
                config.errorHandlingMode,
                config.dl_connectTimeout,
                config.dl_soTimeout,
                config.dl_recvBuffer,
                config.dl_sendBuffer
            ) {
                override fun onError(err: String?) {
                    abort()
                    onCriticalFailure(err)
                }
            }
            if (stopASAP) {
                break
            }
        }

        Utils.sleep(config.ul_streamDelay.toLong())
        var graceTimeDone = false
        var startT = System.currentTimeMillis()
        var bonusT: Long = 0
        while (true) {
            val t = (System.currentTimeMillis() - startT).toDouble()
            if (!graceTimeDone && t >= config.dl_graceTime * 1000) {
                graceTimeDone = true
                for (d in streams) d!!.resetDownloadCounter()
                startT = System.currentTimeMillis()
                continue
            }
            if (stopASAP || t + bonusT >= config.time_dl_max * 1000) {
                for (d in streams) d?.stopASAP()
                Log.d("TAG1234", "nhay vo day: ")

                break
            }
            if (graceTimeDone) {
                var totDownloaded: Long = 0
                for (d in streams) totDownloaded += d!!.totalDownloaded
                var speed: Double = totDownloaded / ((if (t < 100) 100.0 else t) / 1000.0)
                if (config.time_auto) {
                    val b = 2.5 * speed / 100000.0
                    bonusT += if (b > 200) 200 else b.toLong()
                }
                val progress = (t + bonusT) / (config.time_dl_max * 1000).toDouble()
                speed =
                    speed * 8 * config.overheadCompensationFactor / if (config.useMebibits) 1048576.0 else 1000000.0
                dl = speed

                if (progress >= 1) {
                    Log.d("TAG1234", "nhay vao day: ")
                    for (d in streams) d?.stopASAP()
                    break
                }
                onDownloadUpdate(dl, if (progress >= 1) 1.0 else progress)
            }
            Utils.sleep(100)
        }
        onDownloadUpdate(dl, 1.0)

    }

    private var ulCalled = false
    private fun ulTest() {
        ulCalled = if (ulCalled) return else true
        val start = System.currentTimeMillis()
        onUploadUpdate(0.0, 0.0)
        val streams = arrayOfNulls<UploadStream>(config.ul_parallelStreams)
        for (i in streams.indices) {
            streams[i] = object : UploadStream(
                backend.server,
                backend.ulURL,
                config.ul_ckSize,
                config.errorHandlingMode,
                config.ul_connectTimeout,
                config.ul_soTimeout,
                config.ul_recvBuffer,
                config.ul_sendBuffer
            ) {
                override fun onError(err: String?) {
                    abort()
                    onCriticalFailure(err)
                }
            }
            Utils.sleep(config.ul_streamDelay.toLong())
        }

        var graceTimeDone = false
        var startT = System.currentTimeMillis()
        var bonusT: Long = 0
        while (true) {
            val t = (System.currentTimeMillis() - startT).toDouble()
            if (!graceTimeDone && t >= config.ul_graceTime * 1000) {
                graceTimeDone = true
                for (u in streams) u!!.resetUploadCounter()
                startT = System.currentTimeMillis()
                continue
            }
            if (stopASAP || t + bonusT >= config.time_ul_max * 1000) {
                for (u in streams) u!!.stopASAP()
                break
            }
            if (graceTimeDone) {
                var totUploaded: Long = 0
                for (u in streams) totUploaded += u!!.totalUploaded
                var speed: Double = totUploaded / ((if (t < 100) 100.0 else t) / 1000.0)
                if (config.time_auto) {
                    val b = 2.5 * speed / 100000.0
                    bonusT += if (b > 200) 200 else b.toLong()
                }
                val progress = (t + bonusT) / (config.time_ul_max * 1000).toDouble()
                speed =
                    speed * 8 * config.overheadCompensationFactor / if (config.useMebibits) 1048576.0 else 1000000.0
                ul = speed
                if (progress > 0) {
                    onUploadUpdate(ul, if (progress > 1) 1.0 else progress)
                }
            }
            Utils.sleep(100)
        }
        if (stopASAP) return
        onUploadUpdate(ul, 1.0)
    }

    private var pingCalled = false

    init {
        this.config = config ?: SpeedtestConfig()
        start()
    }

    private fun pingTest() {
        pingCalled = if (pingCalled) return else true
        onPingJitterUpdate(0.0, 0.0, 0.0)
        val ps: PingStream = object : PingStream(backend.server) {
            private var minPing = Double.MAX_VALUE
            private var prevPing = -1.0
            private var counter = 0
            override fun onError(err: String) {
                onPingJitterUpdate(0.0, 0.0, 0.0)
                abort()
                onCriticalFailure(err)
            }

            override fun onPong(ns: Long): Boolean {
                counter++
                if (ns < minPing) minPing = ns.toDouble()
                ping = minPing
                if (prevPing == -1.0) {
                    jitter = 0.0
                } else {
                    val j = Math.abs(ns - prevPing)
                    sumDiff += j
                    count++
                    jitter = sumDiff / count
                }
                prevPing = ns.toDouble()
                val progress = counter / config.count_ping.toDouble()
                onPingJitterUpdate(ns.toDouble(), jitter, if (progress > 1) 1.0 else progress)
                return !stopASAP
            }

            override fun onDone() {}
        }
        if (stopASAP) return
        onPingJitterUpdate(ping, jitter, 1.0)
    }

    fun abort() {
        if (stopASAP) return
        stopASAP = true
    }

    abstract fun onDownloadUpdate(dl: Double, progress: Double)
    abstract fun onUploadUpdate(ul: Double, progress: Double)
    abstract fun onPingJitterUpdate(ping: Double, jitter: Double, progress: Double)
    abstract fun onEnd()
    abstract fun onCriticalFailure(err: String?)
}