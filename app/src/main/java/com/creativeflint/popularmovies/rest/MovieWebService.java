package com.creativeflint.popularmovies.rest;

import java.util.Map;

import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.http.QueryMap;

/**
 * Created by ty on 8/30/15.
 */
public interface MovieWebService {
    @GET("/3/discover/movie")
    Response getMovies(@QueryMap Map<String, String> filters);

    @GET("/3/movie/{id}/videos")
    Response getTrailers(@Path("id") String movieId, @Query("api_key") String apiKey);
}
