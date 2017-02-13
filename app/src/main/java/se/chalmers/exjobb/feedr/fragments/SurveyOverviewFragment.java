package se.chalmers.exjobb.feedr.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

import se.chalmers.exjobb.feedr.R;
import se.chalmers.exjobb.feedr.adapters.QuestionAdapter;
import se.chalmers.exjobb.feedr.models.Answer;
import se.chalmers.exjobb.feedr.models.Question;
import se.chalmers.exjobb.feedr.models.Survey;
import se.chalmers.exjobb.feedr.utils.SharedPreferencesUtils;

public class SurveyOverviewFragment extends Fragment {
    //private static final String ARG_SURVEY = "survey";
    //private static final String ARG_SURVEYKEY = "survey";



   // private Survey mSurvey;
    protected QuestionAdapter mAdapter;
    private Map<String,Question> questions;
    private OnSurveyQuestionClickedListener mListener;
    private String mSurveyKey;

    public SurveyOverviewFragment() {
        // Required empty public constructor
    }



//    public static SurveyOverviewFragment newInstance(Survey survey) {
//        SurveyOverviewFragment fragment = new SurveyOverviewFragment();
//        Bundle args = new Bundle();
//       // args.putParcelable(ARG_SURVEY, survey);
//
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
   //         mSurvey = getArguments().getParcelable(ARG_SURVEY);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_survey_overview, container, false);
       // questions = mSurvey.getQuestions();
        TextView surveyName = (TextView) view.findViewById(R.id.survey_overview_name);
        surveyName.setText(SharedPreferencesUtils.getCurrentSurveyName(getContext()));
        mSurveyKey = SharedPreferencesUtils.getCurrentSurveyKey(view.getContext());
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.survey_overview_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        registerForContextMenu(recyclerView);
        mAdapter = new QuestionAdapter(this, mSurveyKey, mListener);
        recyclerView.setAdapter(mAdapter);

        return view;
    }


    public void onButtonPressed(String questionKey) {
        if (mListener != null) {
            mListener.onQuestionClicked(questionKey );
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSurveyQuestionClickedListener) {
            mListener = (OnSurveyQuestionClickedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSurveyQuestionClickedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnSurveyQuestionClickedListener {

        void onQuestionClicked(String questionKey);
    }
}
