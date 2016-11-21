package com.mes.udacity.popularmovies.app.popularmovies.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Mohamed Elsayed on 10/29/2016.
 */

public class Trailer {

    @SerializedName("key")
    private String key;

    @SerializedName("name")
    private String name;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
