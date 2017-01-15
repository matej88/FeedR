package se.chalmers.exjobb.feedr.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;


import se.chalmers.exjobb.feedr.R;
import se.chalmers.exjobb.feedr.adapters.SurveyAdapter;
import se.chalmers.exjobb.feedr.models.Survey;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnSurveyClickListener} interface
 * to handle interaction events.
 * Use the {@link SurveyListTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SurveyListTabFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_COURSEKEY = "current_course_key";


    private String mCurrentCourseKey;
    private SurveyAdapter mAdapter;
    private OnSurveyClickListener mListener;


    public SurveyListTabFragment() {
        // Required empty public constructor
    }


    public static SurveyListTabFragment newInstance(String courseKey) {
        SurveyListTabFragment fragment = new SurveyListTabFragment();
        Bundle args = new Bundle();
        args.putString(ARG_COURSEKEY, courseKey);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCurrentCourseKey = getArguments().getString(ARG_COURSEKEY);

        }

        Log.d("onCreate", mCurrentCourseKey);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_survey_list_tab, container, false);

        Log.d("onCreateView", mCurrentCourseKey);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab_addSurvey);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                  .setAction("Action", null).show();
                mListener.onAddSurveyClicked(mCurrentCourseKey);

            }
        });

        fab.setVisibility(View.VISIBLE);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.survey_tab_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        registerForContextMenu(recyclerView);
        mAdapter = new SurveyAdapter(this, mCurrentCourseKey, mListener);
        recyclerView.setAdapter(mAdapter);
          return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Survey survey) {
        if (mListener != null) {
            mListener.onSurveyClicked(survey);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSurveyClickListener) {
            mListener = (OnSurveyClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSurveyClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnSurveyClickListener {
        void onSurveyClicked(Survey survey);
        void onAddSurveyClicked(String courseKey);
    }


}
