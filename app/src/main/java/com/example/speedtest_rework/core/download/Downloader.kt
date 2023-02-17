import android.net.SSLCertificateSocketFactory
import android.util.Log
import kotlinx.coroutines.*
import java.io.InputStream
import java.net.URL
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.*

abstract class Downloader(
    private val path: String, private val ckSize: Int,
) {
    private var totDownloaded: Long = 0

    private var job: Job? = null
    private var coroutineScope: CoroutineScope? = null
    private var inputStream: InputStream? = null

    private  fun trustEveryone() {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier { hostname, session -> true }
            val context: SSLContext = SSLContext.getInstance("TLS")
            context.init(null, arrayOf<X509TrustManager>(object : X509TrustManager {
                @Throws(CertificateException::class)
                override fun checkClientTrusted(
                    chain: Array<X509Certificate?>?,
                    authType: String?
                ) {
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(
                    chain: Array<X509Certificate?>?,
                    authType: String?
                ) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf(arrayOf<X509Certificate>()[0])
                }

            }), SecureRandom())
            HttpsURLConnection.setDefaultSSLSocketFactory(
                context.socketFactory
            )
        } catch (e: Exception) { // should never happen
            e.printStackTrace()
        }
    }
    fun startDownload() {
        stopASAP = true
        coroutineScope = CoroutineScope(Dispatchers.IO)
        job = coroutineScope?.launch {
            try {
                var lastProgressEvent = System.currentTimeMillis()
                val ckBytes = ckSize * 1048576L
                var bytesLeft: Long = 0
                val url = URL(path)
                val httpsConn = url.openConnection() as HttpsURLConnection
                httpsConn.sslSocketFactory = SSLCertificateSocketFactory.getInsecure(0, null);
                httpsConn.setRequestProperty("Cache-Control", "no-cache")
                httpsConn.setRequestProperty("Expires", "-1")
                httpsConn.setRequestProperty("Pragma", "no-cache")
                httpsConn.setRequestProperty("Connection", "Keep-Alive")
                httpsConn.connect()
                inputStream = httpsConn.inputStream
                val buf = ByteArray(BUFFER_SIZE)
                while (true) {
                    bytesLeft += ckBytes
                    val l = inputStream?.read(buf)
                    if (l != null) {
                        if (l < 0 || !stopASAP) {
                            break
                        }
                        bytesLeft -= l.toLong()
                        totDownloaded += l.toLong()
                        if (System.currentTimeMillis() - lastProgressEvent > 200) {
                            lastProgressEvent = System.currentTimeMillis()
                            onProgress(totDownloaded)
                        }
                    }
                    Log.d("TAG", "startDownload: "+totDownloaded)

                    if (!stopASAP) {
                        break
                    }


                }
                inputStream?.close()
            } catch (t: Throwable) {
                try {
                    inputStream?.close()
                    cancelDownload()
                } catch (t1: Throwable) {
                }
                onError(t.printStackTrace().toString())
            }
        }
    }

    fun cancelDownload() {
        coroutineScope?.cancel()
        job?.cancel()
        stopASAP = false
    }

    abstract fun onProgress(downloaded: Long)
    abstract fun onError(err: String?)
    fun resetDownloadCounter() {
        totDownloaded = 0
    }

    companion object {
        var stopASAP = true
        private const val BUFFER_SIZE = 1024
    }
}