package com.example.speedtest_rework.core.worker;

import android.util.Log;

import com.example.speedtest_rework.core.base.Utils;
import com.example.speedtest_rework.core.config.SpeedtestConfig;
import com.example.speedtest_rework.core.download.DownloadStream;
import com.example.speedtest_rework.core.ping.PingStream;
import com.example.speedtest_rework.core.serverSelector.TestPoint;
import com.example.speedtest_rework.core.upload.UploadStream;

public abstract class SpeedtestWorker extends Thread {
    private TestPoint backend;
    private SpeedtestConfig config;
    private boolean stopASAP = false;
    private double dl = -1, ul = -1, ping = -1, jitter = -1;


    public SpeedtestWorker(TestPoint backend, SpeedtestConfig config) {
        this.backend = backend;
        this.config = config == null ? new SpeedtestConfig() : config;

        start();
    }

    public void run() {
        try {
            for (char t : config.getTest_order().toCharArray()) {
                if (t == '_') Utils.sleep(100);
                if (t == 'D') dlTest();
                if (t == 'U') ulTest();
                if (t == 'P') pingTest();
                if (stopASAP) {
                    return;
                }
            }
            onEnd();
        } catch (Throwable t) {
            onCriticalFailure(t.toString());
        }
    }


    private boolean dlCalled = false;

    private void dlTest() {
        if (dlCalled) return;
        else dlCalled = true;
        final long start = System.currentTimeMillis();
        onDownloadUpdate(0, 0);
        DownloadStream[] streams = new DownloadStream[config.getDl_parallelStreams()];
        for (int i = 0; i < streams.length; i++) {
            streams[i] = new DownloadStream(backend.getServer(), backend.getDlURL(), config.getDl_ckSize(), config.getErrorHandlingMode(), config.getDl_connectTimeout(), config.getDl_soTimeout(), config.getDl_recvBuffer(), config.getDl_sendBuffer()) {
                @Override
                public void onError(String err) {
                    abort();
                    onCriticalFailure(err);
                }
            };
            Utils.sleep(config.getDl_streamDelay());
        }
        boolean graceTimeDone = false;
        long startT = System.currentTimeMillis(), bonusT = 0;
        for (; ; ) {
            double t = System.currentTimeMillis() - startT;
            if (!graceTimeDone && t >= config.getDl_graceTime() * 1000) {
                graceTimeDone = true;
                for (DownloadStream d : streams) d.resetDownloadCounter();
                startT = System.currentTimeMillis();
                continue;
            }
            if (stopASAP || t + bonusT >= config.getTime_dl_max() * 1000) {
                for (DownloadStream d : streams) d.stopASAP();
                for (DownloadStream d : streams) d.join();
                break;
            }
            if (graceTimeDone) {
                long totDownloaded = 0;
                for (DownloadStream d : streams) totDownloaded += d.getTotalDownloaded();
                double speed = totDownloaded / ((t < 100 ? 100 : t) / 1000.0);
                if (config.getTime_auto()) {
                    double b = (2.5 * speed) / 100000.0;
                    bonusT += b > 200 ? 200 : b;
                }
                double progress = (t + bonusT) / (double) (config.getTime_dl_max() * 1000);
                speed = (speed * 8 * config.getOverheadCompensationFactor()) / (config.getUseMebibits() ? 1048576.0 : 1000000.0);

                dl = speed;
                if (progress >= 1) {
                    break;
                }
                onDownloadUpdate(dl, progress >= 1 ? 1 : progress);
            }

            Utils.sleep(100);
        }
        if (stopASAP) return;
        onDownloadUpdate(dl, 1);

    }

    private boolean ulCalled = false;

    private void ulTest() {
        if (ulCalled) return;
        else ulCalled = true;
        final long start = System.currentTimeMillis();
        onUploadUpdate(0, 0);
        UploadStream[] streams = new UploadStream[config.getUl_parallelStreams()];
        for (int i = 0; i < streams.length; i++) {
            streams[i] = new UploadStream(backend.getServer(), backend.getUlURL(), config.getUl_ckSize(), config.getErrorHandlingMode(), config.getUl_connectTimeout(), config.getUl_soTimeout(), config.getUl_recvBuffer(), config.getUl_sendBuffer()) {
                @Override
                public void onError(String err) {
                    abort();
                    onCriticalFailure(err);
                }
            };
            Utils.sleep(config.getUl_streamDelay());
        }
        boolean graceTimeDone = false;
        long startT = System.currentTimeMillis(), bonusT = 0;
        for (; ; ) {
            double t = System.currentTimeMillis() - startT;
            if (!graceTimeDone && t >= config.getUl_graceTime() * 1000) {
                graceTimeDone = true;
                for (UploadStream u : streams) u.resetUploadCounter();
                startT = System.currentTimeMillis();
                continue;
            }
            if (stopASAP || t + bonusT >= config.getTime_ul_max() * 1000) {
                for (UploadStream u : streams) u.stopASAP();
                for (UploadStream u : streams) u.join();
                break;
            }
            if (graceTimeDone) {
                long totUploaded = 0;
                for (UploadStream u : streams) totUploaded += u.getTotalUploaded();
                double speed = totUploaded / ((t < 100 ? 100 : t) / 1000.0);
                if (config.getTime_auto()) {
                    double b = (2.5 * speed) / 100000.0;
                    bonusT += b > 200 ? 200 : b;
                }
                double progress = (t + bonusT) / (double) (config.getTime_ul_max() * 1000);
                speed = (speed * 8 * config.getOverheadCompensationFactor()) / (config.getUseMebibits() ? 1048576.0 : 1000000.0);
                ul = speed;
                onUploadUpdate(ul, progress > 1 ? 1 : progress);
            }
            Utils.sleep(100);
        }
        if (stopASAP) return;
        onUploadUpdate(ul, 1);
    }

    private boolean pingCalled = false;

    private void pingTest() {
        if (pingCalled) return;
        else pingCalled = true;
        final long start = System.currentTimeMillis();
        onPingJitterUpdate(0, 0, 0);
        PingStream ps = new PingStream(backend.getServer(), backend.getPingURL(), config.getCount_ping(), config.getErrorHandlingMode(), config.getPing_connectTimeout(), config.getPing_soTimeout(), config.getPing_recvBuffer(), config.getPing_sendBuffer()) {

            private double minPing = Double.MAX_VALUE, prevPing = -1;
            private int counter = 0;

            @Override
            public void onError(String err) {
                abort();
                onCriticalFailure(err);
            }

            @Override
            public boolean onPong(long ns) {
                counter++;
                double ms = ns / 1000000.0;
                if (ms < minPing) minPing = ms;
                ping = minPing;
                if (prevPing == -1) {
                    jitter = 0;
                } else {
                    double j = Math.abs(ms - prevPing);
                    jitter = j > jitter ? (jitter * 0.3 + j * 0.7) : (jitter * 0.8 + j * 0.2);
                }
                prevPing = ms;
                double progress = counter / (double) config.getCount_ping();
                onPingJitterUpdate(ping, jitter, progress > 1 ? 1 : progress);
                return !stopASAP;
            }

            @Override
            public void onDone() {
            }
        };
        ps.join();
        if (stopASAP) return;
        onPingJitterUpdate(ping, jitter, 1);
    }


    public void abort() {
        if (stopASAP) return;
        stopASAP = true;
    }

    public abstract void onDownloadUpdate(double dl, double progress);

    public abstract void onUploadUpdate(double ul, double progress);

    public abstract void onPingJitterUpdate(double ping, double jitter, double progress);

    public abstract void onEnd();

    public abstract void onCriticalFailure(String err);

}
