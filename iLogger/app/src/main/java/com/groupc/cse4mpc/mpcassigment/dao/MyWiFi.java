package com.groupc.cse4mpc.mpcassigment.dao;

/**
 * Created by junqi on 20/10/15.
 */
public class MyWiFi {
    private long id;
    private String summary;
    private String time;
    private String location;


    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String getSummary() {
        return this.summary;
    }

    public void setSummary(String summary){
        this.summary = summary;
    }

    public String getTime(){
        return this.time;
    }

    public void setTime(String time){
        this.time = time;
    }

    public String getLocation(){
        return this.location;
    }

    public void setLocation(String location){
        this.location = location;
    }
}
