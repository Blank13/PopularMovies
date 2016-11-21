package com.mes.udacity.popularmovies.app.popularmovies.fragments;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mes.udacity.popularmovies.app.popularmovies.R;
import com.mes.udacity.popularmovies.app.popularmovies.adapters.ReviewsListAdapter;
import com.mes.udacity.popularmovies.app.popularmovies.adapters.TrailersListAdapter;
import com.mes.udacity.popularmovies.app.popularmovies.database.MovieContract;
import com.mes.udacity.popularmovies.app.popularmovies.database.MovieDBHelper;
import com.mes.udacity.popularmovies.app.popularmovies.listeners.ReviewsListListener;
import com.mes.udacity.popularmovies.app.popularmovies.listeners.TrailersListListener;
import com.mes.udacity.popularmovies.app.popularmovies.models.Movie;
import com.mes.udacity.popularmovies.app.popularmovies.models.Review;
import com.mes.udacity.popularmovies.app.popularmovies.models.Trailer;
import com.mes.udacity.popularmovies.app.popularmovies.responses.ReviewsResponse;
import com.mes.udacity.popularmovies.app.popularmovies.responses.TrailerResponse;
import com.mes.udacity.popularmovies.app.popularmovies.utils.Constants;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mohamed Elsayed on 10/21/2016.
 */

public class DetailFragment extends Fragment implements ReviewsListListener, TrailersListListener{

    public static final String MOVIE_CALL = "movie";

    private Movie movie;

    private ScrollView scrollView;

    private TextView title;
    private TextView date;
    private TextView rate;
    private TextView overView;

    private Button favButton;

    private ImageView image;

    private ListView trailers;
    private ListView reviews;

    private TrailersListAdapter trailersListAdapter;
    private ReviewsListAdapter reviewsListAdapter;

