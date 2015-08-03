package com.creativeflint.popularmovies;

/**
 * Created by ty on 8/2/15.
 */
public enum SortOption {
    POPULARITY("popularity.desc"),
    USER_RATING("vote_average.desc");

    private String optionValue;

    SortOption(String optionValue){
        this.optionValue = optionValue;
    }

    public String getOptionValue(){
        return optionValue;
    }
}
