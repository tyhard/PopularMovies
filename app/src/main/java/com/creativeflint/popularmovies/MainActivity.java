package com.creativeflint.popularmovies;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.creativeflint.popularmovies.model.Movie;

public class MainActivity extends Activity
        implements MoviePosterFragment.OnMovieSelectedListener{

    public static final String TAG = "MovieActivity";

    private MovieDetailFragment movieDetailFragment;
    private MoviePosterFragment moviePosterFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "Entered on create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getFragmentManager();
        Log.d(TAG, "init detailFragment in onCreate");
        movieDetailFragment = (MovieDetailFragment) fragmentManager.findFragmentByTag("data");
        Log.d(TAG, "movieDetailFragment: " + movieDetailFragment);

        if (movieDetailFragment == null) {
            moviePosterFragment = MoviePosterFragment.newInstance(null);
            fragmentManager.beginTransaction()
//                .addToBackStack(null)
                    .replace(R.id.main_fragment_container, moviePosterFragment, "posters")
                    .commit();
        }


    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            Log.d(TAG, "Action bar null?" + (getActionBar() == null));
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onMovieSelected(int position) {
//        MoviePosterFragment posterFragment = (MoviePosterFragment) getFragmentManager()
//               .findFragmentById(R.id.main_fragment_container);
        moviePosterFragment = (MoviePosterFragment) getFragmentManager().findFragmentByTag("posters");
        Movie selectedMovie = moviePosterFragment.getSelectedMovie(position);


        movieDetailFragment = MovieDetailFragment.newInstance(selectedMovie);

        getFragmentManager().beginTransaction()
            .replace(R.id.main_fragment_container, movieDetailFragment, "data")
            .addToBackStack(null)
            .commit();
    }


}
