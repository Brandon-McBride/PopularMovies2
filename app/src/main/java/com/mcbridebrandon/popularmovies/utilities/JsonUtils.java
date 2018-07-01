package com.mcbridebrandon.popularmovies.utilities;


import android.nfc.Tag;
import android.util.Log;

import com.mcbridebrandon.popularmovies.model.Movie;
import com.mcbridebrandon.popularmovies.model.Review;
import com.mcbridebrandon.popularmovies.model.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonUtils {

    //Movie
    //private static final String KEY_PAGE_NUMBER = "page";
    //private static final String KEY_TOTAL_RESULTS = "total_results";
    //private static final String KEY_TOTAL_PAGES = "total_pages";
    private static final String KEY_RESULTS = "results";
    private static final String KEY_ID = "id";
    private static final String KEY_ORIGINAL_TITLE = "original_title";
    private static final String KEY_POSTER_PATH = "poster_path";
    private static final String KEY_BACKDROP_PATH = "backdrop_path";
    private static final String KEY_OVERVIEW = "overview";
    private static final String KEY_VOTE_AVERAGE = "vote_average";
    private static final String KEY_RELEASE_DATE = "release_date";

    //Trailer
    private static final String KEY_TRAILER_ID = "id";
    private static final String KEY_TRAILER_KEY = "key";
    private static final String KEY_TRAILER_NAME = "name";
    private static final String KEY_TRAILER_SITE = "site";
    private static final String KEY_TRAILER_SIZE = "size";
    private static final String KEY_TRAILER_RESULTS = "results";

    //Review
    private static final String KEY_REVIEW_ID = "id";
    private static final String KEY_REVIEW_AUTHOR = "author";
    private static final String KEY_REVIEW_CONTENT= "content";
    private static final String KEY_REVIEW_URL = "url";
    private static final String KEY_REVIEW_SIZE = "size";
    private static final String KEY_REVIEW_RESULTS = "results";
    private static final String TAG = JsonUtils.class.getSimpleName();

    public static List<Movie> parseMovieJson(String json) {


        if (json != null) {
            try {
                //initialize the json object from the json string
                JSONObject movieDetails = new JSONObject(json);

                // Getting JSON Array node
                JSONArray results = movieDetails.getJSONArray(KEY_RESULTS);

                //create an array list to hold the movie results in
                List<Movie> moviesArray = new ArrayList<>();

                //Need to get ALL Movie Results
                for(int i=0;i<results.length();i++) {
                    JSONObject movieObject = results.getJSONObject(i);
                    moviesArray.add(createMovieObject(movieObject));                }

                return moviesArray;

            } catch (JSONException e) {
                Log.e(TAG,"Error in parsing movie json");
            }
        }
        return null;
    }
    public static Trailer[] parseTrailerJson(String json) {


        if (json != null) {
            try {
                //initialize the json object from the json string
                JSONObject trailerDetails = new JSONObject(json);

                // Getting JSON Array node
                JSONArray results = trailerDetails.getJSONArray(KEY_TRAILER_RESULTS);

                //create an array to hold the movie results in
                Trailer[] trailerArray = new Trailer[results.length()];

                //Need to get ALL Movie Results
                for(int i=0;i<results.length();i++) {
                    JSONObject trailerObject = results.getJSONObject(i);
                    trailerArray[i] = createTrailerObject(trailerObject);
                }

                return trailerArray;

            } catch (JSONException e) {
                Log.e("JsonUtils", "Error in Json Parsing", e);
            }
        }
        return null;
    }
    public static Review[] parseReviewJson(String json) {


        if (json != null) {
            try {
                //initialize the json object from the json string
                JSONObject reviewDetails = new JSONObject(json);

                // Getting JSON Array node
                JSONArray results = reviewDetails.getJSONArray(KEY_RESULTS);

                //create an array to hold the movie results in
                Review[] reviewsArray = new Review[results.length()];

                //Need to get ALL Movie Results
                for(int i=0;i<results.length();i++) {
                    JSONObject reviewObject = results.getJSONObject(i);
                    reviewsArray[i] = createReviewObject(reviewObject);
                }

                return reviewsArray;

            } catch (JSONException e) {
                Log.e("JsonUtils", "Error in Json Parsing", e);
            }
        }
        return null;
    }
    private static Movie createMovieObject(JSONObject movieObject) {

        //get movie id
        String id = movieObject.optString(KEY_ID);

        //get movie title
        String title = movieObject.optString(KEY_ORIGINAL_TITLE);

        //release date
        String releaseDate = movieObject.optString(KEY_RELEASE_DATE);


        //movie poster path
        String posterPath = movieObject.optString(KEY_POSTER_PATH);

        //vote average
        Double voteAverage = movieObject.optDouble(KEY_VOTE_AVERAGE);

        //get the plot synopsis or movie overview
        String movieOverview = movieObject.optString(KEY_OVERVIEW);

        //get the backdrop path
        String backdropPath = movieObject.optString(KEY_BACKDROP_PATH);

        //create a new movie

        return new Movie(id,voteAverage,  title, releaseDate, movieOverview, posterPath, backdropPath);
    }

    private  static Trailer createTrailerObject(JSONObject trailerObject){
        //
        String id = trailerObject.optString(KEY_TRAILER_ID);

        //
        String key = trailerObject.optString(KEY_TRAILER_KEY);

        //
        String name = trailerObject.optString(KEY_TRAILER_NAME);


        //
        String site = trailerObject.optString(KEY_TRAILER_SITE);

        //
        String size = trailerObject.optString(KEY_TRAILER_SIZE);


        //create a new trailer

        return new Trailer(id,key,name,site,size);
    }

    private static Review createReviewObject(JSONObject reviewObject){
        //
        String id = reviewObject.optString(KEY_REVIEW_ID);

        //
        String author = reviewObject.optString(KEY_REVIEW_AUTHOR);

        //
        String content = reviewObject.optString(KEY_REVIEW_CONTENT);


        //
        String url = reviewObject.optString(KEY_REVIEW_URL);

        //
        String size = reviewObject.optString(KEY_REVIEW_SIZE);


        //create a new trailer

        return new Review(id,author,content,url,size);
    }
}