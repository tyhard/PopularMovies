package com.creativeflint.popularmovies.model;

import java.util.Date;

/**
 * Created by ty on 7/23/15.
 */
public class Movie {

    /*original title
    movie poster image thumbnail
    A plot synopsis (called overview in the api)
    user rating (called vote_average in the api)
    release date*/

    private String title;
    private String posterPath;
    private String overview;
    private double userRating;
    private Date releaseDate;

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

}
