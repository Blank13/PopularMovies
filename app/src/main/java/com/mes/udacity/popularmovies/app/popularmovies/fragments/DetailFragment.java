package com.mes.udacity.popularmovies.app.popularmovies.fragments;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mes.udacity.popularmovies.app.popularmovies.R;
import com.mes.udacity.popularmovies.app.popularmovies.activities.MainActivity;
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
import com.mes.udacity.popularmovies.app.popularmovies.utils.SizedListView;
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

    private SizedListView trailers;
    private SizedListView reviews;
    private TrailersListAdapter trailersListAdapter;
    private ReviewsListAdapter reviewsListAdapter;

    private boolean firstTime = true;

    public interface DetailCallBack{
        void onFavouriteClick();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_fragment,container,false);
        Intent intent = getActivity().getIntent();
        String movieJson;
//        contentResolver = getContext().get
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

        trailers = (SizedListView) view.findViewById(R.id.movie_trials);
        reviews = (SizedListView) view.findViewById(R.id.movie_reviews);

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
        final String search = "SELECT "+ MovieContract.MovieEntry._ID + " FROM "+
                MovieContract.MovieEntry.MOVIES_TABLE + " WHERE " +
                MovieContract.MovieEntry._ID  + " = " + movie.getId() + "";
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
                    if(getActivity() instanceof MainActivity){
                        ((DetailCallBack)getActivity()).onFavouriteClick();
                    }
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
                Intent webIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.youtube.com/watch?v=" + trailer.getKey()));
                    startActivity(webIntent);
            }
        });
    }

    @Override
    public void onReviewListReady(List<Review> reviewList) {
        reviewsListAdapter.updateReviews(reviewList);
        reviews.setExpanded(true);
    }

    @Override
    public void onReviewError(String message) {

    }

    @Override
    public void onTrailerListReady(List<Trailer> trailerList) {
        trailersListAdapter.updatetrailers(trailerList);
        trailers.setExpanded(true);
    }

    @Override
    public void onTrailerError(String message) {

    }

    private class FetchTrailers extends AsyncTask<String, Void, List<Trailer>>{

        @Override
        protected List<Trailer> doInBackground(String... params) {
//            MovieDBHelper movieDBHelper = new MovieDBHelper(getContext());
//            SQLiteDatabase db = movieDBHelper.getReadableDatabase();
//            final String search = "SELECT * FROM "+
//                    MovieContract.TrailerEntry.TRAILERS_TABLE + " WHERE " +
//                    MovieContract.TrailerEntry.MOVIE_ID  + " = " + movie.getId();
            Cursor cursor = getContext().getContentResolver()
                    .query(MovieContract.TrailerEntry.TRAILER_CONTENT_URI,
                            null,
                            MovieContract.TrailerEntry.MOVIE_ID + " = " + movie.getId(),
                            null,
                            null);
            if(cursor.getCount() > 0){
                List<Trailer> trailers = new ArrayList<>();
                while (cursor.moveToNext()){
                    Trailer trailer = new Trailer();
                    trailer.setKey(cursor.getString(
                            cursor.getColumnIndex(MovieContract.TrailerEntry.TRAILER_KEY)));
                    trailer.setName(cursor.getString(
                            cursor.getColumnIndex(MovieContract.TrailerEntry.TRAILER_NAME)));
                    trailers.add(trailer);
                }
                cursor.close();
                return trailers;
            }
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
//            MovieDBHelper movieDBHelper = new MovieDBHelper(getContext());
//            SQLiteDatabase db = movieDBHelper.getReadableDatabase();
//            final String search = "SELECT * FROM "+
//                    MovieContract.ReviewEntry.REVIEWS_TABLE + " WHERE " +
//                    MovieContract.ReviewEntry.MOVIE_ID  + " = " + movie.getId();
            Cursor cursor = getContext().getContentResolver()
                    .query(MovieContract.ReviewEntry.REVIEW_CONTENT_URI,
                            null,
                            MovieContract.ReviewEntry.MOVIE_ID + " = " + movie.getId(),
                            null,
                            null);
            if(cursor.getCount() > 0){
                List<Review> trailers = new ArrayList<>();
                while (cursor.moveToNext()){
                    Review review = new Review();
                    review.setAuthor(cursor.getString(
                            cursor.getColumnIndex(MovieContract.ReviewEntry.REVIEWS_AUTHOR)));
                    review.setContent(cursor.getString(
                            cursor.getColumnIndex(MovieContract.ReviewEntry.REVIEWS_CONTENT)));
                    trailers.add(review);
                }
                cursor.close();
                return trailers;
            }
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
//            MovieDBHelper movieDBHelper = new MovieDBHelper(getContext());
//            SQLiteDatabase db = movieDBHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(MovieContract.MovieEntry._ID, movie.getId());
            values.put(MovieContract.MovieEntry.MOVIE_TITLE, movie.getTitle());
            values.put(MovieContract.MovieEntry.MOVIE_POSTER_PATH, movie.getPosterPath());
            values.put(MovieContract.MovieEntry.MOVIE_RELEASE_DATE, movie.getReleaseDate());
            values.put(MovieContract.MovieEntry.MOVIE_VOTE_AVERAGE, movie.getVoteAverage());
            values.put(MovieContract.MovieEntry.MOVIE_OVER_VIEW, movie.getOverView());
//            db.insert(MovieContract.MovieEntry.MOVIES_TABLE, null, values);
            getContext().getContentResolver()
                    .insert(MovieContract.MovieEntry.MOVIE_CONTENT_URI, values);
            for(int i = 0; i < trailersListAdapter.getCount(); i++){
                Trailer trailer = (Trailer) trailersListAdapter.getItem(i);
                values.clear();
                values.put(MovieContract.TrailerEntry.MOVIE_ID, movie.getId());
                values.put(MovieContract.TrailerEntry.TRAILER_KEY, trailer.getKey());
                values.put(MovieContract.TrailerEntry.TRAILER_NAME, trailer.getName());
                getContext().getContentResolver()
                        .insert(MovieContract.TrailerEntry.TRAILER_CONTENT_URI,values);
            }

            for(int i = 0; i < reviewsListAdapter.getCount(); i++){
                Review review = (Review) reviewsListAdapter.getItem(i);
                values.clear();
                values.put(MovieContract.ReviewEntry.MOVIE_ID, movie.getId());
                values.put(MovieContract.ReviewEntry.REVIEWS_AUTHOR, review.getAuthor());
                values.put(MovieContract.ReviewEntry.REVIEWS_CONTENT, review.getContent());
                getContext().getContentResolver()
                        .insert(MovieContract.ReviewEntry.REVIEW_CONTENT_URI, values);
            }
//            db.close();
            return null;
        }
    }

    private class DeleteFromDB extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
