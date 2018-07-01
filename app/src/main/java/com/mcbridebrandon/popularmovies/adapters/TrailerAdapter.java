package com.mcbridebrandon.popularmovies.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mcbridebrandon.popularmovies.R;
import com.mcbridebrandon.popularmovies.model.Trailer;
import com.squareup.picasso.Picasso;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder>{

    private static final String TAG = "TrailerAdapter" ;
    private Trailer[] mTrailerList;
    private final TrailerAdapter.ItemClickListener mClickListener;


    public TrailerAdapter(Context context, Trailer[] itemList, TrailerAdapter.ItemClickListener clickListener) {
        this.mTrailerList = itemList;
        this.mClickListener = clickListener;
    }


    @NonNull
    @Override
    public TrailerAdapter.TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.trailer_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);

        return new TrailerAdapter.TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerAdapter.TrailerViewHolder holder, int position) {
        Log.d(TAG, "#" + position);

        if (mTrailerList != null) {
            Picasso.get()
                    .load((mTrailerList[position].getPosterPath()))
                    .fit()
                    .into(holder.TrailerImageView);
            TextView trailerTitle = holder.itemView.findViewById(R.id.tv_trailer_title);
            trailerTitle.setText(mTrailerList[position].getName());
            Log.d(TAG, "#" + mTrailerList[position].getPosterPath());
        }
    }

    @Override
    public int getItemCount() {
        if(mTrailerList != null) {
            return this.mTrailerList.length;
        }else{
            return 1;
        }
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final ImageView TrailerImageView;

        TrailerViewHolder(View itemView) {
            super(itemView);
            TrailerImageView = itemView.findViewById(R.id.iv_movie_trailer_image);

            //set onclick
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mClickListener.onTrailerItemClick(getAdapterPosition());
        }
    }

    //click listener for movie poster
    public interface ItemClickListener {
        void onTrailerItemClick(int position);
    }


    public void updateAdapter(Trailer[] itemList){
        this.mTrailerList = itemList;
    }
}


