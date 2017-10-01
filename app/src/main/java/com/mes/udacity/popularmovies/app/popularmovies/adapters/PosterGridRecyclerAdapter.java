package com.mes.udacity.popularmovies.app.popularmovies.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mes.udacity.popularmovies.app.popularmovies.R;
import com.mes.udacity.popularmovies.app.popularmovies.models.Movie;
import com.mes.udacity.popularmovies.app.popularmovies.utils.Constants;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Mohamed Elsayed on 10/19/2016.
 */

public class PosterGridRecyclerAdapter extends RecyclerView.Adapter<PosterGridRecyclerAdapter.PosterViewHolder> {

    private final static String TAG = PosterGridRecyclerAdapter.class.getSimpleName();
    private static ListItemClickListener listItemClickListener;
    private List<Movie> movies;

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    public PosterGridRecyclerAdapter(List<Movie> movies,
                                     ListItemClickListener listItemClickListener) {
        this.movies = movies;
        PosterGridRecyclerAdapter.listItemClickListener = listItemClickListener;
    }

    @Override
    public PosterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.poster_item, parent, false);
        return new PosterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PosterViewHolder holder, int position) {
        Movie movie = movies.get(position);
        Picasso.with(holder.image.getContext())
                .load(Constants.MOVIE_API_IMAGE_BASE_URL+ movie.getPosterPath())
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return movies != null ? movies.size() : 0;
    }

    public Movie getItem(int index) {
        return movies.get(index);
    }

    public static class PosterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView image;

        public PosterViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.poster_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedItemIndex = getAdapterPosition();
            listItemClickListener.onListItemClick(clickedItemIndex);
        }

    }

    public void updatePosters(List<Movie> movies){
        this.movies.addAll(movies);
        notifyDataSetChanged();
    }

    public void clear(){
        if(this.movies != null) {
            this.movies.clear();
        }
    }
}