/*
 * Copyright (c) 2015 Creative Flint
 */
package com.creativeflint.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;
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
 * Model for holding movie data.
 */
public class Movie implements Parcelable, Serializable{

    private String movieDbId;
    private String title;
    private String posterPath;
    private String overview;
    private double userRating;
    private Date releaseDate;

    private final static String TAG = "Movie";
    private final static String posterImageRootUrl = "http://image.tmdb.org/t/p/w185";
    private final static String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v=";

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>(){
        @Override
        public Movie createFromParcel(Parcel in){
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public Movie() {}

    public Movie(Parcel in){
        movieDbId = in.readString();
        title = in.readString();
        posterPath = in.readString();
        overview = in.readString();
        userRating = in.readDouble();
        releaseDate = new Date(in.readLong());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(movieDbId);
        dest.writeString(title);
        dest.writeString(posterPath);
        dest.writeString(overview);
        dest.writeDouble(userRating);
        dest.writeLong(releaseDate.getTime());
    }

    public String getMovieDbId() {
        return movieDbId;
    }

    public void setMovieDbId(String movieDbId) {
        this.movieDbId = movieDbId;
    }

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

    /**
     * Builds a list of Movie objects from a JSON string of movie data
     * (as supplied by themoviedb.org).
     * @param json the raw JSON data
     * @return a {@code List} of {@code Movie} objects
     * @throws JSONException if the supplied JSON string can't be parsed.
     */
    public static List<Movie> getMoviesFromJson(String json) throws JSONException {
        List<Movie> movieList = new ArrayList<>();
        JSONObject envelope = new JSONObject(json);
        JSONArray jsonMovieList = envelope.getJSONArray("results");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (int i = 0; i < jsonMovieList.length(); i++){
            JSONObject jsonMovie = jsonMovieList.getJSONObject(i);
            Movie movie = new Movie();
            movie.setMovieDbId(jsonMovie.getString("id"));
            movie.setTitle(jsonMovie.getString("title"));
            movie.setOverview(jsonMovie.getString("overview"));
            movie.setPosterPath(jsonMovie.getString("poster_path"));
            movie.setUserRating(jsonMovie.getDouble("vote_average"));
            String dateStr = jsonMovie.getString("release_date");
            try{
                if (dateStr != null){
                    movie.setReleaseDate(dateFormat.parse(dateStr));
                } else {
                    dateStr = "";
                }
            } catch (ParseException pe) {
                Log.e(TAG, "Unable to parse date: " + dateStr);
                dateStr = "";
            }

            movieList.add(movie);
        }
        return movieList;
    }

    public static String[] getTrailersUrlsFromJson(String json) throws JSONException{
        JSONObject envelope = new JSONObject(json);
        JSONArray jsonTrailerList = envelope.getJSONArray("results");

        String[] trailerUrls = new String[jsonTrailerList.length()];
        Log.d(TAG, "Number of trailers: " + trailerUrls.length);
        for(int i = 0; i < jsonTrailerList.length(); i++){
            JSONObject trailer = jsonTrailerList.getJSONObject(i);
            trailerUrls[i] = YOUTUBE_BASE_URL + trailer.getString("key");
        }
        return trailerUrls;
    }
}
