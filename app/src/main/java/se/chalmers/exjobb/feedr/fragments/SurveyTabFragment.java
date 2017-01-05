package se.chalmers.exjobb.feedr.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import se.chalmers.exjobb.feedr.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SurveyTabFragment extends Fragment {


    public SurveyTabFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_survey_tab, container, false);
    }

}
