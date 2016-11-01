package com.mes.udacity.popularmovies.app.popularmovies.responses;

import com.google.gson.annotations.SerializedName;
import com.mes.udacity.popularmovies.app.popularmovies.models.Review;

import java.util.List;

/**
 * Created by Mohamed Elsayed on 10/29/2016.
 */

public class ReviewsResponse {

    @SerializedName("results")
    private List<Review> reviews;

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }
}
