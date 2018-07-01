package com.mcbridebrandon.popularmovies.utilities;


import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * These utilities will be used to make a network call to The Movie Database api https://developers.themoviedb.org/.
 */
public class NetworkUtils {
    private static final String TAG = "netutils";
    //Base url for api
    private final static String TMDB_BASE_URL = "https://api.themoviedb.org/3/movie/";

    //param for api key
    private final static String PARAM_API_KEY = "api_key";
    //insert your api key here
    private final static String API_KEY = "PLACE API KEY HERE";

    //get popular movies
    private final static String POPULAR = "popular";

    //get top rated movies
    private final static String TOP_RATED = "top_rated";

    //get trailers
    private final static String VIDEOS = "videos";

    //get reviews
    private final static String REVIEWS = "reviews";

    /**
     * Builds the URL used to query The Movie DB to get Movies.
     */
    public static URL buildMovieUrl(String tmdbQuery) {

        Uri builtUri = null;

        //check if tmbdQuery is null or if not use it
        if (tmdbQuery != null) {
            if (tmdbQuery.equals("popular")) {
                //tmdbQuery would be sort by popular or top rated
                builtUri = Uri.parse(TMDB_BASE_URL).buildUpon()
                        .appendPath(POPULAR)
                        .appendQueryParameter(PARAM_API_KEY, API_KEY)
                        .build();
            } else if (tmdbQuery.equals("topRated")) {
                //tmdbQuery would be sort by popular or top rated
                builtUri = Uri.parse(TMDB_BASE_URL).buildUpon()
                        .appendPath(TOP_RATED)
                        .appendQueryParameter(PARAM_API_KEY, API_KEY)
                        .build();
            }
        } else {

            //default
            builtUri = Uri.parse(TMDB_BASE_URL).buildUpon()
                    .appendQueryParameter(PARAM_API_KEY, API_KEY)
                    .build();
        }
        URL url = null;
        try {
            if (builtUri != null) {
                url = new URL(builtUri.toString());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }
    public static URL buildTrailerUrl(String id) {

        Uri builtUri = null;

        //check if tmbdQuery is null or if not use it
        if (id != null) {
                //
                builtUri = Uri.parse(TMDB_BASE_URL).buildUpon()
                        .appendPath(id)
                        .appendPath(VIDEOS)
                        .appendQueryParameter(PARAM_API_KEY, API_KEY)
                        .build();
        } else {

         //null id error
        }
        URL url = null;
        try {
            if (builtUri != null) {
                url = new URL(builtUri.toString());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "#" + url);

        return url;
}
    public static URL buildReviewUrl(String id) {

        Uri builtUri = null;

        //check if tmbdQuery is null or if not use it
        if (id != null) {
                //tmdbQuery would be sort by popular or top rated
                builtUri = Uri.parse(TMDB_BASE_URL).buildUpon()
                        .appendPath(id)
                        .appendPath(REVIEWS)
                        .appendQueryParameter(PARAM_API_KEY, API_KEY)
                        .build();
        } else {
        //null id error
        }
        URL url = null;
        try {
            if (builtUri != null) {
                url = new URL(builtUri.toString());
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }
    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}

