package com.mes.udacity.popularmovies.app.popularmovies.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mes.udacity.popularmovies.app.popularmovies.R;
import com.mes.udacity.popularmovies.app.popularmovies.fragments.DetailFragment;
import com.mes.udacity.popularmovies.app.popularmovies.fragments.PostersFragment;

public class DetailActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            attachDetailFragment();
        }
    }

    private void attachDetailFragment() {
        Fragment fragment = fragmentManager.findFragmentByTag("DETAIL_FRAGMENT");
        if (fragment == null) {
            fragment = new DetailFragment();
        }
        fragmentManager.beginTransaction()
                .replace(R.id.activity_detail_container, fragment, "DETAIL_FRAGMENT")
                .commit();
    }
}
