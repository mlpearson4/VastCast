package com.vastcast.vastcast;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;

public class Collection implements Serializable {
    private String title;
    private String description;
    private URL image;
    private String author;
    private ArrayList<Episode> episodes;
    private URL source;

    public Collection(String title, String description, URL image, String author, ArrayList<Episode> episodes, URL source) {
        this.title = title;
        this.description = description;
        this.image = image;
        this.author = author;
        this.episodes = episodes;
        this.source = source;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public URL getImage() {
        return image;
    }

    public ArrayList<Episode> getEpisodes() {
        return episodes;
    }

    public URL getSource() {
        return source;
    }
}
