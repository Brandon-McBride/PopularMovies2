package com.mcbridebrandon.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mcbridebrandon.popularmovies.adapters.MovieAdapter;
import com.mcbridebrandon.popularmovies.adapters.ReviewAdapter;
import com.mcbridebrandon.popularmovies.adapters.TrailerAdapter;
import com.mcbridebrandon.popularmovies.data.AppDatabase;
import com.mcbridebrandon.popularmovies.data.AppExecutors;
import com.mcbridebrandon.popularmovies.model.Movie;
import com.mcbridebrandon.popularmovies.model.Review;
import com.mcbridebrandon.popularmovies.model.Trailer;
import com.mcbridebrandon.popularmovies.utilities.JsonUtils;
import com.mcbridebrandon.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;

public class DetailsActivity extends AppCompatActivity implements TrailerAdapter.ItemClickListener, ReviewAdapter.ItemClickListener {
    private static final String TAG =  DetailsActivity.class.getSimpleName();
    private static final String ADDFAVORITE = "Add Favorite";
    private static final String REMOVEFAVORITE = "Remove Favorite";
    private TrailerAdapter mAdapter;
    private Trailer[] mTrailerData;
    private ReviewAdapter mReviewAdapter;
    private Review[] mReviewData;
    private Movie mMovie;
    private Boolean isFavorite = false;
    Button favoriteButton;

    private AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        setTitle("Movie Details");

        //initialize db
        mDb = AppDatabase.getsInstance(getApplicationContext());

        //get movie from main
        Intent intent = getIntent();
        mMovie = (Movie) intent.getSerializableExtra(getString(R.string.movie));

        //set up views
        ImageView ivPoster = findViewById(R.id.iv_poster);
        ImageView ivBackdrop = findViewById(R.id.iv_backdrop);
        TextView tvOriginalTitle = findViewById(R.id.tv_original_title);
        TextView tvOverview = findViewById(R.id.tv_overview);
        TextView tvVoteAverage = findViewById(R.id.tv_vote_average);
        TextView tvReleaseDate = findViewById(R.id.tv_release_date);


        if (mMovie != null) {
            Picasso.get()
                    .load((mMovie.getPosterPath()))
                    .fit()
                    .into(ivPoster);
            Picasso.get()
                    .load((mMovie.getBackdropPath()))
                    .fit()
                    .into(ivBackdrop);

            tvOriginalTitle.setText(mMovie.getTitle());
            tvOverview.setText(mMovie.getOverview());
            tvReleaseDate.setText(mMovie.getReleaseDate());
            tvVoteAverage.setText(String.format(Locale.US,"%.2f", mMovie.getVoteAverage()));

        }


        if (isNetworkAvailable()) {
            // set up the Traielr RecyclerView
            RecyclerView mRecyclerView = findViewById(R.id.rv_trailer_grid);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false));
            mAdapter = new TrailerAdapter(this, mTrailerData, this);
            //adapter.setClickListener(this);
            mRecyclerView.setAdapter(mAdapter);

            //query tmdb
            makeTrailerSearchQuery(mMovie.getId());

            // set up the Review RecyclerView
            RecyclerView mReviewRecyclerView = findViewById(R.id.rv_review_list);
            mReviewRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            mReviewAdapter = new ReviewAdapter(this, mReviewData, this);
            //adapter.setClickListener(this);
            mReviewRecyclerView.setAdapter(mReviewAdapter);

            //query tmdb
            makeReviewSearchQuery(mMovie.getId());
        } else {
            TextView error = findViewById(R.id.tv_no_connection);
            error.setVisibility(View.VISIBLE);
            error.setText(R.string.no_connection);
        }



        //favorite button
        favoriteButton = findViewById(R.id.btn_favorite);
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                Movie movie = mDb.movieDao().queryMovieById(mMovie.getId());
                isFavorite = movie != null;
                Log.d(TAG, "!!!!!!" + (movie != null));
                if(isFavorite)
                {
                    favoriteButton.setText(REMOVEFAVORITE);
                }
            }
        });



        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               setAsFavorite();
            }
        });

    }

    private void setAsFavorite() {


        if(favoriteButton.getText().equals(ADDFAVORITE))
        {
            insertMovie();
            favoriteButton.setText(REMOVEFAVORITE);
        }else
        {
            deleteMovie();
            favoriteButton.setText(ADDFAVORITE);
        }
    }
    private void insertMovie(){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDb.movieDao().insertMovie(mMovie);
            }
        });
    }
    private void deleteMovie(){
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                        mDb.movieDao().deleteMovie(mMovie);
                    }
                });
    }
    private void makeTrailerSearchQuery(String id) {
        URL searchUrl = NetworkUtils.buildTrailerUrl(id);
        // Create a new MovieQueryTask and call its execute method, passing in the url to query
        new TrailerQueryTask().execute(searchUrl);
    }
    private void makeReviewSearchQuery(String id) {
        URL searchUrl = NetworkUtils.buildReviewUrl(id);
        // Create a new MovieQueryTask and call its execute method, passing in the url to query
        new ReviewQueryTask().execute(searchUrl);
    }
    @Override
    public void onReviewItemClick(int position) {
        String URL = mReviewData[position].getUrl();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public void onTrailerItemClick(int position) {
        String URL = mTrailerData[position].getVideoUrl();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }

    }

    private class TrailerQueryTask extends AsyncTask<URL, Void, String> {
        //private Movie[] mMovieData;
        //Override the doInBackground method to perform the query. Return the results.
        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String searchResults = null;
            try {
                searchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return searchResults;
        }

        // Override onPostExecute to display the results in the TextView
        @Override
        protected void onPostExecute(String searchResults) {
            if (searchResults != null && !searchResults.equals("")) {
                mTrailerData = JsonUtils.parseTrailerJson(searchResults);
                mAdapter.updateAdapter(mTrailerData);
                mAdapter.notifyDataSetChanged();
            }
        }
    }
    private class ReviewQueryTask extends AsyncTask<URL, Void, String> {
        //private Movie[] mMovieData;
        //Override the doInBackground method to perform the query. Return the results.
        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String searchResults = null;
            try {
                searchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return searchResults;
        }

        // Override onPostExecute to display the results in the TextView
        @Override
        protected void onPostExecute(String searchResults) {
            if (searchResults != null && !searchResults.equals("")) {
                mReviewData = JsonUtils.parseReviewJson(searchResults);
                mReviewAdapter.updateAdapter(mReviewData);
                mReviewAdapter.notifyDataSetChanged();
            }
        }
    }

    //Reference https://stackoverflow.com/questions/4238921/detect-whether-there-is-an-internet-connection-available-on-android
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
