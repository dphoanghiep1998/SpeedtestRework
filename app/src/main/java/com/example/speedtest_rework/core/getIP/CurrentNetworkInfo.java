package com.example.speedtest_rework.core.getIP;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CurrentNetworkInfo extends Thread{
    double selfLat = 0.0;
    double selfLon = 0.0;
    String selfIsp = "";
    String selfIspIp = "";
    boolean finished = false;

    @Override
    public void run() {
        try {
            URL url = new URL("https://www.speedtest.net/speedtest-config.php");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);
            int code = urlConnection.getResponseCode();
            if (code == 200) {
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(
                                urlConnection.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {
                    if (!line.contains("isp=")) {
                        continue;
                    }
                    selfLat = Double.parseDouble(line.split("lat=\"")[1].split(" ")[0].replace("\"", ""));
                    selfLon = Double.parseDouble(line.split("lon=\"")[1].split(" ")[0].replace("\"", ""));
                    selfIsp = line.split("isp=\"")[1].split(" ")[0].replace("\"", "");
                    selfIspIp = line.split("ip=\"")[1].split(" ")[0].replace("\"", "");
                    break;
                }
                br.close();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
    }
}
