package com.mcbridebrandon.popularmovies.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.Serializable;

@Entity (tableName = "movies")
public class Movie implements Serializable {

    private static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/";
    private static final String POSTER_SIZE = "w300";
    private static final String BACKDROP_SIZE = "w780";
    private static final String TAG = "MOVIE CLASS" ;

    private Double voteAverage;
    @PrimaryKey
    @NonNull
    private String id;
    private String title;
    private String releaseDate;
    private String overview;
    private String posterPath;
    private String backdropPath;



    /**
     * No args constructor for use in serialization
     */
    @Ignore
    public Movie() {
    }

    //constructor
    public Movie(String id,Double voteAverage, String title, String releaseDate, String overview, String posterPath, String backdropPath) {
        this.voteAverage = voteAverage;
        //this.setId(id);
        this.id = id;
        this.title = title;
        this.releaseDate = releaseDate;
        this.overview = overview;

        String posterSubstr = posterPath.substring(0,5);
        String backdropSubstr = backdropPath.substring(0,5);
        Log.d(TAG, "#" + posterSubstr);

        //should do it differently maybe?
        if(posterSubstr.equals("https") && backdropSubstr.equals("https"))
        {
            this.posterPath = posterPath;
            this.backdropPath = backdropPath;
        }else {
            this.posterPath = IMAGE_BASE_URL + POSTER_SIZE + posterPath;
            this.backdropPath = IMAGE_BASE_URL + BACKDROP_SIZE + backdropPath;
        }
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPosterPath() {

        return  posterPath;
    }

    public void setPosterPath(String posterUrl) {
        this.posterPath = IMAGE_BASE_URL + POSTER_SIZE + posterUrl;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath =  IMAGE_BASE_URL + BACKDROP_SIZE + backdropPath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