    private boolean firstTime = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_fragment,container,false);
        Intent intent = getActivity().getIntent();
        String movieJson;
        Gson gson = new Gson();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)){
            movieJson = intent.getStringExtra(Intent.EXTRA_TEXT);
            movie = gson.fromJson(movieJson,Movie.class);
        }
        else {
            Bundle args = getArguments();
            movieJson = args.getString(MOVIE_CALL);
            movie = gson.fromJson(movieJson, Movie.class);
        }

        scrollView = (ScrollView) view.findViewById(R.id.detail_scroll);

        title = (TextView) view.findViewById(R.id.movie_titile);
        date = (TextView) view.findViewById(R.id.movie_date);
        rate = (TextView) view.findViewById(R.id.movie_rate);
        overView = (TextView) view.findViewById(R.id.movie_overview);

        favButton = (Button) view.findViewById(R.id.movie_fav_button);
        if(checkExsistanceInFavourite()){
           favButton.setSelected(true);
        }
        initFavouriteAction();

        image = (ImageView) view.findViewById(R.id.movie_image);

        title.setText(movie.getTitle());
        date.setText(movie.getReleaseDate().toString());
        rate.setText(Double.toString(movie.getVoteAverage())+"/10");
        overView.setText(movie.getOverView());

        trailers = (ListView) view.findViewById(R.id.movie_trials);
        reviews = (ListView) view.findViewById(R.id.movie_reviews);

        Picasso.with(getContext())
                .load(Constants.MOVIE_API_IMAGE_BASE_URL+ movie.getPosterPath())
                .into(image);

        initTrailersAndReviews();

        if(trailersListAdapter == null){
            trailersListAdapter = new TrailersListAdapter(getContext(),new ArrayList<Trailer>());
        }
        if(reviewsListAdapter == null){
            reviewsListAdapter = new ReviewsListAdapter(getContext(),new ArrayList<Review>());
        }

        trailers.setFocusable(false);
        reviews.setFocusable(false);
        trailers.setAdapter(trailersListAdapter);
        reviews.setAdapter(reviewsListAdapter);

        initTrailerAction();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        setRetainInstance(true);
        super.onActivityCreated(savedInstanceState);
    }

    private boolean checkExsistanceInFavourite() {
        if(movie == null){
            return false;
        }
        MovieDBHelper movieDBHelper = new MovieDBHelper(getContext());
        SQLiteDatabase db = movieDBHelper.getReadableDatabase();
        final String search = "SELECT "+ MovieContract.MovieEntery._ID + " FROM "+
                MovieContract.MovieEntery.MOVIES_TABLE + " WHERE " +
                MovieContract.MovieEntery._ID  + " = " + movie.getId() + "";
        Cursor cursor = db.rawQuery(search, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            db.close();
            return false;
        }
        cursor.close();
        db.close();
        return true;
    }

    private void initFavouriteAction() {
        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!v.isSelected()){
                    v.setSelected(true);
                    addToFavourite();
                }
                else{
                    v.setSelected(false);
                    removeFromFavourite();
                }
            }
        });
    }

    private void initTrailersAndReviews() {
        if(firstTime){
            String id = String.valueOf(movie.getId());
            FetchTrailers fetchTrailers = new FetchTrailers();
            fetchTrailers.execute(id);
            FetchReviews fetchReviews = new FetchReviews();
            fetchReviews.execute(id);
        }
    }

    private void initTrailerAction() {
        trailers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Trailer trailer = (Trailer) trailersListAdapter.getItem(position);
                Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:"
                        + trailer.getKey()));
                Intent webIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.youtube.com/watch?v=" + trailer.getKey()));
                try {
                    startActivity(appIntent);
                } catch (ActivityNotFoundException ex) {
                    startActivity(webIntent);
                }
            }
        });
    }

    @Override
    public void onReviewListReady(List<Review> reviewList) {
        reviewsListAdapter.updateReviews(reviewList);
        setListViewHeightBasedOnChildren(reviews,reviewsListAdapter);
    }

    @Override
    public void onReviewError(String message) {

    }

    @Override
    public void onTrailerListReady(List<Trailer> trailerList) {
        trailersListAdapter.updatetrailers(trailerList);
        setListViewHeightBasedOnChildren(trailers,trailersListAdapter);
    }

    @Override
    public void onTrailerError(String message) {

    }

    /**
     * Take the list and make it with a fixed height due to scroll conflict between ScrollView
     * and ListView
     * @param listView
     * @param listAdapter
     */
    public void setListViewHeightBasedOnChildren(ListView listView, BaseAdapter listAdapter) {
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);

            if(listItem != null){
                // This next line is needed before you call measure or else you won't get measured height at all. The listitem needs to be drawn first to know the height.
                listItem.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                totalHeight += listItem.getMeasuredHeight();

            }
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    private class FetchTrailers extends AsyncTask<String, Void, List<Trailer>>{

        @Override
        protected List<Trailer> doInBackground(String... params) {
            MovieDBHelper movieDBHelper = new MovieDBHelper(getContext());
            SQLiteDatabase db = movieDBHelper.getReadableDatabase();
            final String search = "SELECT * FROM "+
                    MovieContract.TrailerEntery.TRAILERS_TABLE + " WHERE " +
                    MovieContract.TrailerEntery.MOVIE_ID  + " = " + movie.getId();
            Cursor cursor = db.rawQuery(search, null);
            if(cursor.getCount() > 0){
                List<Trailer> trailers = new ArrayList<>();
                while (cursor.moveToNext()){
                    Trailer trailer = new Trailer();
                    trailer.setKey(cursor.getString(
                            cursor.getColumnIndex(MovieContract.TrailerEntery.TRAILER_KEY)));
                    trailer.setName(cursor.getString(
                            cursor.getColumnIndex(MovieContract.TrailerEntery.TRAILER_NAME)));
                    trailers.add(trailer);
                }
                db.close();
                cursor.close();
                return trailers;
            }
            db.close();
            cursor.close();
            HttpURLConnection urlConnection = null;
            TrailerResponse trailerResponse = null;

            final String APPID_PARAM = "api_key";
            final String TYPE_PARAM = "videos";

            Uri uri = Uri.parse(Constants.MOVIE_API_BASE_URL).buildUpon()
                    .appendPath(params[0])
                    .appendEncodedPath(TYPE_PARAM)
                    .appendQueryParameter(APPID_PARAM,Constants.MOVIE_API_KEY)
                    .build();
            try {
                URL url = new URL(uri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                Gson gson = new Gson();
                trailerResponse = gson.fromJson(getBodyString(urlConnection.getInputStream()),TrailerResponse.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return trailerResponse.getTrailers();
        }

        @Override
        protected void onPostExecute(List<Trailer> trailerList) {
            if(trailerList != null){
                onTrailerListReady(trailerList);
            }
        }
    }

    private class FetchReviews extends AsyncTask<String, Void, List<Review>>{

        @Override
        protected List<Review> doInBackground(String... params) {
            MovieDBHelper movieDBHelper = new MovieDBHelper(getContext());
            SQLiteDatabase db = movieDBHelper.getReadableDatabase();
            final String search = "SELECT * FROM "+
                    MovieContract.ReviewEntery.REVIEWS_TABLE + " WHERE " +
                    MovieContract.ReviewEntery.MOVIE_ID  + " = " + movie.getId();
            Cursor cursor = db.rawQuery(search, null);
            if(cursor.getCount() > 0){
                List<Review> trailers = new ArrayList<>();
                while (cursor.moveToNext()){
                    Review review = new Review();
                    review.setAuthor(cursor.getString(
                            cursor.getColumnIndex(MovieContract.ReviewEntery.REVIEWS_AUTHOR)));
                    review.setContent(cursor.getString(
                            cursor.getColumnIndex(MovieContract.ReviewEntery.REVIEWS_CONTENT)));
                    trailers.add(review);
                }
                db.close();
                cursor.close();
                return trailers;
            }
            db.close();
            cursor.close();
            HttpURLConnection urlConnection = null;
            ReviewsResponse reviewsRsponse = null;

            final String APPID_PARAM = "api_key";
            final String TYPE_PARAM = "reviews";

            Uri uri = Uri.parse(Constants.MOVIE_API_BASE_URL).buildUpon()
                    .appendPath(params[0])
                    .appendEncodedPath(TYPE_PARAM)
                    .appendQueryParameter(APPID_PARAM,Constants.MOVIE_API_KEY)
                    .build();
            try {
                URL url = new URL(uri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                Gson gson = new Gson();
                reviewsRsponse = gson.fromJson(
                        getBodyString(urlConnection.getInputStream()),ReviewsResponse.class);
                return reviewsRsponse.getReviews();
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Review> reviews) {
            if(reviews != null){
                onReviewListReady(reviews);
            }
        }
    }

    private String getBodyString(InputStream inputStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }

    private void addToFavourite() {
        AddToDB db = new AddToDB();
        db.execute();
    }

    private void removeFromFavourite() {
        DeleteFromDB db = new DeleteFromDB();
        db.execute();
    }

    private class AddToDB extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            MovieDBHelper movieDBHelper = new MovieDBHelper(getContext());
            SQLiteDatabase db = movieDBHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(MovieContract.MovieEntery._ID, movie.getId());
            values.put(MovieContract.MovieEntery.MOVIE_TITLE, movie.getTitle());
            values.put(MovieContract.MovieEntery.MOVIE_POSTER_PATH, movie.getPosterPath());
            values.put(MovieContract.MovieEntery.MOVIE_RELEASE_DATE, movie.getReleaseDate());
            values.put(MovieContract.MovieEntery.MOVIE_VOTE_AVERAGE, movie.getVoteAverage());
            values.put(MovieContract.MovieEntery.MOVIE_OVER_VIEW, movie.getOverView());
            db.insert(MovieContract.MovieEntery.MOVIES_TABLE, null, values);

            for(int i = 0; i < trailersListAdapter.getCount(); i++){
                Trailer trailer = (Trailer) trailersListAdapter.getItem(i);
                values.clear();
                values.put(MovieContract.TrailerEntery.MOVIE_ID, movie.getId());
                values.put(MovieContract.TrailerEntery.TRAILER_KEY, trailer.getKey());
                values.put(MovieContract.TrailerEntery.TRAILER_NAME, trailer.getName());
                db.insert(MovieContract.TrailerEntery.TRAILERS_TABLE, null, values);
            }

            for(int i = 0; i < reviewsListAdapter.getCount(); i++){
                Review review = (Review) reviewsListAdapter.getItem(i);
                values.clear();
                values.put(MovieContract.ReviewEntery.MOVIE_ID, movie.getId());
                values.put(MovieContract.ReviewEntery.REVIEWS_AUTHOR, review.getAuthor());
                values.put(MovieContract.ReviewEntery.REVIEWS_CONTENT, review.getContent());
                db.insert(MovieContract.ReviewEntery.REVIEWS_TABLE, null, values);
            }
            db.close();
            return null;
        }
    }

    private class DeleteFromDB extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            MovieDBHelper movieDBHelper = new MovieDBHelper(getContext());
            SQLiteDatabase db = movieDBHelper.getWritableDatabase();
            db.delete(MovieContract.MovieEntery.MOVIES_TABLE,
                    MovieContract.MovieEntery._ID + "=" + movie.getId(), null);
            while (db.delete(MovieContract.TrailerEntery.TRAILERS_TABLE,
                    MovieContract.TrailerEntery.MOVIE_ID + "=" + movie.getId(), null) > 0);
            while (db.delete(MovieContract.ReviewEntery.REVIEWS_TABLE,
                    MovieContract.ReviewEntery.MOVIE_ID + "=" + movie.getId(), null) > 0);
            db.close();
            return null;
        }
    }

}
