package com.example.hydRate;

import com.google.android.gms.maps.model.LatLng;

public class Fountain {
    public LatLng location;
    public String name;
    public String picID;
    private String ftnID;
    String picPath;

    public String getFtnID() {
        return ftnID;
    }

    public void setFtnID(String ftnID) {
        this.ftnID = ftnID;
    }

    public String getPicPath() {
        return picPath;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }

    public String getPicID() {
        return picID;
    }

    public void setPicID(String picID) {
        this.picID = picID;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation (LatLng location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }
}
