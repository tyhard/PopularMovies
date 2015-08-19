package com.creativeflint.popularmovies;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import com.creativeflint.popularmovies.model.Movie;

public class MainActivity extends Activity
        implements MoviePosterFragment.OnMovieSelectedListener,
        MoviePosterFragment.OnCommunicationErrorListener{

    public static final String TAG = "MovieActivity";
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
        }

        FragmentManager fragmentManager = getFragmentManager();
        mMovieDetailFragment = (MovieDetailFragment) fragmentManager
                .findFragmentByTag(DETAILS_FRAG_TAG);

        if (mMovieDetailFragment == null) {
            if (mMoviePosterFragment == null){
                mMoviePosterFragment = MoviePosterFragment.newInstance(null);
            }

            fragmentManager.beginTransaction()
//                .addToBackStack(null)
                    .replace(R.id.main_fragment_container, mMoviePosterFragment, POSTER_FRAG_TAG)
                    .commit();
        }


    }


    @Override
    public void onMovieSelected(int position) {
        mMoviePosterFragment = (MoviePosterFragment) getFragmentManager()
                .findFragmentByTag(POSTER_FRAG_TAG);
        Movie selectedMovie = mMoviePosterFragment.getSelectedMovie(position);

        mMovieDetailFragment = MovieDetailFragment.newInstance(selectedMovie);
        getFragmentManager().beginTransaction()
            .replace(R.id.main_fragment_container, mMovieDetailFragment, DETAILS_FRAG_TAG)
            .addToBackStack(null)
            .commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getFragmentManager().putFragment(outState, POSTER_FRAG_TAG, mMoviePosterFragment);
    }

    @Override
    public void onCommunicationError() {
        Log.d(TAG, "Comm Error Called");
        AlertDialog alertDialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        Log.d(TAG, "Alert = " + builder);

        builder.setMessage(R.string.unable_to_connect);
        builder.setTitle(R.string.download_failed);
        builder.setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mMoviePosterFragment == null){
                    mMoviePosterFragment = MoviePosterFragment.newInstance(null);
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
