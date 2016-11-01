package com.mes.udacity.popularmovies.app.popularmovies.activities;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.mes.udacity.popularmovies.app.popularmovies.fragments.PostersFragment;
import com.mes.udacity.popularmovies.app.popularmovies.R;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            attachPostersFragment();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_show_favourite){

        }
        return super.onOptionsItemSelected(item);
    }

    private void attachPostersFragment(){
        Fragment fragment = fragmentManager.findFragmentByTag("POSTERS_FRAGMENT");
        if (fragment == null) {
            fragment = new PostersFragment();
        }
        fragmentManager.beginTransaction()
                .replace(R.id.main_container, fragment, "POSTERS_FRAGMENT")
                .commit();
    }
}
