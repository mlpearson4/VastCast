package com.vastcast.vastcast;

import java.io.Serializable;
import java.net.URL;

public class Episode implements Serializable {
    private String title;
    private String description;
    private int season;
    private int episode;
    private int duration;
    private URL link;

    public Episode(String title, String description, int season, int episode, int duration, URL link) {
        this.title = title;
        this.description = description;
        this.season = season;
        this.episode = episode;
        this.duration = duration;
        this.link = link;

        /*TODO: Add Date Using pubdate Tag*/
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

    public int getSeason() {
        return season;
    }

    public int getDuration() {
        return duration;
    }

    public String getDurationText() {
        int seconds = duration % 60;
        int minutes = duration / 60;
        return Integer.toString(minutes) + ":" + String.format("%02d", seconds);
    }

    public URL getLink() {
        return link;
    }
}
