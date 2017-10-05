package com.mes.udacity.popularmovies.app.popularmovies.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
                MovieContract.MovieEntry.MOVIES_TABLE + " (" +
                MovieContract.MovieEntry._ID+ " INTEGER PRIMARY KEY," +
                MovieContract.MovieEntry.MOVIE_TITLE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.MOVIE_POSTER_PATH + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.MOVIE_OVER_VIEW + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.MOVIE_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.MOVIE_VOTE_AVERAGE + " TEXT NOT NULL " +
                " );";
        final String SQL_CREATE_TRAILERS_TABLE = "CREATE TABLE " +
                MovieContract.TrailerEntry.TRAILERS_TABLE + " (" +
                MovieContract.TrailerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieContract.TrailerEntry.MOVIE_ID + " INTEGER NOT NULL, " +
                MovieContract.TrailerEntry.TRAILER_KEY + " TEXT NOT NULL, " +
                MovieContract.TrailerEntry.TRAILER_NAME + " TEXT NOT NULL " +

                " );";

        final String SQL_CREATE_REVIEWS_TABLE = "CREATE TABLE " +
                MovieContract.ReviewEntry.REVIEWS_TABLE + " (" +
                MovieContract.ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieContract.ReviewEntry.MOVIE_ID + " INTEGER NOT NULL, " +
                MovieContract.ReviewEntry.REVIEWS_AUTHOR + " TEXT NOT NULL, " +
                MovieContract.ReviewEntry.REVIEWS_CONTENT + " TEXT NOT NULL " +
                " );";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);
        db.execSQL(SQL_CREATE_TRAILERS_TABLE);
        db.execSQL(SQL_CREATE_REVIEWS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.MOVIES_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.TrailerEntry.TRAILERS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.ReviewEntry.REVIEWS_TABLE);
        onCreate(db);
    }
}
