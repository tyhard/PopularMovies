package com.creativeflint.popularmovies.rest;

import android.util.Log;

import com.creativeflint.popularmovies.model.Movie;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by ty on 8/30/15.
 */
public class MovieFetcher {

    private static final String TAG = "MovieFetcher";
    private static final String BASE_MOVIE_URL = "http://api.themoviedb.org";
    private static final String MOVIE_DB_API_KEY = "";
    private static final int MIN_VOTE_COUNT = 10;
    private static final RestAdapter REST_ADAPTER = new RestAdapter.Builder()
            .setEndpoint(BASE_MOVIE_URL)
            .setLogLevel(RestAdapter.LogLevel.BASIC)
            .build();

    static {
        if (MOVIE_DB_API_KEY == null || MOVIE_DB_API_KEY.isEmpty()){
            throw new IllegalArgumentException("API key is missing");
        }
    }

    public static List<Movie> getMoviesFromService(String sort, int page){
        MovieWebService service = REST_ADAPTER.create(MovieWebService.class);

        Map<String, String> params = new HashMap<>();
        params.put("sort_by", sort);
        params.put("vote_count.gte", Integer.toString(MIN_VOTE_COUNT));
        params.put("page", Integer.toString(page));
        params.put("api_key", MOVIE_DB_API_KEY);

        List<Movie> movies = null;
        try{
            Response response = service.getMovies(params);
            TypedByteArray byteArray = (TypedByteArray) response.getBody();
            String jsonString = new String(byteArray.getBytes());

            movies = Movie.getMoviesFromJson(jsonString);
        } catch (
                RetrofitError re){
            Log.e(TAG, re.getMessage() + ": " + re.getUrl());
            movies = null;
        } catch (JSONException je){
            Log.e(TAG, je.getMessage());
            movies = null;
        }

        return movies == null ? new ArrayList<Movie>() : movies;
    }

}