//            MovieDBHelper movieDBHelper = new MovieDBHelper(getContext());
//            SQLiteDatabase db = movieDBHelper.getWritableDatabase();
//            db.delete(MovieContract.MovieEntry.MOVIES_TABLE,
//                    MovieContract.MovieEntry._ID + "=" + movie.getId(), null);
//            while (db.delete(MovieContract.TrailerEntry.TRAILERS_TABLE,
//                    MovieContract.TrailerEntry.MOVIE_ID + "=" + movie.getId(), null) > 0);
//            while (db.delete(MovieContract.ReviewEntry.REVIEWS_TABLE,
//                    MovieContract.ReviewEntry.MOVIE_ID + "=" + movie.getId(), null) > 0);
//            db.close();
            getContext().getContentResolver()
                    .delete(MovieContract.MovieEntry.MOVIE_CONTENT_URI,
                            MovieContract.MovieEntry._ID + " = " + movie.getId(),
                            null);
            getContext().getContentResolver()
                    .delete(MovieContract.TrailerEntry.TRAILER_CONTENT_URI,
                            MovieContract.ReviewEntry.MOVIE_ID + " = " + movie.getId(),
                            null);
            getContext().getContentResolver()
                    .delete(MovieContract.ReviewEntry.REVIEW_CONTENT_URI,
                            MovieContract.ReviewEntry.MOVIE_ID + " = " + movie.getId(),
                            null);
            return null;
        }
    }

}
