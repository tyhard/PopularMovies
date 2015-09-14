package com.creativeflint.popularmovies.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ty on 9/13/2015.
 */
public class Review {
    private String author;
    private String content;

    Review(){

    }

    Review(String author, String content){
        this.author = author;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public static List<Review> getReviewsFromJson(String json) throws JSONException{
        JSONObject envelope = new JSONObject(json);
        JSONArray jsonReviews = envelope.getJSONArray("results");

        List<Review> reviews = new ArrayList<>();
        for(int i = 0; i < jsonReviews.length(); i++){
            JSONObject review = jsonReviews.getJSONObject(i);
            reviews.add(new Review(review.getString("author"), review.getString("content")));
        }
        return reviews;

    }
}
