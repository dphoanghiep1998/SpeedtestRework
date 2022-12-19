package com.example.speedtest_rework.core.ping;

import android.util.Log;

import com.stealthcopter.networktools.Ping;
import com.stealthcopter.networktools.ping.PingResult;
import com.stealthcopter.networktools.ping.PingStats;

import java.net.URL;

public abstract class PingStream {
    private String server, path;
    private Ping testPing;
    private boolean stopASAP = false;

    public PingStream(String server) {
        this.server = server;
        init();
    }

    private void init() {
        if (stopASAP) return;
        new Thread() {
            public void run() {
                try {
                    URL url = new URL(server);
                    testPing = Ping.onAddress(url.getHost()).setTimes(3).setTimeOutMillis(400).doPing(new Ping.PingListener() {
                        @Override
                        public void onResult(PingResult pingResult) {
                            Log.d("TAG", "onResult: " + pingResult.timeTaken);
                            onPong((long) pingResult.timeTaken);
                        }

                        @Override
                        public void onFinished(PingStats pingStats) {

                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });

                } catch (Exception e) {
                    onError(e.toString());
                }
            }
        }.start();
    }

    public abstract void onError(String err);

    public abstract boolean onPong(long ns);

    public abstract void onDone();

    public void stopASAP() {
        stopASAP = true;
        if (testPing != null) testPing.cancel();
    }


}
