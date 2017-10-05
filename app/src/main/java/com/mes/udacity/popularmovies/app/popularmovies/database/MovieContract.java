package com.mes.udacity.popularmovies.app.popularmovies.database;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Mohamed Elsayed on 11/18/2016.
 */

public class MovieContract {

    public final static String AUTHORITY = "com.mes.udacity.popularmovies.app.popularmovies";

    public final static Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static class MovieEntry implements BaseColumns {

        public final static String MOVIES_TABLE = "movies";
        public final static Uri MOVIE_CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(MOVIES_TABLE).build();

        public final static String MOVIE_POSTER_PATH = "poster_path";
        public final static String MOVIE_OVER_VIEW = "over_view";
        public final static String MOVIE_RELEASE_DATE = "release_date";
        public final static String MOVIE_TITLE = "title";
        public final static String MOVIE_VOTE_AVERAGE = "vote_average";


    }

    public static class TrailerEntry implements BaseColumns {

        public final static String TRAILERS_TABLE = "trailers";
        public final static Uri TRAILER_CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(TRAILERS_TABLE).build();

        public final static String MOVIE_ID = "movie_id";
        public final static String TRAILER_KEY = "key";
        public final static String TRAILER_NAME = "name";
    }

    public static class ReviewEntry implements BaseColumns {

        public final static String REVIEWS_TABLE = "reviews";
        public final static Uri REVIEW_CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(REVIEWS_TABLE).build();

        public final static String MOVIE_ID = "movie_id";
        public final static String REVIEWS_AUTHOR = "author";
        public final static String REVIEWS_CONTENT = "content";
    }
}
