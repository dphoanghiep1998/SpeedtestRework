package com.example.speedtest_rework.core;


import com.example.speedtest_rework.core.config.SpeedtestConfig;
import com.example.speedtest_rework.core.serverSelector.TestPoint;
import com.example.speedtest_rework.core.worker.SpeedtestWorker;

public class Speedtest {
    private TestPoint server = null;
    private SpeedtestConfig config = new SpeedtestConfig();
    private int state = 0; //0=configs, 1=test points, 2=server selection, 3=ready, 4=testing, 5=finished

    private Object mutex = new Object();


    public void addTestPoint(TestPoint t) {
        synchronized (mutex) {
            if (state == 0)
                state = 1;
//            if (state > 1)
//                throw new IllegalStateException("Cannot add test points at this moment");
            server = t;
            state = 3;
        }
    }


    private SpeedtestWorker st = null;

    public void start(final SpeedtestHandler callback) {
        synchronized (mutex) {
            if (state < 3) throw new IllegalStateException("Server hasn't been selected yet");
            if (state == 4) throw new IllegalStateException("Test already running");
            state = 4;

            st = new SpeedtestWorker(server, config) {
                @Override
                public void onDownloadUpdate(double dl, double progress) {
                    callback.onDownloadUpdate(dl, progress);
                }

                @Override
                public void onUploadUpdate(double ul, double progress) {
                    callback.onUploadUpdate(ul, progress);
                }

                @Override
                public void onPingJitterUpdate(double ping, double jitter, double progress) {
                    callback.onPingJitterUpdate(ping, jitter, progress);
                }


                @Override
                public void onEnd() {
                    synchronized (mutex) {
                        state = 5;
                    }
                    callback.onEnd();
                }

                @Override
                public void onCriticalFailure(String err) {
                    synchronized (mutex) {
                        state = 5;
                    }
                    callback.onCriticalFailure(err);
                }
            };
        }
    }


    public void abort() {
        synchronized (mutex) {
            state = 5;
        }
        if (st != null) {
            st.abort();
        }
    }

    public static abstract class SpeedtestHandler {
        public abstract void onDownloadUpdate(double dl, double progress);

        public abstract void onUploadUpdate(double ul, double progress);

        public abstract void onPingJitterUpdate(double ping, double jitter, double progress);

        public abstract void onEnd();


        public abstract void onCriticalFailure(String err);
    }
}
