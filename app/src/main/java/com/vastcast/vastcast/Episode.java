package com.vastcast.vastcast;

import java.io.Serializable;
import java.net.URL;

public class Episode implements Serializable /*TODO: investigate alternatives*/ {
    private String title;
    private String description;
    private int season;
    private int episode;
    private int duration;
    private URL link;
    //Date using pubDate tag private Date date;

    public Episode(String title, String description, int season, int episode, int duration, URL link) {
        this.title = title;
        this.description = description;
        this.season = season;
        this.episode = episode;
        this.duration = duration;
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getEpisodeNumber() {
        return episode;
    }

    public int getDuration() {
        return duration;
    }

    public URL getLink() {
        return link;
    }
}
