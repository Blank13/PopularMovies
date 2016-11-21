package com.mes.udacity.popularmovies.app.popularmovies.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.mes.udacity.popularmovies.app.popularmovies.fragments.DetailFragment;
import com.mes.udacity.popularmovies.app.popularmovies.fragments.PostersFragment;

/**
 * Created by Mohamed Elsayed on 11/19/2016.
 */

public class StaticMethods {

    public static void attachPostersFragment(FragmentManager fragmentManager, int container) {
        Fragment fragment = fragmentManager.findFragmentByTag(Constants.POSTER_FRAGMENT);
        if (fragment == null) {
            fragment = new PostersFragment();
        }
        fragmentManager.beginTransaction()
                .replace(container, fragment, Constants.POSTER_FRAGMENT)
                .commit();
    }

    public static void attachDetailFragment(FragmentManager fragmentManager, int container) {
        Fragment fragment = fragmentManager.findFragmentByTag(Constants.DETAIL_FRAGMENT);
        if (fragment == null) {
            fragment = new DetailFragment();
        }
        fragmentManager.beginTransaction()
                .replace(container, fragment, Constants.DETAIL_FRAGMENT)
                .commit();
    }

    public static boolean haveNetworkConnection(Activity activity) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) activity
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI")) {
                if (ni.isConnected()) {
                    haveConnectedWifi = true;
                }
            }
            else if (ni.getTypeName().equalsIgnoreCase("MOBILE")) {
                if (ni.isConnected()) {
                    haveConnectedMobile = true;
                }
            }
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
}
