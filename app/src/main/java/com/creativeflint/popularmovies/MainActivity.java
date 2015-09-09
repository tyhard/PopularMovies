/*
 * Copyright (c) 2015 Creative Flint
 */
package com.creativeflint.popularmovies;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.creativeflint.popularmovies.model.Movie;

/**
 * Main activity for the Popular Movies app
 */
public class MainActivity extends Activity
        implements MoviePosterFragment.OnMovieSelectedListener,
        MoviePosterFragment.OnCommunicationErrorListener{

    private static final String TAG = "MovieActivity";
    private static final String POSTER_FRAG_TAG = "posters";
    private static final String DETAILS_FRAG_TAG = "details";

    private MovieDetailFragment mMovieDetailFragment;
    private MoviePosterFragment mMoviePosterFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "Entered on create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null){
            mMoviePosterFragment = (MoviePosterFragment) getFragmentManager()
                    .getFragment(savedInstanceState, POSTER_FRAG_TAG);
            Log.d(TAG, "MoviePosterFragment restored: " + mMoviePosterFragment);
            Log.d(TAG, "savedInstanceState = " + savedInstanceState);
        }

        FragmentManager fragmentManager = getFragmentManager();
        mMovieDetailFragment = (MovieDetailFragment) fragmentManager
                .findFragmentByTag(DETAILS_FRAG_TAG);

        if (mMovieDetailFragment == null) {
            if (mMoviePosterFragment == null){
                mMoviePosterFragment = new MoviePosterFragment();
            }
            fragmentManager.beginTransaction()
                    .replace(R.id.posters_container, mMoviePosterFragment, POSTER_FRAG_TAG)
                    .commit();
        }
    }


    @Override
    public void onMovieSelected(int position) {
        Movie selectedMovie = mMoviePosterFragment.getSelectedMovie(position);

        mMovieDetailFragment = MovieDetailFragment.newInstance(selectedMovie);

        int screenWidth = getResources().getConfiguration().screenWidthDp;
        Log.d(TAG, "Smallest Screen: " + screenWidth);
        Fragment detailsFragment = getFragmentManager().findFragmentByTag(DETAILS_FRAG_TAG);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        if (screenWidth <= 600) {
            transaction.replace(R.id.main_fragment_container, mMovieDetailFragment, DETAILS_FRAG_TAG);
        } else {
            FrameLayout container = (FrameLayout) findViewById(R.id.detail_container);
            Log.d(TAG, "Adding details fragment.");

            if (detailsFragment != null && detailsFragment.isVisible()){
                Log.d(TAG, "Details found, replacing");
                transaction.replace(R.id.detail_container, mMovieDetailFragment, DETAILS_FRAG_TAG);
            } else {
                Log.d(TAG, "No details found, adding.");
                transaction.add(R.id.detail_container, mMovieDetailFragment, DETAILS_FRAG_TAG);
            }
            container.setVisibility(View.VISIBLE);
        }
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getFragmentManager().putFragment(outState, POSTER_FRAG_TAG, mMoviePosterFragment);
        Log.d(TAG, "Saving fragment: " + getFragmentManager().getFragment(outState, POSTER_FRAG_TAG));
    }

    @Override
    public void onCommunicationError() {
        Log.d(TAG, "Comm Error Called");
        AlertDialog alertDialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unable_to_connect);
        builder.setTitle(R.string.download_failed);
        builder.setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mMoviePosterFragment == null){
                    mMoviePosterFragment = new MoviePosterFragment();
                }

                getFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment_container, mMoviePosterFragment, POSTER_FRAG_TAG)
                        .commit();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //do nothing.
            }
        });
        alertDialog = builder.create();
        if (!alertDialog.isShowing()){
            alertDialog.show();
        }
    }
}
