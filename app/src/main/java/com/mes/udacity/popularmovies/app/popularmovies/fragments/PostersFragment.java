package com.mes.udacity.popularmovies.app.popularmovies.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mes.udacity.popularmovies.app.popularmovies.R;
import com.mes.udacity.popularmovies.app.popularmovies.activities.DetailActivity;
import com.mes.udacity.popularmovies.app.popularmovies.adapters.PosterGridAdapter;
import com.mes.udacity.popularmovies.app.popularmovies.listeners.PostersListListener;
import com.mes.udacity.popularmovies.app.popularmovies.responses.ApiResponse;
import com.mes.udacity.popularmovies.app.popularmovies.models.Movie;
import com.mes.udacity.popularmovies.app.popularmovies.utils.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mohamed Elsayed on 10/19/2016.
 */

public class PostersFragment extends Fragment implements PostersListListener{

    private GridView gridView;
    private ProgressBar progressBar;
    private PosterGridAdapter posterGridAdapter;
    private int pages = 1;
    private String sortType = null;

    @Override
    public void onStart() {
        super.onStart();
        updateMovies(sortType);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.posters_fragment,container,false);
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        gridView = (GridView) view.findViewById(R.id.posters_gridview);
        posterGridAdapter = new PosterGridAdapter(getContext(),new ArrayList<Movie>());
        gridView.setAdapter(posterGridAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = (Movie) posterGridAdapter.getItem(position);
                Gson gson = new Gson();
                String movieStr = gson.toJson(movie);
                Intent intent = new Intent(getActivity(),DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT,movieStr);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.posters_fragment_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_popular_sort:
                sortType = "popular";
                posterGridAdapter.clear();
                pages = 1;
                updateMovies(sortType);
                return true;
            case R.id.action_rating_sort:
                sortType = "top_rated";
                posterGridAdapter.clear();
                pages = 1;
                updateMovies(sortType);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPosterListReady(List<Movie> movieList) {
        progressBar.setVisibility(View.GONE);
        posterGridAdapter.updatePosters(movieList);
        if (pages < 15) {
            updateMovies(sortType);
        }
    }

    @Override
    public void onPosterError(String message) {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(getContext(),message , Toast.LENGTH_LONG).show();
    }

    private void updateMovies(String sort){
        FetchMoviesData fetchMoviesData = new FetchMoviesData();
        if(sort == null){
            sort = "popular";
        }
        fetchMoviesData.execute(sort);
    }

    private class FetchMoviesData extends AsyncTask<String, Void, List<Movie>>{

        @Override
        protected List<Movie> doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            ApiResponse apiResponse = null;

            final String PAGE_NUM = "page";
            final String APPID_PARAM = "api_key";

            Uri uri = Uri.parse(Constants.MOVIE_API_BASE_URL).buildUpon()
                    .appendEncodedPath(params[0])
                    .appendQueryParameter(PAGE_NUM,Integer.toString(pages))
                    .appendQueryParameter(APPID_PARAM,Constants.MOVIE_API_KEY)
                    .build();
            try {
                URL url = new URL(uri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                Gson gson = new Gson();
                apiResponse = gson.fromJson(getBodyString(urlConnection.getInputStream()),ApiResponse.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return apiResponse.getMovies();
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            if(movies != null){
                onPosterListReady(movies);
                pages++;
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
}
