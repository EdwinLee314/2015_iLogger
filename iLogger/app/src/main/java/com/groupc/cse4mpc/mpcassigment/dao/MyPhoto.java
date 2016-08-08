package com.groupc.cse4mpc.mpcassigment.dao;

/**
 * Created by junqi on 21/10/15.
 */
public class MyPhoto {
    private long id;
    private String filepath;
    private String description;
    private String time;
    private String location;


    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String getFilepath(){
        return this.filepath;
    }

    public  void setFilepath(String filepath){
        this.filepath = filepath;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description){
        this.description = description;
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
