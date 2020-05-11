package com.mp3Player.model;

public class Song {
    private String title;
    private String url;
    private int duration;

    public Song(String title, String url, int duration) {
        this.title = title;
        this.url = url;
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;

    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
