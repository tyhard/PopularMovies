package com.creativeflint.popularmovies.rest;

import android.util.Log;

import com.creativeflint.popularmovies.model.Movie;
import com.creativeflint.popularmovies.model.Review;

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
            String jsonString = convertResponseToString(response);
            movies = Movie.getMoviesFromJson(jsonString);
        } catch (RetrofitError re){
            Log.e(TAG, re.getMessage() + ": " + re.getUrl());
            movies = null;
        } catch (JSONException je){
            Log.e(TAG, "Can't parse movies: " + je.getMessage());
            movies = null;
        }

        return movies == null ? new ArrayList<Movie>() : movies;
    }

    public static String[] getTrailerUrlsFromService(String movieId){
        MovieWebService service = REST_ADAPTER.create(MovieWebService.class);

        String[] urls = null;
        try{
            Response response = service.getTrailers(movieId, MOVIE_DB_API_KEY);
            Log.d(TAG, "Trailer response = " + response.getStatus());
            String jsonString = convertResponseToString(response);
            urls = Movie.getTrailersUrlsFromJson(jsonString);
        } catch (RetrofitError re){
            Log.e(TAG, re.getMessage() + ": " + re.getUrl());
        } catch (JSONException je){
            Log.e(TAG, "Can't parse trailers: " + je.getMessage());
        }
        return urls == null ? new String[0] : urls;
    }

    private static String convertResponseToString (Response response){
        if (response.getBody() == null){
            return "";
        }
        TypedByteArray byteArray = (TypedByteArray) response.getBody();
        return new String(byteArray.getBytes());
    }

    public static List<Review> getReviewsFromService(String movieId){
        MovieWebService service = REST_ADAPTER.create(MovieWebService.class);

        List<Review> reviews = null;
        try{
            Response response = service.getReviews(movieId, MOVIE_DB_API_KEY);
            Log.d(TAG, "Reviews response: " + response.getStatus());
            String jsonString = convertResponseToString(response);
            reviews = Review.getReviewsFromJson(jsonString);
        } catch (RetrofitError re){
            Log.e(TAG, re.getMessage() + ": " + re.getUrl());
        } catch (JSONException je){
            Log.e(TAG, "Can't parse reviews: " + je.getMessage());
        }
        return reviews == null ? new ArrayList<Review>() : reviews;
    }

}
