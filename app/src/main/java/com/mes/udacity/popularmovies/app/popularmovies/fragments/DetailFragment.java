package com.mes.udacity.popularmovies.app.popularmovies.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mes.udacity.popularmovies.app.popularmovies.R;
import com.mes.udacity.popularmovies.app.popularmovies.adapters.ReviewsListAdapter;
import com.mes.udacity.popularmovies.app.popularmovies.adapters.TrailersListAdapter;
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

    private Movie movie;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_fragment,container,false);
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)){
            String json = intent.getStringExtra(Intent.EXTRA_TEXT);
            Gson gson = new Gson();
            movie = gson.fromJson(json,Movie.class);
        }
        title = (TextView) view.findViewById(R.id.movie_titile);
        date = (TextView) view.findViewById(R.id.movie_date);
        rate = (TextView) view.findViewById(R.id.movie_rate);
        overView = (TextView) view.findViewById(R.id.movie_overview);

        favButton = (Button) view.findViewById(R.id.movie_fav_button);

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

        trailersListAdapter = new TrailersListAdapter(getContext(),new ArrayList<Trailer>());
        reviewsListAdapter = new ReviewsListAdapter(getContext(),new ArrayList<Review>());
        trailers.setAdapter(trailersListAdapter);
        reviews.setAdapter(reviewsListAdapter);
        initTrailerAction();
        return view;
    }

    private void initTrailersAndReviews() {
        String id = String.valueOf(movie.getId());
        FetchTrailers fetchTrailers = new FetchTrailers();
        fetchTrailers.execute(id);
        FetchReviews fetchReviews = new FetchReviews();
        fetchReviews.execute(id);
    }

    private void initTrailerAction() {
        trailers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

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
                reviewsRsponse = gson.fromJson(getBodyString(urlConnection.getInputStream()),ReviewsResponse.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return reviewsRsponse.getReviews();
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
}
