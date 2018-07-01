package com.mcbridebrandon.popularmovies.model;

public class Review {

    private String id;
    private String author;
    private String content;
    private String url;
    private String size;



    /**
     * No args constructor for use in serialization
     */
    public Review() {
    }

    public Review(String id, String author, String content, String url, String size){
        this.id = id;
        this.author = author;
        this.content = content;
        this.url = url;
        this.size = size;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
