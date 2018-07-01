package com.mcbridebrandon.popularmovies.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mcbridebrandon.popularmovies.R;
import com.mcbridebrandon.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;


public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder>{

    private List<Movie> mMovieList;
    private final ItemClickListener mClickListener;


    public MovieAdapter(Context context, List<Movie> itemList, ItemClickListener clickListener) {
        this.mMovieList = itemList;
        this.mClickListener = clickListener;
    }


    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);

        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        //Log.d(TAG, "#" + position);

        if (mMovieList != null) {
            Picasso.get()
                    .load((mMovieList.get(position).getPosterPath()))
                    .fit()
                    .into(holder.movieImageView);
            //Log.d(TAG, "#" + mMovieList[position].getPosterPath());
        }
    }

    @Override
    public int getItemCount() {
        if(mMovieList != null) {
            return this.mMovieList.size();
        }else{
            return 1;
        }
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView movieImageView;

        MovieViewHolder(View itemView) {
            super(itemView);
            movieImageView = itemView.findViewById(R.id.iv_movie_item);

            //set onclick
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mClickListener.onItemClick(getAdapterPosition());
        }
    }

    //click listener for movie poster
    public interface ItemClickListener {
        void onItemClick(int position);
    }



    public void updateAdapter(List<Movie> itemList){
        this.mMovieList = itemList;
    }
}
