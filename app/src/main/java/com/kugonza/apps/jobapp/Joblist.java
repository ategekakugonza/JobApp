package com.kugonza.apps.jobapp;

public class Joblist {
    private String id;
    private String user_id;
    private String title;
    private String details;
    private  String requirements;
    private String date;
    private String time;
    private String image;

    public Joblist() {
    }

    public Joblist(String id, String user_id, String title, String details, String requirements, String date, String time, String image) {
        this.id = id;
        this.user_id = user_id;
        this.title = title;
        this.details = details;
        this.requirements = requirements;
        this.date = date;
        this.time = time;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
