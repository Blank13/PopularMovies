package com.mes.udacity.popularmovies.app.popularmovies.listeners;

import com.mes.udacity.popularmovies.app.popularmovies.models.Trailer;

import java.util.List;

/**
 * Created by Mohamed Elsayed on 11/1/2016.
 */

public interface TrailersListListener {

    void onTrailerListReady(List<Trailer> trailerList);

    void onTrailerError(String message);
}
