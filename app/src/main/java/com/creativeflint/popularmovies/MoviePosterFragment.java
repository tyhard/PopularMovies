package com.creativeflint.popularmovies;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.creativeflint.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnMovieSelectedListener}
 * interface.
 */
public class MoviePosterFragment extends Fragment implements AbsListView.OnItemClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SORT_OPTION = "sortOption";
    private static final String TAG = "MoviePosterFragment";
    private static final String SORT_POPULAR_PARAM = "popularity.desc";
    private static final String SORT_RATING_PARAM = "vote_average.desc";
    private static final String MOVIE_DB_API_KEY = "";
    private static final int MINIMUM_VOTE_COUNT = 10;
    private static final int SORT_POPULAR_ITEM_POSITION = 0;
    private static final int SORT_RATING_ITEM_POSITION = 1;

    private AlertDialog alertDialog = null;


    // TODO: Rename and change types of parameters
    private String mSortOption;
    private int mCurrentPage = 1;

    private OnMovieSelectedListener mPosterClickListener;
    private OnCommunicationErrorListener mCommunicationErrorListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;


    private ArrayAdapter mMovieAdapter;
    private ArrayAdapter<CharSequence> mSortSpinnerAdapter;

    public static MoviePosterFragment newInstance(String sortOption) {
        MoviePosterFragment fragment = new MoviePosterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SORT_OPTION, sortOption);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MoviePosterFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//            Fragment fragment = MoviePosterFragment.newInstance(null, null);
        SharedPreferences settings = getActivity().getPreferences(Context.MODE_PRIVATE);

        if (getArguments() != null) {
            mSortOption = getArguments().getString(ARG_SORT_OPTION);
        }
        if (mSortOption == null){
            mSortOption = settings.getString(ARG_SORT_OPTION, SORT_POPULAR_PARAM);
        }

        mMovieAdapter = new MoviePosterAdapter(new ArrayList<Movie>());
        fetchMovies();
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item, container, false);


        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mMovieAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    if (view.getLastVisiblePosition() >= view.getCount() - 1) {
                        fetchMovies();
                    }
                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        });

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mPosterClickListener = (OnMovieSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnMovieSelectedListener");
        }
        try {
            mCommunicationErrorListener = (OnCommunicationErrorListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnCommunicationErrorListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mPosterClickListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mPosterClickListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mPosterClickListener.onMovieSelected(position);
        }
    }


    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    public Movie getSelectedMovie(int position){
        return (Movie) mMovieAdapter.getItem(position);
    }

    public interface OnMovieSelectedListener {
        public void onMovieSelected(int position);
    }

    public interface OnCommunicationErrorListener{
        public void onCommunicationError();
    }

    private class FetchMoviesTask extends AsyncTask<String, Void, List<Movie>> {

        FetchMoviesTask(){
        }

        @Override
        protected void onPostExecute(List<Movie> movieList) {
            super.onPostExecute(movieList);
            if (movieList.isEmpty()){
                mCommunicationErrorListener.onCommunicationError();
            }
            if (mMovieAdapter == null){
                movieList = new ArrayList<Movie>();
            } else{
                mMovieAdapter.addAll(movieList);
                mCurrentPage++;
            }

        }

        @Override
        protected List<Movie> doInBackground(String... queryParams) {

            ConnectivityManager conManager = (ConnectivityManager) getActivity()
                    .getApplicationContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo network = conManager.getActiveNetworkInfo();
            if (network == null || !network.isConnectedOrConnecting()){
                return new ArrayList<Movie>();
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String moviesJson = null;

            String sort = null;
            if (queryParams != null && queryParams.length > 0){
                sort = queryParams[0];
            }

            //TODO: add TMDb attribution to "About" or "Credits"
            Uri movieServiceUri = Uri.parse("http://api.themoviedb.org").buildUpon()
                    .appendPath("3")
                    .appendPath("discover")
                    .appendPath("movie")
                    .appendQueryParameter("sort_by", sort)
                    .appendQueryParameter("vote_count.gte", Integer.toString(MINIMUM_VOTE_COUNT))
                    .appendQueryParameter("page", Integer.toString(mCurrentPage))
                    .appendQueryParameter("api_key", MOVIE_DB_API_KEY)
                    .build();

            try{

                URL url = new URL(movieServiceUri.toString());
                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                // Read the input stream into a String
                InputStream stream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (stream == null) {
                    // Nothing to do.
                    moviesJson = null;
                }
                reader = new BufferedReader(new InputStreamReader(stream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    moviesJson = null;
                }
                moviesJson = buffer.toString();
            } catch (IOException e) {
                Log.e(TAG, "Error ", e);
                moviesJson = null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(TAG, "Error closing stream", e);
                    }
                }
            }
            List<Movie> movies = null;
            try {
                movies = Movie.getMoviesFromJson(moviesJson);
            } catch (JSONException e) {
                Log.e(TAG, "Can't parse JSON: " + e.getMessage());
            }
            return movies == null ? new ArrayList<Movie>() : movies;
        }


    }

    private class MoviePosterAdapter extends ArrayAdapter<Movie>{

        public MoviePosterAdapter(ArrayList<Movie> movies){
            super(getActivity(), 0, movies);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            if (convertView == null){
                convertView = getActivity().getLayoutInflater().inflate(
                        R.layout.fragment_movie_poster, parent, false);
            }

            ImageView posterView = (ImageView) convertView.findViewById(R.id.movie_poster_image_view);
            Movie movie = getItem(position);
            Log.d(TAG, "Poster URL: " + movie.getPosterPath());
            Picasso.with(getActivity().getApplicationContext())
                    .load(movie.getPosterPath())
                    .placeholder(R.drawable.spinner_rotate) //TODO: fix spinner
                    .into(posterView);
            return convertView;
        }

    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.menu_posters, menu);
        MenuItem item = menu.findItem(R.id.sort_spinner);
        Spinner sortSpinner = (Spinner) item.getActionView();
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getActivity().getApplicationContext(), R.array.sort_options_array,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(adapter);
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == SORT_POPULAR_ITEM_POSITION) {
                    if(mSortOption != SORT_POPULAR_PARAM){
                        mSortOption = SORT_POPULAR_PARAM;
                        mMovieAdapter.clear();
                        mCurrentPage = 1;
                        fetchMovies();
                    }
                } else {
                    if (mSortOption != SORT_RATING_PARAM){
                        mSortOption = SORT_RATING_PARAM;
                        mMovieAdapter.clear();
                        mCurrentPage = 1;
                        fetchMovies();
                    }
                }
                SharedPreferences.Editor prefsEditor = getActivity()
                        .getPreferences(Context.MODE_PRIVATE)
                        .edit();
                prefsEditor.putString(ARG_SORT_OPTION, mSortOption).commit();
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Do nothing.
            }
        });

        if (mSortOption != null && mSortOption.equals(SORT_RATING_PARAM)){
            sortSpinner.setSelection(SORT_RATING_ITEM_POSITION);
        }

    }

    private void fetchMovies(){
        new FetchMoviesTask().execute(mSortOption);
    }

//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//    }
}
