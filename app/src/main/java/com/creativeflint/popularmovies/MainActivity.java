package com.creativeflint.popularmovies;


import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;

import com.creativeflint.popularmovies.model.Movie;

public class MainActivity extends Activity
        implements MoviePosterFragment.OnMovieSelectedListener{

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
//        MoviePosterFragment posterFragment = (MoviePosterFragment) getFragmentManager()
//               .findFragmentById(R.id.main_fragment_container);
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
}
