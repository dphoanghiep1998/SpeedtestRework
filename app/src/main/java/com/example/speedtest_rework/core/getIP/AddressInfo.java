package com.example.speedtest_rework.core.getIP;

public class AddressInfo {
    private  String cc;
    private  String country;
    private  int distance;
    private  int force_ping_select;
    private  String host;
    private  int https_functional;
    private  int id;
    private  String lat;
    private  String lon;
    private  String name;
    private  int preferred;
    private  String sponsor;
    private String url;

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

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
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
    }

    public final String getDownloadUrl() {
        return this.url.replace( "upload.php", "random4000x4000.jpg");
    }

    public final String getUploadUrl() {
        return this.url;
    }
}
