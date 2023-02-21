package com.example.speedtest_rework.core.upload

import com.example.speedtest_rework.core.base.Connection
import kotlinx.coroutines.*
import java.util.*

abstract class Uploader(private val c: Connection, private val path: String, ckSize: Int) {
    private var stopASAP = false
    private var resetASAP = false
    private var totUploaded: Long = 0
    private val garbage: ByteArray
    private var job: Job? = null
    private var coroutineScope: CoroutineScope? = null

    init {
        garbage = ByteArray(ckSize * 1024)
        val r = Random(System.nanoTime())
        r.nextBytes(garbage)
    }

    fun startUpload() {
        coroutineScope = CoroutineScope(Dispatchers.IO)
        job = coroutineScope?.launch {
            try {
                val s = path
                var lastProgressEvent = System.currentTimeMillis()
                val out = c.outputStream
                while (true) {
                    if (stopASAP) break
                    c.POST(s, true, "application/octet-stream", garbage.size.toLong())
                    var offset = 0
                    while (offset < garbage.size) {
                        if (stopASAP) break
                        val l =
                            if (offset + BUFFER_SIZE >= garbage.size) garbage.size - offset else BUFFER_SIZE
                        out!!.write(garbage, offset, l)
                        if (stopASAP) break
                        if (resetASAP) {
                            totUploaded = 0
                            resetASAP = false
                        }
                        totUploaded += l.toLong()
                        if (System.currentTimeMillis() - lastProgressEvent > 100) {
                            lastProgressEvent = System.currentTimeMillis()
                            onProgress(totUploaded)
                        }
                        offset += BUFFER_SIZE
                    }
                    if (stopASAP) break
                    while (c.readLineUnbuffered()!!.trim { it <= ' ' }.isNotEmpty());
                }
                c.close()
            } catch (t: Throwable) {
                try {
                    c.close()
                } catch (t1: Throwable) {
                }
                onError(t.toString())
            }
        }

    }

    fun stopUpload() {
        coroutineScope?.cancel()
        job?.cancel()
        stopASAP = true
    }

    abstract fun onProgress(uploaded: Long)
    abstract fun onError(err: String?)
    fun resetUploadCounter() {
        resetASAP = true
    }

    val uploaded: Long
        get() = if (resetASAP) 0 else totUploaded

    companion object {
        private const val BUFFER_SIZE = 150 * 1024
    }
}