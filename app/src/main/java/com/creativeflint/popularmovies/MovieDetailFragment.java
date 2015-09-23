/*
 * Copyright (c) 2015 Creative Flint
 */
package com.creativeflint.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.creativeflint.popularmovies.model.Movie;
import com.creativeflint.popularmovies.model.Review;
import com.creativeflint.popularmovies.rest.MovieFetcher;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * A fragment for displaying the details of a selected movie.
 */
public class MovieDetailFragment extends Fragment {
    private static final String MOVIE_KEY = "MOVIE";
    private static final String TAG = "MovieDetailFragment";
    private static final String POSTER_FRAG_TAG = "posters";
    private static final String TRAILERS_URL_KEY = "trailers";
    private static final String REVIEWS_KEY = "reviews";

    private Movie mMovie;
    private String[] mTrailerUrls;
    private List<Review> mReviews;
    private OnDataLoadedListener mOnTrailersLoadedListener;
    private ShareActionProvider mShareActionProvider;
//    private ArrayAdapter<String> mTrailerAdapter;

    public static MovieDetailFragment newInstance(Movie movie) {
        MovieDetailFragment fragment = new MovieDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(MOVIE_KEY, movie);
        fragment.setArguments(args);
        return fragment;
    }

    public MovieDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null){
            mTrailerUrls = savedInstanceState.getStringArray(TRAILERS_URL_KEY);
            mMovie = (Movie) savedInstanceState.get(MOVIE_KEY);
            mReviews = savedInstanceState.getParcelableArrayList(REVIEWS_KEY);
        }
        if (getArguments() != null && mMovie == null) {
            mMovie = (Movie) getArguments().getSerializable(MOVIE_KEY);
        }

        setHasOptionsMenu(true);
        if (mTrailerUrls == null || mTrailerUrls.length == 0){
            new FetchTrailersTask().execute();
        }
        if (mReviews == null){
            new FetchReviewsTask().execute();
        }

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

        CheckBox favoriteButton = (CheckBox) detailView.findViewById(R.id.favorite_button);
        favoriteButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    saveMovieAsFavorite(mMovie);
                }
            }
        });

        ImageView moviePoster = (ImageView) detailView.findViewById(R.id.movie_poster_view);
        Picasso.with(getActivity().getApplicationContext())
                .load(mMovie.getPosterPath())
                .placeholder(R.drawable.spinner_rotate)
                .into(moviePoster);
        return detailView;
    }

    private void saveMovieAsFavorite(Movie movie) {
        Toast.makeText(getActivity().getApplicationContext(),
                "Saving " + movie.getTitle(), Toast.LENGTH_SHORT).show();
        SharedPreferences.Editor prefsEditor = getActivity()
                .getPreferences(Context.MODE_PRIVATE).edit();
        prefsEditor.putString("Movie_" + movie.getMovieDbId(), movie.getMovieDbId());
        prefsEditor.commit();
    }

    private List<String> getSavedFavoriteMoviesIds(){
        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        Map<String, ?> allPrefs = prefs.getAll();
        List<String> movieIds = new ArrayList<>();
        for (String key : allPrefs.keySet()){
            if (key.startsWith("Movie_")) {
                movieIds.add(prefs.getString(key, null));
            }
        }
        return movieIds;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(MOVIE_KEY, mMovie);
        outState.putStringArray(TRAILERS_URL_KEY, mTrailerUrls);
        outState.putParcelableArrayList(REVIEWS_KEY, (ArrayList<Review>) mReviews);
        Log.d(TAG, "Saving Bundle: " + outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_details, menu);
        MenuItem shareItem = menu.findItem(R.id.share_item);
        Log.d(TAG, "SHARE ITEM:" + shareItem);
        mShareActionProvider = (ShareActionProvider) shareItem.getActionProvider();
//        if (mTrailerUrls != null)
//        setShareIntent(mTrailerUrls[0]);

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

    private void setShareIntent(String trailerUrl){
        if (trailerUrl != null && !trailerUrl.isEmpty()) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            //TODO: add string resources
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Checkout the " + "'" + mMovie.getTitle() +
                    "' " + "movie trailer.\n" + trailerUrl);
            sendIntent.setType("text/plain");
            if (mShareActionProvider != null){
                mShareActionProvider.setShareIntent(sendIntent);
            }
        }
    }

    /**
     * A background task to retrieve trailers from the service URL
     */
    private class FetchTrailersTask extends AsyncTask<Void, Void, String[]> {

        FetchTrailersTask(){
        }

        @Override
        protected void onPostExecute(String[] urls) {
            super.onPostExecute(urls);
            Log.d(TAG, "Returned " + urls.length + " trailer urls.");
            mOnTrailersLoadedListener.onTrailersLoaded(urls);
            mTrailerUrls = urls;

            if (urls.length > 0){
                setShareIntent(urls[0]);
//                mTrailerAdapter.addAll(Arrays.asList(urls));
            }
        }

        @Override
        protected String[] doInBackground(Void... params) {
            Log.d(TAG, "Making network call - Trailers");
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

    private class FetchReviewsTask extends AsyncTask<Void, Void, List<Review>> {

        @Override
        protected List<Review> doInBackground(Void... params) {
            Log.d(TAG, "Making network call - Reviews");
            ConnectivityManager conManager = (ConnectivityManager) getActivity()
                    .getApplicationContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo network = conManager.getActiveNetworkInfo();
            if (network == null || !network.isConnectedOrConnecting()){
                return new ArrayList<>();
            }
            Log.d(TAG, "Getting reviews for " + mMovie.getTitle()
                    + "(" + mMovie.getMovieDbId() + ")");
            return MovieFetcher.getReviewsFromService(mMovie.getMovieDbId());
        }

        @Override
        protected void onPostExecute(List<Review> reviews) {
            super.onPostExecute(reviews);
            Log.d(TAG, "Returned " + reviews.size() + " reviews.");
            mOnTrailersLoadedListener.onReviewsLoaded(reviews);
            mReviews = reviews;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mOnTrailersLoadedListener = (OnDataLoadedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnDataLoadedListener");
        }
    }

    public interface OnDataLoadedListener {
        public void onTrailersLoaded(String[] trailerUrls);
        public void onReviewsLoaded(List<Review> reviews);
    }


}
