package com.oybek.bridgevk.Entities;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TramStopInfo {
    @SerializedName("tramStopName")
    private String tramStopName;

    @SerializedName("latitude")
    private double latitude;

    @SerializedName("longitude")
    private double longitude;

    @SerializedName("tramInfoList")
    private List<TramInfo> tramInfoList;

    public String getTramStopName() {
        return tramStopName;
    }

    public void setTramStopName(String tramStopName) {
        this.tramStopName = tramStopName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public List<TramInfo> getTramInfoList() {
        return tramInfoList;
    }

    public void setTramInfoList(List<TramInfo> tramInfoList) {
        this.tramInfoList = tramInfoList;
    }

    @Override
    public String toString() {
        return "TramStopInfo{" +
                "tramStopName='" + tramStopName + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", tramInfoList=" + tramInfoList +
                '}';
    }
}