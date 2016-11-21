package com.mes.udacity.popularmovies.app.popularmovies.database;

import android.provider.BaseColumns;

/**
 * Created by Mohamed Elsayed on 11/18/2016.
 */

public class MovieContract {

    public static class MovieEntery implements BaseColumns {
        public final static String MOVIES_TABLE = "movies";
        public final static String MOVIE_POSTER_PATH = "poster_path";
        public final static String MOVIE_OVER_VIEW = "over_view";
        public final static String MOVIE_RELEASE_DATE = "release_date";
        public final static String MOVIE_TITLE = "title";
        public final static String MOVIE_VOTE_AVERAGE = "vote_average";
    }

    public static class TrailerEntery implements BaseColumns {
        public final static String TRAILERS_TABLE = "trailers";
        public final static String MOVIE_ID = "movie_id";
        public final static String TRAILER_KEY = "key";
        public final static String TRAILER_NAME = "name";
    }

    public static class ReviewEntery implements BaseColumns {
        public final static String REVIEWS_TABLE = "reviews";
        public final static String MOVIE_ID = "movie_id";
        public final static String REVIEWS_AUTHOR = "author";
        public final static String REVIEWS_CONTENT = "content";
    }
}
