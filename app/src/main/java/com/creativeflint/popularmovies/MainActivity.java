package com.creativeflint.popularmovies;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.creativeflint.popularmovies.model.Movie;

public class MainActivity extends AppCompatActivity
        implements MoviePosterFragment.OnMovieSelectedListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getFragmentManager();

        Fragment fragment = MoviePosterFragment.newInstance(null, null);
        fragmentManager.beginTransaction()
                .addToBackStack(null)
                .add(R.id.main_fragment_container, fragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMovieSelected(int position) {
        MoviePosterFragment posterFragment = (MoviePosterFragment) getFragmentManager()
                .findFragmentById(R.id.main_fragment_container);
        Movie selectedMovie = posterFragment.getSelectedMovie(position);


        MovieDetailFragment detailFragment = MovieDetailFragment.newInstance(selectedMovie);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment_container, detailFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
