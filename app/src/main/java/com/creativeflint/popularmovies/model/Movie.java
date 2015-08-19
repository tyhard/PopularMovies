package com.creativeflint.popularmovies.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ty on 7/23/15.
 */
public class Movie implements Serializable{

    private String title;
    private String posterPath;
    private String overview;
    private double userRating;
    private Date releaseDate;

    private final static String TAG = "Movie";
    private final static String posterImageRootUrl = "http://image.tmdb.org/t/p/w185";

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        if (posterPath != null && posterPath.startsWith("/")){
            this.posterPath = posterImageRootUrl + posterPath;
        } else {
            this.posterPath = posterPath;
        }

    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public double getUserRating() {
        return userRating;
    }

    public void setUserRating(double userRating) {
        this.userRating = userRating;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Override
    public String toString() {
        return this.getTitle();
    }

    public static List<Movie> getMoviesFromJson(String json) throws JSONException {
        List<Movie> movieList = new ArrayList<>();
        JSONObject envelope = new JSONObject(json);
        JSONArray jsonMovieList = envelope.getJSONArray("results");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (int i = 0; i < jsonMovieList.length(); i++){
            JSONObject jsonMovie = jsonMovieList.getJSONObject(i);
            Movie movie = new Movie();
            movie.setTitle(jsonMovie.getString("title"));
            movie.setOverview(jsonMovie.getString("overview"));
            movie.setPosterPath(jsonMovie.getString("poster_path"));
            movie.setUserRating(jsonMovie.getDouble("vote_average"));
            String dateStr = jsonMovie.getString("release_date");
            try{
                if (dateStr != null){
                    movie.setReleaseDate(dateFormat.parse(dateStr));
                }
            } catch (ParseException pe) {
                Log.e(TAG, "Unable to parse date: " + dateStr);
                dateStr = "";
            }

            movieList.add(movie);
        }
        Log.d(TAG, "movieList size: " + movieList.size());
        return movieList;
    }

}
