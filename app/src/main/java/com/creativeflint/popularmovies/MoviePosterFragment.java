package com.creativeflint.popularmovies;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.Spinner;

import com.creativeflint.popularmovies.model.Movie;
import com.creativeflint.popularmovies.rest.MovieFetcher;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


/**
 * A fragment representing a grid of movie posters.
 *
 * Activities containing this fragment MUST implement the {@link OnMovieSelectedListener}
 * and {@link OnCommunicationErrorListener}
 * interface.
 */
public class MoviePosterFragment extends Fragment implements AbsListView.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener{

    private static final String ARG_SORT_OPTION = "sortOption";
    private static final String ARG_MOVIE_LIST = "movieList";
    private static final String TAG = "MoviePosterFragment";
    private static final String SORT_POPULAR_PARAM = "popularity.desc";
    private static final String SORT_RATING_PARAM = "vote_average.desc";
    private static final String MOVIE_SCROLL_POSITION = "moviePosition";
    private static final String CURRENT_PAGE = "currentPage";
    private static final int SORT_POPULAR_ITEM_POSITION = 0;
    private static final int SORT_RATING_ITEM_POSITION = 1;
    private static final int MY_FAVORITES_ITEM_POSITION = 2;

    private FetchMoviesTask mFetchMoviesTask;

    private String mSortOption;
    private int mCurrentPage = 1;
    private int mScrollPosition = 0;

    private OnMovieSelectedListener mPosterClickListener;
    private OnCommunicationErrorListener mCommunicationErrorListener;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    private ArrayAdapter<Movie> mMovieAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MoviePosterFragment() {
    }

    public static MoviePosterFragment newInstance(int position){
        MoviePosterFragment fragment = new MoviePosterFragment();
        Bundle args = new Bundle();
        args.putInt(MOVIE_SCROLL_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "On Create Called, Bundle = " + savedInstanceState);

        SharedPreferences settings = getActivity().getPreferences(Context.MODE_PRIVATE);

        if (getArguments() != null) {
            mSortOption = getArguments().getString(ARG_SORT_OPTION);
        }
        if (mSortOption == null){
            mSortOption = settings.getString(ARG_SORT_OPTION, SORT_POPULAR_PARAM);
        }
        if (mSortOption == null){
            mSortOption = SORT_POPULAR_PARAM;
        }
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView Called, Bundle = " + savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_item, container, false);

        if (mMovieAdapter == null){
            mMovieAdapter = new MoviePosterAdapter(new ArrayList<Movie>());
            Log.d(TAG, "New mMovieAdapter created.");
        }

        ArrayList<Movie> movies = null;
        if (savedInstanceState != null){
            movies = savedInstanceState.getParcelableArrayList(ARG_MOVIE_LIST);
            Log.d(TAG, "Movies restored: " + (movies != null ? movies.size() : 0));
            mScrollPosition = savedInstanceState.getInt(MOVIE_SCROLL_POSITION);
            Log.d(TAG, "Scroll position restored: " + mScrollPosition);
            mCurrentPage = savedInstanceState.getInt(CURRENT_PAGE);
        }

        //Restore any movies already downloaded
        if (movies != null && !movies.isEmpty()){
            mMovieAdapter.addAll(movies);
        }

        if (mMovieAdapter.isEmpty()){
            mCurrentPage = 1;
            fetchMovies();
        }

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mListView = (AbsListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(mMovieAdapter);

        mListView.setOnItemClickListener(this);

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    mScrollPosition = view.getFirstVisiblePosition();
                    Log.d(TAG, "Scroll Position: " + mScrollPosition);
                    if (view.getLastVisiblePosition() >= view.getCount() - 1) {
                        fetchMovies();
                    }
                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                mSwipeRefreshLayout.setEnabled(firstVisibleItem == 0);

            }
        });
        Log.d(TAG, "Scrolling to: " + mScrollPosition);
        mListView.setSelection(mScrollPosition);
        mListView.smoothScrollToPosition(mScrollPosition);

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
        mCommunicationErrorListener = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        List<Movie> movies = new ArrayList<>();
        if (mMovieAdapter != null){
            for (int i = 0; i < mMovieAdapter.getCount(); i++){
                movies.add(mMovieAdapter.getItem(i));
            }
        }
        outState.putParcelableArrayList(ARG_MOVIE_LIST, (ArrayList<? extends Parcelable>) movies);
        outState.putInt(MOVIE_SCROLL_POSITION, mScrollPosition);
        outState.putInt(CURRENT_PAGE, mCurrentPage);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mPosterClickListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mPosterClickListener.onMovieSelected(position);
        }
    }

    public Movie getSelectedMovie(int position){
        return mMovieAdapter.getItem(position);
    }

    @Override
    public void onRefresh() {
        mMovieAdapter.clear();
        mCurrentPage = 1;
        fetchMovies();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    /**
     * Listener for movie selections
     */
    public interface OnMovieSelectedListener {
        /**
         * Fired when a movie poster is selected.
         * @param position the position of the movie in the list
         */
        public void onMovieSelected(int position);
    }

    /**
     * Listener for communication errors
     */
    public interface OnCommunicationErrorListener{
        /**
         * Fired when a network communication error occurs so info can be passed to the UI.
         */
        public void onCommunicationError();
    }

    /**
     * A background task to retrieve movies from the service URL
     */
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
                movieList = new ArrayList<>();
            }
            mMovieAdapter.addAll(movieList);
            mCurrentPage++;
            mFetchMoviesTask = null;
        }

        @Override
        protected List<Movie> doInBackground(String... queryParams) {
            Log.d(TAG, "Making network call");
            ConnectivityManager conManager = (ConnectivityManager) getActivity()
                    .getApplicationContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo network = conManager.getActiveNetworkInfo();
            if (network == null || !network.isConnectedOrConnecting()){
                return new ArrayList<>();
            }

            String sort = null;
            if (queryParams != null && queryParams.length > 0){
                sort = queryParams[0];
            } else {
                sort = SORT_POPULAR_PARAM;
            }

            return MovieFetcher.getMoviesFromService(sort, mCurrentPage);
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
            Picasso.with(getActivity().getApplicationContext())
                    .load(movie.getPosterPath())
                    .placeholder(R.drawable.spinner_rotate)
                    .into(posterView);
            return convertView;
        }

    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
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
                switch (position) {
                    case SORT_POPULAR_ITEM_POSITION:
                        if (!mSortOption.equals(SORT_POPULAR_PARAM)) {
                            mSortOption = SORT_POPULAR_PARAM;
                            mMovieAdapter.clear();
                            mCurrentPage = 1;
                            fetchMovies();
                        }
                        break;
                    case SORT_RATING_ITEM_POSITION:
                        if (!mSortOption.equals(SORT_RATING_PARAM)) {
                            mSortOption = SORT_RATING_PARAM;
                            mMovieAdapter.clear();
                            mCurrentPage = 1;
                            fetchMovies();
                        }
                        break;
                    case MY_FAVORITES_ITEM_POSITION:
                        //TODO: retrieve movies from the database
                        break;
                    default:
                        throw new IllegalArgumentException("Unsupported menu item position: "
                                + position);

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
        if (mFetchMoviesTask == null || mFetchMoviesTask.getStatus() == AsyncTask.Status.FINISHED){
            mFetchMoviesTask = new FetchMoviesTask();
            mFetchMoviesTask.execute(mSortOption);
        }
    }

}
