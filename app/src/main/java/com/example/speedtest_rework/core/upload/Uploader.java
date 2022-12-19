package com.example.speedtest_rework.core.upload;

import com.example.speedtest_rework.core.base.Connection;

import java.io.DataOutputStream;
import java.util.Random;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;


public abstract class Uploader extends Thread {
    private Connection c;
    private String path;
    private boolean stopASAP = false, resetASAP = false;
    private long totUploaded = 0;
    private byte[] garbage;

    public Uploader(Connection c, String path, int ckSize) {
        this.c = c;
        this.path = path;
        Random r = new Random(System.nanoTime());
        r.nextBytes(garbage);
        start();
    }

    byte[] buffer = new byte[150 * 1024];

    public void run() {
        long uploadedKByte = 0;

        while (true) {
            try {
                HttpsURLConnection conn = null;
                conn = (HttpsURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setSSLSocketFactory((SSLSocketFactory) SSLSocketFactory.getDefault());
                conn.setHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });
                conn.connect();
                DataOutputStream dos = new DataOutputStream(conn.getOutputStream());


                dos.write(buffer, 0, buffer.length);
                dos.flush();

                conn.getResponseCode();

                uploadedKByte += buffer.length / 1024.0;
                long endTime = System.currentTimeMillis();
                double uploadElapsedTime = (endTime - startTime) / 1000.0;
                if (uploadElapsedTime >= timeout) {
                    break;
                }

                dos.close();
                conn.disconnect();
            } catch (Exception ex) {
                ex.printStackTrace();
                break;
            }
        }
    }


    public void stopASAP() {
        this.stopASAP = true;
    }

    public abstract void onProgress(long uploaded);

    public abstract void onError(String err);

    public void resetUploadCounter() {
        resetASAP = true;
    }

    public long getUploaded() {
        return resetASAP ? 0 : totUploaded;
    }
}
