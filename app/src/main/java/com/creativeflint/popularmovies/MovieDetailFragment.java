/*
 * Copyright (c) 2015 Creative Flint
 */
package com.creativeflint.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.creativeflint.popularmovies.model.Movie;
import com.creativeflint.popularmovies.rest.MovieFetcher;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * A fragment for displaying the details of a selected movie.
 */
public class MovieDetailFragment extends Fragment {
    private static final String MOVIE_PARAM = "MOVIE";
    private static final String TAG = "MovieDetailFragment";
    private static final String POSTER_FRAG_TAG = "posters";

    private Movie mMovie;
    private OnTrailersLoadedListener mOnTrailersLoadedListener;

    public static MovieDetailFragment newInstance(Movie movie) {
        MovieDetailFragment fragment = new MovieDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(MOVIE_PARAM, movie);
        fragment.setArguments(args);
        return fragment;
    }

    public MovieDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMovie = (Movie) getArguments().getSerializable(MOVIE_PARAM);
        }
        new FetchTrailersTask().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View detailView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        TextView title = (TextView) detailView.findViewById(R.id.movie_title_text);
        title.setText(mMovie.getTitle());
        TextView releaseDate = (TextView) detailView.findViewById(R.id.release_date_text);
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
        releaseDate.append(dateFormat.format(mMovie.getReleaseDate()));
        RatingBar ratingBar = (RatingBar) detailView.findViewById(R.id.user_rating_bar);
        ratingBar.setMax(10);
        ratingBar.setStepSize(.25F);
        float rating = Double.valueOf(mMovie.getUserRating()).floatValue();
        ratingBar.setRating(rating);
        Log.d(TAG, "User rating: " + rating);
        TextView plotSummary = (TextView) detailView.findViewById(R.id.plot_summary_text);
        plotSummary.setText(mMovie.getOverview());

        ImageView moviePoster = (ImageView) detailView.findViewById(R.id.movie_poster_view);
        Picasso.with(getActivity().getApplicationContext())
                .load(mMovie.getPosterPath())
                .placeholder(R.drawable.spinner_rotate)
                .into(moviePoster);
        return detailView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(MOVIE_PARAM, mMovie);
        Log.d(TAG, "Saving Bundle: " + outState);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            getActivity().getFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment_container,
                    getFragmentManager().findFragmentByTag(POSTER_FRAG_TAG)).commit();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }


    }

    /**
     * A background task to retrieve movies from the service URL
     */
    private class FetchTrailersTask extends AsyncTask<Void, Void, String[]> {

        FetchTrailersTask(){
        }

        @Override
        protected void onPostExecute(String[] urls) {
            super.onPostExecute(urls);
            Log.d(TAG, "Returned " + urls.length + " trailer urls.");
            mOnTrailersLoadedListener.onTrailersLoaded(urls);
        }

        @Override
        protected String[] doInBackground(Void... params) {
            Log.d(TAG, "Making network call");
            ConnectivityManager conManager = (ConnectivityManager) getActivity()
                    .getApplicationContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo network = conManager.getActiveNetworkInfo();
            if (network == null || !network.isConnectedOrConnecting()){
                return new String[0];
            }
            Log.d(TAG, "Getting trailers for " + mMovie.getTitle()
                    + "(" + mMovie.getMovieDbId() + ")");
            return MovieFetcher.getTrailerUrlsFromService(mMovie.getMovieDbId());
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mOnTrailersLoadedListener = (OnTrailersLoadedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnTrailersLoadedListener");
        }
    }

    public interface OnTrailersLoadedListener{
        public void onTrailersLoaded(String[] trailerUrls);
    }


}
