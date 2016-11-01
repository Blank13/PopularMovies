package com.mes.udacity.popularmovies.app.popularmovies.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.mes.udacity.popularmovies.app.popularmovies.R;
import com.mes.udacity.popularmovies.app.popularmovies.models.Movie;
import com.mes.udacity.popularmovies.app.popularmovies.utils.Constants;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Mohamed Elsayed on 10/19/2016.
 */

public class PosterGridAdapter extends BaseAdapter {

    private final static String TAG = PosterGridAdapter.class.getSimpleName();

    private List<Movie> movies;
    private Context context;

    public PosterGridAdapter(Context context, List<Movie> movies) {
        this.movies = movies;
        this.context = context;
    }


    @Override
    public int getCount() {
        return movies != null ? movies.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return movies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rootView = convertView;
        PosterViewHolder posterViewHolder = null;
        if(rootView == null){
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            rootView = inflater.inflate(R.layout.poster_item,parent,false);
            posterViewHolder = new PosterViewHolder();
            posterViewHolder.image = (ImageView) rootView.findViewById(R.id.poster_image);
            rootView.setTag(posterViewHolder);
        }
        else {
            posterViewHolder = (PosterViewHolder) rootView.getTag();
        }
        Movie movie = movies.get(position);
        Picasso.with(context)
                .load(Constants.MOVIE_API_IMAGE_BASE_URL+ movie.getPosterPath())
                .into(posterViewHolder.image);
        return rootView;
    }

    public static class PosterViewHolder{
        private ImageView image;
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