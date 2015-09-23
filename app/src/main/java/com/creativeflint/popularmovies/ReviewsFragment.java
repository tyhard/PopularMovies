package com.creativeflint.popularmovies;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReviewsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReviewsFragment extends Fragment {

    private static final String AUTHOR_ARG = "author";
    private static final String CONTENT_ARG = "content";

    private String mAuthor;
    private String mContent;



    public static ReviewsFragment newInstance(String author, String content) {
        ReviewsFragment fragment = new ReviewsFragment();
        Bundle args = new Bundle();
        args.putString(AUTHOR_ARG, author);
        args.putString(CONTENT_ARG, content);
        fragment.setArguments(args);
        return fragment;
    }

    public ReviewsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null){
            mAuthor = savedInstanceState.getString(AUTHOR_ARG);
            mContent = savedInstanceState.getString(CONTENT_ARG);
        }
        if (getArguments() != null) {
            mAuthor = getArguments().getString(AUTHOR_ARG);
            mContent = getArguments().getString(CONTENT_ARG);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null){
            mAuthor = savedInstanceState.getString(AUTHOR_ARG);
            mContent = savedInstanceState.getString(CONTENT_ARG);
        }
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reviews, container, false);
        TextView textView = (TextView) view.findViewById(R.id.review_text);
        textView.setText(mAuthor + ": " + mContent);
        return view;

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(AUTHOR_ARG, mAuthor);
        outState.putString(CONTENT_ARG, mContent);
    }
}
