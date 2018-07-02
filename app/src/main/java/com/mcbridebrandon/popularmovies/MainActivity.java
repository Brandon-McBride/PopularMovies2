package com.mcbridebrandon.popularmovies;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mcbridebrandon.popularmovies.adapters.MovieAdapter;
import com.mcbridebrandon.popularmovies.data.AppDatabase;
import com.mcbridebrandon.popularmovies.model.MainViewModel;
import com.mcbridebrandon.popularmovies.model.Movie;
import com.mcbridebrandon.popularmovies.utilities.JsonUtils;
import com.mcbridebrandon.popularmovies.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.List;
//test
public class MainActivity extends AppCompatActivity implements MovieAdapter.ItemClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private MovieAdapter mAdapter;
    private List<Movie> mMovieData;
    private AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (isNetworkAvailable()) {
            // Set up the RecyclerView for displaying the list of movies in a grid
            RecyclerView mRecyclerView = findViewById(R.id.rv_movie_grid);
            int numberOfColumns = 2;
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
            mAdapter = new MovieAdapter(this, mMovieData, this);
            //adapter.setClickListener(this);
            mRecyclerView.setAdapter(mAdapter);

            //Query The Movie Database using popular as the default sorting
            makeMovieSearchQuery("popular");
        } else {
            //If there is no connection display an error in a textview
            TextView error = findViewById(R.id.tv_no_connection);
            error.setVisibility(View.VISIBLE);
            error.setText(R.string.no_connection);
        }

        //get database instance
        mDb = AppDatabase.getsInstance(getApplicationContext());
    }


    private void makeMovieSearchQuery(String sortParam) {
        URL searchUrl = NetworkUtils.buildMovieUrl(sortParam);
        // Create a new MovieQueryTask and call its execute method, passing in the url to query
        new MovieQueryTask().execute(searchUrl);
    }

    @Override
    public void onItemClick(int position) {
        // need to launch movie detail activity
        launchDetailsActivity(position);
    }

    // COMPLETED (1) Create a class called GithubQueryTask that extends AsyncTask<URL, Void, String>
    private class MovieQueryTask extends AsyncTask<URL, Void, String> {
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
                mMovieData = JsonUtils.parseMovieJson(searchResults);
                mAdapter.updateAdapter(mMovieData);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        if(isNetworkAvailable()) {
            //show menu for sorting
            MenuItem item = menu.findItem(R.id.menu_sort_by);
            item.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();
        if (itemThatWasClickedId == R.id.menu_popular) {
            Context context = MainActivity.this;
            makeMovieSearchQuery(getString(R.string.popular));
            String textToShow = getString(R.string.popular_toast);
            Toast.makeText(context, textToShow, Toast.LENGTH_SHORT).show();

            return true;
        } else if (itemThatWasClickedId == R.id.menu_top_rated) {
            Context context = MainActivity.this;

            makeMovieSearchQuery(getString(R.string.topRated));
            String textToShow = getString(R.string.top_rated_toast);
            Toast.makeText(context, textToShow, Toast.LENGTH_SHORT).show();

            return true;
        }else if (itemThatWasClickedId == R.id.menu_favorites) {

            //get favorite movies list
            getFavoriteMovies();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getFavoriteMovies() {
            MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
            viewModel.getMovies().observe(this, new Observer<List<Movie>>() {
                @Override
                public void onChanged(@Nullable List<Movie> movieEntries) {
                    Log.d(TAG, "Updating list of tasks from LiveData in ViewModel");
                    mAdapter.updateAdapter(movieEntries);
                    mAdapter.notifyDataSetChanged();
                }
            });
        }


    private void launchDetailsActivity(int position) {
        Movie movieToSend = this.mMovieData.get(position);//new Movie();
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra(getString(R.string.movie), movieToSend);
        startActivity(intent);
    }

    //Reference https://stackoverflow.com/questions/4238921/detect-whether-there-is-an-internet-connection-available-on-android
    //Method to check if a network conenction is available
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
