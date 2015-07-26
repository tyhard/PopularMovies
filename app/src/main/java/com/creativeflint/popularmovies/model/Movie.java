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
    private String imageUrl;
    private String overview;
    private int userRating;
    private Date releaseDate;
}
