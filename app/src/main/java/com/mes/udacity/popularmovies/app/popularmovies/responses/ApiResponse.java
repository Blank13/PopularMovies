package com.mes.udacity.popularmovies.app.popularmovies.responses;

import com.google.gson.annotations.SerializedName;
import com.mes.udacity.popularmovies.app.popularmovies.models.Movie;

import java.util.List;

/**
 * Created by Mohamed Elsayed on 10/19/2016.
 */

public class ApiResponse {
    @SerializedName("page")
    private long page;

    @SerializedName("results")
    private List<Movie> movies;

    @SerializedName("total_results")
    private long totalResults;

    @SerializedName("total_pages")
    private long totalPages;

    public long getPage() {
        return page;
    }

    public void setPage(long page) {
        this.page = page;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    public long getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(long totalResults) {
        this.totalResults = totalResults;
    }

    public long getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(long totalPages) {
        this.totalPages = totalPages;
    }
}
