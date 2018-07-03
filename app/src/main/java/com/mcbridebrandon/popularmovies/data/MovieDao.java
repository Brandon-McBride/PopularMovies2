package com.mcbridebrandon.popularmovies.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.mcbridebrandon.popularmovies.model.Movie;

import java.util.List;

@Dao
public interface MovieDao {
    @Query("SELECT * FROM movies ORDER BY releaseDate")
    LiveData<List<Movie>> loadAllMovies();


    @Query("SELECT * FROM movies WHERE id LIKE :mId")
    Movie queryMovieById(String mId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovie(Movie movie);

    @Delete
    void deleteMovie(Movie movie);
}
