package com.mes.udacity.popularmovies.app.popularmovies.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Mohamed on 10/5/2017.
 */

public class MovieContentProvider extends ContentProvider {

    public final static int MOVIE = 100;
    public final static int TRAILER = 200;
    public final static int REVIEW = 300;

    public final static UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.MovieEntry.MOVIES_TABLE, MOVIE);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.TrailerEntry.TRAILERS_TABLE,
                TRAILER);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.ReviewEntry.REVIEWS_TABLE, REVIEW);

        return uriMatcher;
    }

    private  MovieDBHelper movieDBHelper;

    @Override
    public boolean onCreate() {
        movieDBHelper = new MovieDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = movieDBHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor cursor;
        switch (match) {
            case MOVIE:
                cursor = db.query(MovieContract.MovieEntry.MOVIES_TABLE,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case TRAILER:
                cursor = db.query(MovieContract.TrailerEntry.TRAILERS_TABLE,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case REVIEW:
                cursor = db.query(MovieContract.ReviewEntry.REVIEWS_TABLE,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        db.close();
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = movieDBHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri returnedUri;
        switch (match) {
            case MOVIE:
                long id = db.insert(MovieContract.MovieEntry.MOVIES_TABLE,null,values);
                if (id > 0) {
                    returnedUri = ContentUris.withAppendedId(
                            MovieContract.MovieEntry.MOVIE_CONTENT_URI, id);
                }
                else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            case TRAILER:
                id = db.insert(MovieContract.TrailerEntry.TRAILERS_TABLE, null, values);
                if (id > 0) {
                    returnedUri = ContentUris.withAppendedId(
                            MovieContract.TrailerEntry.TRAILER_CONTENT_URI, id);
                }
                else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            case REVIEW:
                id = db.insert(MovieContract.ReviewEntry.REVIEWS_TABLE, null, values);
                if (id > 0) {
                    returnedUri = ContentUris.withAppendedId(
                            MovieContract.ReviewEntry.REVIEW_CONTENT_URI, id);
                }
                else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        db.close();
        return returnedUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = movieDBHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int count = 0;
        switch (match) {
            case MOVIE:
                count = db.delete(MovieContract.MovieEntry.MOVIES_TABLE,
                        selection,
                        selectionArgs);
                break;
            case TRAILER:
                count = db.delete(MovieContract.TrailerEntry.TRAILERS_TABLE,
                        selection,
                        selectionArgs);
                break;
            case REVIEW:
                count = db.delete(MovieContract.ReviewEntry.REVIEWS_TABLE,
                        selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        db.close();
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        return 0;
    }
}
