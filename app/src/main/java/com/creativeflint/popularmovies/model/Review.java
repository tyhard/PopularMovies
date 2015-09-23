package com.creativeflint.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ty on 9/13/2015.
 */
public class Review implements Parcelable{
    private String author;
    private String content;

    Review(){

    }

    Review(String author, String content){
        this.author = author;
        this.content = content;
    }

    Review(Parcel in){
        this.author = in.readString();
        this.content = in.readString();
    }

    public static final Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>(){
        @Override
        public Review createFromParcel(Parcel in){
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(author);
        dest.writeString(content);
    }
}
