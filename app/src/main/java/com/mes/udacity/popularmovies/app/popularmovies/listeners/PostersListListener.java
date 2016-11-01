package com.mes.udacity.popularmovies.app.popularmovies.listeners;

import com.mes.udacity.popularmovies.app.popularmovies.models.Movie;

import java.util.List;

/**
 * Created by Mohamed Elsayed on 10/19/2016.
 */

public interface PostersListListener {

    void onPosterListReady(List<Movie> movieList);

    void onPosterError(String message);
}
