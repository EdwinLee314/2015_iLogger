package com.groupc.cse4mpc.mpcassigment.dao;

/**
 * Created by junqi on 19/10/15.
 */
public class MyLocation {
    private long id;
    private double latitude;
    private double longitude;
    private String address;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setLatitude(double latitude){
        this.latitude = latitude;
    }

    public double getLatitude(){
        return latitude;
    }

    public void setLongitude(double longitude){
        this.longitude = longitude;
    }

    public double getLongitude(){
        return longitude;
    }

    public void setAddress(String address){this.address = address;}

    public String getAddress(){return address;}
}
