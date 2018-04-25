package com.vastcast.vastcast;

import java.io.Serializable;
import java.net.URL;

public class Episode implements Serializable {
    private String title;
    private String description;
    private int duration;
    private String link;

    public Episode(){

    }
    public Episode(String title, String description, int duration, String link) {
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title=title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String descript){
        this.description=descript;
    }
    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration){
        this.duration=duration;
    }
    public String getDurationText() {
        int seconds = duration % 60;
        int minutes = duration / 60;
        return Integer.toString(minutes) + ":" + String.format("%02d", seconds);
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link){
        this.link=link;
    }

    public URL makeLink(){
        try {
            return new URL(this.link);
        }
        catch (Exception e){
            return null;
        }
    }
}
