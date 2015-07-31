package com.creativeflint.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.creativeflint.popularmovies.model.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import java.io.BufferedReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ty on 7/28/15.
 */
public class FetchMoviesTask extends AsyncTask<String, Void, List<Movie>> {

    FetchMoviesTask(){
    }


    @Override
    protected List<Movie> doInBackground(String... param) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String moviesJson = null;

        Uri builtUri = Uri.parse("http://api.themoviedb.org").buildUpon()
                .appendPath("discover")
                .appendPath("movie")
                .appendQueryParameter("sort_by", "popularity.asc")
                .appendQueryParameter("page", "10") //TODO: limit to 10 pages of results?
                .build();

        try{

            URL url = new URL(builtUri.toString());
            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                moviesJson = null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                moviesJson = null;
            }
            moviesJson = buffer.toString();
        } catch (IOException e) {
            Log.e(this.getClass().getName(), "Error ", e);
            moviesJson = null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(this.getClass().getName(), "Error closing stream", e);
                }
            }
        }
        List<Movie> movies = null;
        try {
            movies = getMoviesFromJson(moviesJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movies;
    }

    private List<Movie> getMoviesFromJson(String json) throws JSONException{
        List<Movie> movieList = new ArrayList<>();
        JSONObject envelope = new JSONObject(json);
        JSONArray jsonMovieList = envelope.getJSONArray("results");
        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd");

        for (int i = 0; i < jsonMovieList.length(); i++){
            JSONObject jsonMovie = jsonMovieList.getJSONObject(i);
            Movie movie = new Movie();
            movie.setTitle(jsonMovie.getString("title"));
            movie.setOverview(jsonMovie.getString("overview"));
            movie.setPosterPath(jsonMovie.getString("poster_path"));
            movie.setUserRating(jsonMovie.getDouble("vote_average"));
            String dateStr = jsonMovie.getString("release_date");
            try{
                movie.setReleaseDate(dateFormat.parse(dateStr));
            } catch (ParseException pe) {
                Log.e(this.getClass().getName(), "Unable to parse date: " + dateStr);
            }

            movieList.add(movie);
        }
        return movieList;
    }
}
