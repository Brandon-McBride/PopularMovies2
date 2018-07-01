package com.mcbridebrandon.popularmovies.model;

public class Trailer {

    private static final String TRAILER_IMAGE_BASE_URL = "https://img.youtube.com/vi/";
    private static final String TRAILER_IMAGE_QUALITY = "/mqdefault.jpg";
    private static final String TRAILER_VIDEO_BASE_URL = "http://www.youtube.com/watch?v=";

    private String id;
    private String key;
    private String name;
    private String site;
    private String size;
    private String posterPath;
    private String videoUrl;





    /**
     * No args constructor for use in serialization
     */
    public Trailer() {
    }

    public Trailer(String id, String key, String name, String site, String size){
        this.id = id;
        this.key = key;
        this.name = name;
        this.site = site;
        this.size = size;
        this.posterPath = TRAILER_IMAGE_BASE_URL + this.key + TRAILER_IMAGE_QUALITY;
        this.videoUrl = TRAILER_VIDEO_BASE_URL + this.key;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = TRAILER_IMAGE_BASE_URL + this.key + TRAILER_IMAGE_QUALITY;
    }
    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
