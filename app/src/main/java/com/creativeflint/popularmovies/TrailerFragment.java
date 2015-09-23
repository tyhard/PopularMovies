package com.creativeflint.popularmovies;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TrailerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TrailerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TrailerFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TRAILER_URL = "trailerUrl";
    private static final String TAG = "TrailerFragment";

    // TODO: Rename and change types of parameters
    private String mTrailerUrl;
    private ImageView mPlayButton;

//    private OnFragmentInteractionListener mListener;

    public static TrailerFragment newInstance(String trailerUrl) {
        TrailerFragment fragment = new TrailerFragment();
        Bundle args = new Bundle();
        args.putString(TRAILER_URL, trailerUrl);
        fragment.setArguments(args);
        return fragment;
    }

    public TrailerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null){
            mTrailerUrl = savedInstanceState.getString(TRAILER_URL);
        }
        if (mTrailerUrl == null && getArguments() != null) {
            mTrailerUrl = getArguments().getString(TRAILER_URL);
        }
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null){
            mTrailerUrl = (String) savedInstanceState.get(TRAILER_URL);
        }

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trailer, container, false);
        TextView text = (TextView) view.findViewById(R.id.trailer_text);
        mPlayButton = (ImageView) view.findViewById(R.id.play_button_image);
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Play button clicked.");
                Intent playVideo = new Intent(Intent.ACTION_VIEW,Uri.parse(mTrailerUrl));
                List<ResolveInfo> activities = getActivity().getPackageManager()
                        .queryIntentActivities(playVideo, 0);
                boolean hasVideoPlayer = activities.size() > 0;

                if (hasVideoPlayer){
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mTrailerUrl)));
                }
            }
        });
        text.setText(mTrailerUrl);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(TRAILER_URL, mTrailerUrl);
    }
}
