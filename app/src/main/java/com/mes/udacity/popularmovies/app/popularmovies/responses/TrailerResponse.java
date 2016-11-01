package com.mes.udacity.popularmovies.app.popularmovies.responses;

import com.google.gson.annotations.SerializedName;
import com.mes.udacity.popularmovies.app.popularmovies.models.Trailer;

import java.util.List;

/**
 * Created by Mohamed Elsayed on 10/29/2016.
 */

public class TrailerResponse {

    @SerializedName("results")
    private List<Trailer> trailers;

    public List<Trailer> getTrailers() {
        return trailers;
    }

    public void setTrailers(List<Trailer> trailers) {
        this.trailers = trailers;
    }
}
