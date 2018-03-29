package com.vastcast.vastcast;

import java.net.URL;
import java.util.ArrayList;

public class Collection {
    private String title;
    private String description;
    private URL image;
    private String author;
    private ArrayList<Episode> episodes;
    private URL source;
    private boolean isPodcast;

    public Collection(String title, String description, URL image, String author, ArrayList<Episode> episodes, URL source, boolean isPodcast) {
        this.title = title;
        this.description = description;
        this.image = image;
        this.author = author;
        this.episodes = episodes;
        this.source = source;
        this.isPodcast = isPodcast;
        /*TODO: update episode numbers*/
        /*TODO: set an updatedDate variable*/
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

    public String getAuthor() {
        return author;
    }

    public ArrayList<Episode> getEpisodes() {
        return episodes;
    }

    public URL getSource() {
        return source;
    }

    public boolean getIsPodcast() {
        return isPodcast;
    }

    public void removeEpisode(Episode e) { //used to remove from playlists
        //
    }
}
