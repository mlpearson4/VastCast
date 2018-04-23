package com.vastcast.vastcast;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;

public class Collection implements Serializable {
    private String title;
    private String description;
    private String image;
    private String author;
    private ArrayList<Episode> episodes;
    private String source;

    public Collection(String title, String description, String image, String author, ArrayList<Episode> episodes, String source) {
        this.title = title;
        this.description = description;
        this.image = image;
        this.author = author;
        this.episodes = episodes;
        this.source = source;
    }
    public Collection(){

    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title){
        this.title= title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String descript){
        this.description=descript;
    }
    public String getImage() {
        return image;
    }
    public void setImage(String image){
        this.image=image.toString();
    }
    public URL makeImage() {
       try {
           return new URL(this.image);
       }
       catch (Exception e){
           return null;
        }
    }
    public ArrayList<Episode> getEpisodes() {
        return episodes;
    }
    public void setEpisodes(ArrayList<Episode> e){
        this.episodes=e;
    }
    public String getSource() {
        return source;
    }
    public void setSource(String source){
        this.source=source.toString();
    }
    public URL makeSource() {
        try {
            return new URL(this.source);
        }
        catch (Exception e){
            return null;
        }
    }
}
