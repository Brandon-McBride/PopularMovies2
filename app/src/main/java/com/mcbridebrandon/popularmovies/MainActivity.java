package com.mcbridebrandon.popularmovies;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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

    //Constants
    private final static String POPULAR_MOVIES = "Popular";
    private final static String TOP_RATED_MOVIES = "Top Rated";
    private final static String FAVORITE_MOVIES = "Favorite";
    //Global variables
    private MovieAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private List<Movie> mMovieData;
    private MovieAdapter favoriteAdapter;
    private AppDatabase mDb;
    private boolean isFavorite = false;
    //variable to keep track which sort the user is currently on
    private final static String KEY_LIST_STATE = "list_state";
    private final static String KEY_LAST_SCREEN = "last_screen";
    private Parcelable rvState;
    private String lastScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Stetho.initializeWithDefaults(this);
        //get database instance
        mDb = AppDatabase.getsInstance(getApplicationContext());
        if (isNetworkAvailable()) {
            // Set up the RecyclerView for displaying the list of movies in a grid
            mRecyclerView = findViewById(R.id.rv_movie_grid);
            int numberOfColumns = 2;
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
            mAdapter = new MovieAdapter(this, mMovieData, this);
            //adapter.setClickListener(this);
            mRecyclerView.setAdapter(mAdapter);


            if(savedInstanceState != null) {
            rvState = savedInstanceState.getParcelable(KEY_LIST_STATE);
            lastScreen = savedInstanceState.getString(KEY_LAST_SCREEN);
            if (lastScreen != null) {
                switch ((lastScreen)) {
                    case POPULAR_MOVIES:
                        setTitle(POPULAR_MOVIES);
                        makeMovieSearchQuery(getString(R.string.popular));
                        break;
                    case TOP_RATED_MOVIES:
                        makeMovieSearchQuery(getString(R.string.topRated));
                        setTitle(TOP_RATED_MOVIES);
                        break;
                    case FAVORITE_MOVIES:
                        isFavorite = true;
                        setTitle(FAVORITE_MOVIES);
                        getFavoriteMovies();
                        break;
                }
            }
        }else
        {
            //Query The Movie Database using popular as the default sorting
            setTitle(POPULAR_MOVIES);
            makeMovieSearchQuery(getString(R.string.popular));
        }

        } else {
            //If there is no connection display an error in a textview
            TextView error = findViewById(R.id.tv_no_connection);
            error.setVisibility(View.VISIBLE);
            error.setText(R.string.no_connection);
        }
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
                mRecyclerView.getLayoutManager().onRestoreInstanceState(rvState);
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

        //Get context for showing a toast message
        Context context = MainActivity.this;
        //Tost message to show user
        String textToShow;

        switch (item.getItemId()) {
            case R.id.menu_popular:
                isFavorite = false;
                lastScreen = POPULAR_MOVIES;
                setTitle(POPULAR_MOVIES);
                makeMovieSearchQuery(getString(R.string.popular));
                textToShow = getString(R.string.popular_toast);
                Toast.makeText(context, textToShow, Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_top_rated:
                isFavorite = false;
                lastScreen = TOP_RATED_MOVIES;
                setTitle(TOP_RATED_MOVIES);
                makeMovieSearchQuery(getString(R.string.topRated));
                textToShow = getString(R.string.top_rated_toast);
                Toast.makeText(context, textToShow, Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_favorites:
                isFavorite = true;
                lastScreen = FAVORITE_MOVIES;
                setTitle(FAVORITE_MOVIES);
                //get favorite movies list
                getFavoriteMovies();
                textToShow = getString(R.string.favorite_toast);
                Toast.makeText(context, textToShow, Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getFavoriteMovies() {
            MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
            viewModel.getMovies().observe(this, new Observer<List<Movie>>() {
                @Override
                public void onChanged(@Nullable List<Movie> movieEntries) {
                   if(isFavorite){
                       mAdapter.updateAdapter(movieEntries);
                   }
                }
            });
        }

    private void launchDetailsActivity(int position) {
       Movie movieToSend;
        if (isFavorite) {
            MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
            LiveData<List<Movie>> movies = viewModel.getMovies();
            movieToSend = movies.getValue().get(position);//new Movie();
        }else{
            movieToSend = this.mMovieData.get(position);
        }
        lastScreen = getTitle().toString();
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //get the recyclerview state
        rvState = mRecyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(KEY_LIST_STATE, rvState);
        //get the last sort/screen the app was on
        outState.putString(KEY_LAST_SCREEN,String.valueOf(getTitle()) );
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //Retrieve previous state
        if (savedInstanceState != null){
            //recyclerview state
            rvState = savedInstanceState.getParcelable(KEY_LIST_STATE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (rvState != null){
            mRecyclerView.getLayoutManager().onRestoreInstanceState(rvState);
        }
    }
}
