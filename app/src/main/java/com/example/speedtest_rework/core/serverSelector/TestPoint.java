package com.example.speedtest_rework.core.serverSelector;

public class TestPoint {
    private String name, server, dlURL, ulURL, pingURL;
    protected float ping = -1;

    public TestPoint(String name, String server, String dlURL, String ulURL, String pingURL) {
        this.name = name;
        this.server = server;
        this.dlURL = dlURL;
        this.ulURL = ulURL;
        this.pingURL = pingURL;

    }

    public TestPoint() {

    }


    public String getName() {
        return name;
    }

    public String getServer() {
        return server;
    }

    public String getDlURL() {
        return dlURL;
    }

    public String getUlURL() {
        return ulURL;
    }

    public String getPingURL() {
        return pingURL;
    }


    public float getPing() {
        return ping;
    }
}
