package com.mcbridebrandon.popularmovies.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mcbridebrandon.popularmovies.R;
import com.mcbridebrandon.popularmovies.model.Review;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>{

    private static final String TAG = "ReviewAdapter" ;
    private Review[] mReviewList;
    private final ReviewAdapter.ItemClickListener mClickListener;


    public ReviewAdapter(Context context, Review[] itemList, ReviewAdapter.ItemClickListener clickListener) {
        this.mReviewList = itemList;
        this.mClickListener = clickListener;
    }


    @NonNull
    @Override
    public ReviewAdapter.ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.review_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);

        return new ReviewAdapter.ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.ReviewViewHolder holder, int position) {
        Log.d(TAG, "#" + position);
        TextView author;
        TextView content;


        if (mReviewList != null) {
     author = holder.itemView.findViewById(R.id.textView4);
     content = holder.itemView.findViewById(R.id.textView7);
           author.setText(mReviewList[position].getAuthor());
            content.setText(mReviewList[position].getContent());

        }
    }

    @Override
    public int getItemCount() {
        if(mReviewList != null) {
            return this.mReviewList.length;
        }else{
            return 1;
        }
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //final ImageView ReviewImageView;

        ReviewViewHolder(View itemView) {
            super(itemView);
            //ReviewImageView = itemView.findViewById(R.id.iv_movie_Review_image);

            //set onclick
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mClickListener.onReviewItemClick(getAdapterPosition());
        }
    }

    //click listener for movie poster
    public interface ItemClickListener {
        void onReviewItemClick(int position);
    }


    public void updateAdapter(Review[] itemList){
        this.mReviewList = itemList;
    }
}


