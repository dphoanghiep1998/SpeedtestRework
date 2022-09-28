package com.example.speedtest_rework.core.getIP;

public class AddressInfo {
    private String cc;
    private String country;
    private int distance;
    private int force_ping_select = 0;
    private String host;
    private int https_functional;
    private int id;
    private double lat;
    private double lon;
    private String name;
    private int preferred;
    private String sponsor;
    private String url;

    public AddressInfo(String cc, String country, int distance, int force_ping_select, String host,
                       int https_functional, int id, double lat, double lon, String name, int preferred, String sponsor, String url) {
        this.cc = cc;
        this.country = country;
        this.distance = distance;
        this.force_ping_select = force_ping_select;
        this.host = host;
        this.https_functional = https_functional;
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.name = name;
        this.preferred = preferred;
        this.sponsor = sponsor;
        this.url = url;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getForce_ping_select() {
        return force_ping_select;
    }

    public void setForce_ping_select(int force_ping_select) {
        this.force_ping_select = force_ping_select;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getHttps_functional() {
        return https_functional;
    }

    public void setHttps_functional(int https_functional) {
        this.https_functional = https_functional;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPreferred() {
        return preferred;
    }

    public void setPreferred(int preferred) {
        this.preferred = preferred;
    }

    public String getSponsor() {
        return sponsor;
    }

    public void setSponsor(String sponsor) {
        this.sponsor = sponsor;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public final String getPingUrl() {
        String pingUrl = this.url;
        return pingUrl.replace("upload.php", "random4000x4000.jpg");
    }

    public final String getDownloadUrl() {
        String dlUrl = this.url;
        return dlUrl.replace("upload.php", "random4000x4000.jpg");
    }

    public String toString() {
        return "AddressInfo(url=" + this.url + ", lat=" + this.lat + ", lon=" + this.lon + ", distance=" + this.distance + ", name=" + this.name + ", country=" + this.country + ", cc=" + this.cc + ", sponsor=" + this.sponsor + ", id=" + this.id + ", preferred=" + this.preferred + ", https_functional=" + this.https_functional + ", host=" + this.host + ", force_ping_select=" + this.force_ping_select + ')';
    }

    public final String getUploadUrl() {
        return this.url;
    }
}
