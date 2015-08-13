package com.creativeflint.popularmovies;

import android.media.Rating;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.creativeflint.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;



public class MovieDetailFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String MOVIE_PARAM = "MOVIE";
    private static final String TAG = "MovieDetailFragment";

    // TODO: Rename and change types of parameters
    private Movie mMovie;



    // TODO: Rename and change types and number of parameters
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
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true); //TODO: Implement
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
                .placeholder(R.drawable.spinner_rotate) //TODO: fix spinner
                .into(moviePoster);
        return detailView;
    }


}
