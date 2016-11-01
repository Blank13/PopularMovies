package com.mes.udacity.popularmovies.app.popularmovies.listeners;

import com.mes.udacity.popularmovies.app.popularmovies.models.Review;

import java.util.List;

/**
 * Created by Mohamed Elsayed on 11/1/2016.
 */

public interface ReviewsListListener {

    void onReviewListReady(List<Review> reviewList);

    void onReviewError(String message);
}
