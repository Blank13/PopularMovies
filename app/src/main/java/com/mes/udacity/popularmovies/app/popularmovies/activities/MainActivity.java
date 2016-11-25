package com.mes.udacity.popularmovies.app.popularmovies.activities;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;

import com.mes.udacity.popularmovies.app.popularmovies.R;
import com.mes.udacity.popularmovies.app.popularmovies.fragments.DetailFragment;
import com.mes.udacity.popularmovies.app.popularmovies.fragments.PostersFragment;
import com.mes.udacity.popularmovies.app.popularmovies.utils.Constants;

import static com.mes.udacity.popularmovies.app.popularmovies.utils.StaticMethods.attachPostersFragment;

public class MainActivity extends AppCompatActivity implements PostersFragment.Callback
        ,DetailFragment.DetailCallBack{

    private FragmentManager fragmentManager;

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        attachPostersFragment(fragmentManager,R.id.main_container);
        if(findViewById(R.id.activity_detail_container) != null){
            mTwoPane = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onItemSelected(String movieStr) {
        if(mTwoPane){
            Bundle args = new Bundle();
            args.putString(DetailFragment.MOVIE_CALL, movieStr);
            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);
            fragmentManager.beginTransaction()
                    .replace(R.id.activity_detail_container,fragment,Constants.DETAIL_FRAGMENT)
                    .commit();
        }
        else {
            Intent intent = new Intent(this,DetailActivity.class)
                    .putExtra(Intent.EXTRA_TEXT, movieStr);
            startActivity(intent);
        }
    }

    @Override
    public void onChangeSort() {
        if(mTwoPane){
            Fragment fragment = fragmentManager.findFragmentByTag(Constants.DETAIL_FRAGMENT);
            if(fragment != null){
                fragmentManager.beginTransaction().detach(fragment).commit();
            }
        }
    }

    @Override
    public void onFavouriteClick() {
        if(mTwoPane){
            Fragment fragment = fragmentManager.findFragmentByTag(Constants.POSTER_FRAGMENT);
            ((PostersFragment)fragment).onFavoutiteChange();
        }
    }
}
