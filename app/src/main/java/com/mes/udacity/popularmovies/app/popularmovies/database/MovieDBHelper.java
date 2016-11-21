package com.mes.udacity.popularmovies.app.popularmovies.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mes.udacity.popularmovies.app.popularmovies.utils.Constants;

/**
 * Created by Mohamed Elsayed on 11/18/2016.
 */

public class MovieDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movie.db";
    private static final int DATABASE_VERSION = 1;

    public MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " +
                MovieContract.MovieEntery.MOVIES_TABLE + " (" +
                MovieContract.MovieEntery._ID+ " INTEGER PRIMARY KEY," +
                MovieContract.MovieEntery.MOVIE_TITLE + " TEXT NOT NULL, " +
                MovieContract.MovieEntery.MOVIE_POSTER_PATH + " TEXT NOT NULL, " +
                MovieContract.MovieEntery.MOVIE_OVER_VIEW + " TEXT NOT NULL, " +
                MovieContract.MovieEntery.MOVIE_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieContract.MovieEntery.MOVIE_VOTE_AVERAGE + " TEXT NOT NULL " +
                " );";
        final String SQL_CREATE_TRAILERS_TABLE = "CREATE TABLE " +
                MovieContract.TrailerEntery.TRAILERS_TABLE + " (" +
                MovieContract.TrailerEntery._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieContract.TrailerEntery.MOVIE_ID + " INTEGER NOT NULL, " +
                MovieContract.TrailerEntery.TRAILER_KEY + " TEXT NOT NULL, " +
                MovieContract.TrailerEntery.TRAILER_NAME + " TEXT NOT NULL " +

                " );";

        final String SQL_CREATE_REVIEWS_TABLE = "CREATE TABLE " +
                MovieContract.ReviewEntery.REVIEWS_TABLE + " (" +
                MovieContract.ReviewEntery._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieContract.ReviewEntery.MOVIE_ID + " INTEGER NOT NULL, " +
                MovieContract.ReviewEntery.REVIEWS_AUTHOR + " TEXT NOT NULL, " +
                MovieContract.ReviewEntery.REVIEWS_CONTENT + " TEXT NOT NULL " +
                " );";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);
        db.execSQL(SQL_CREATE_TRAILERS_TABLE);
        db.execSQL(SQL_CREATE_REVIEWS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntery.MOVIES_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.TrailerEntery.TRAILERS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.ReviewEntery.REVIEWS_TABLE);
        onCreate(db);
    }
}
