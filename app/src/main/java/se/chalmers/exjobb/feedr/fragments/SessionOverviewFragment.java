package se.chalmers.exjobb.feedr.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import se.chalmers.exjobb.feedr.R;


public class SessionOverviewFragment extends Fragment {

    private static final String ARG_SESSION_KEY = "sessionKey";



    private String sessionKey;


    public SessionOverviewFragment() {
        // Required empty public constructor
    }

    public static SessionOverviewFragment newInstance(String sessionKey) {
        SessionOverviewFragment fragment = new SessionOverviewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SESSION_KEY, sessionKey);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sessionKey = getArguments().getString(ARG_SESSION_KEY);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_session_overview, container, false);
    }

}
