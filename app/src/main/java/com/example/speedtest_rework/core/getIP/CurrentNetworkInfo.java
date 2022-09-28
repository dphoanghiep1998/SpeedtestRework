package com.example.speedtest_rework.core.getIP;

import com.example.speedtest_rework.network.NetworkResult;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CurrentNetworkInfo {
    private double selfLat = 0.0;
    private double selfLon = 0.0;
    private String selfIsp = "";
    private String selfIspIp = "";

    public CurrentNetworkInfo(double selfLat, double selfLon, String selfIsp, String selfIspIp) {
        this.selfLat = selfLat;
        this.selfLon = selfLon;
        this.selfIsp = selfIsp;
        this.selfIspIp = selfIspIp;
    }

    public CurrentNetworkInfo() {

    }

    public double getSelfLat() {
        return selfLat;
    }

    public void setSelfLat(double selfLat) {
        this.selfLat = selfLat;
    }

    public double getSelfLon() {
        return selfLon;
    }

    public void setSelfLon(double selfLon) {
        this.selfLon = selfLon;
    }

    public String getSelfIsp() {
        return selfIsp;
    }

    public void setSelfIsp(String selfIsp) {
        this.selfIsp = selfIsp;
    }

    public String getSelfIspIp() {
        return selfIspIp;
    }

    public void setSelfIspIp(String selfIspIp) {
        this.selfIspIp = selfIspIp;
    }

    public CurrentNetworkInfo getCurrentNetWorkInfo() {
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
                    this.selfLat = Double.parseDouble(line.split("lat=\"")[1].split(" ")[0].replace("\"", ""));
                    this.selfLon = Double.parseDouble(line.split("lon=\"")[1].split(" ")[0].replace("\"", ""));
                    this.selfIsp = line.split("isp=\"")[1].split(" ")[0].replace("\"", "");
                    this.selfIspIp = line.split("ip=\"")[1].split(" ")[0].replace("\"", "");
                }
                br.close();

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return this;
    }

    @Override
    public String toString() {
        return "CurrentNetworkInfo{" +
                "selfLat=" + selfLat +
                ", selfLon=" + selfLon +
                ", selfIsp='" + selfIsp + '\'' +
                ", selfIspIp='" + selfIspIp + '\'' +
                '}';
    }
}
