package se.chalmers.exjobb.feedr.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import se.chalmers.exjobb.feedr.R;
import se.chalmers.exjobb.feedr.models.Question;

public class AddSurveyFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_COURSE_CODE = "course_code";


    // TODO: Rename and change types of parameters
    private String mCourseKey;


    private LinearLayout mLayout;
    private TextView mTextView;
    private Button mButton;
    private Button sendSurvey;
    private ArrayList<Question> questions = new ArrayList<>();

    private OnSurveyAddListener mListener;

    public AddSurveyFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static AddSurveyFragment newInstance(String courseKey) {
        AddSurveyFragment fragment = new AddSurveyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_COURSE_CODE, courseKey);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCourseKey = getArguments().getString(ARG_COURSE_CODE);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_survey, container, false);

        mTextView = (TextView) view.findViewById(R.id.questionIn);
        mButton = (Button) view.findViewById(R.id.addQuestion);
        mLayout = (LinearLayout) view.findViewById(R.id.container);
        sendSurvey = (Button) view.findViewById(R.id.btn_add_survey);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater =
                        (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                final View addQuestionView = layoutInflater.inflate(R.layout.row_survey, null);

                TextView newQuestion = (TextView) addQuestionView.findViewById(R.id.newQuestion);
                String question = mTextView.getText().toString();
                newQuestion.setText(question);

                final Question q = new Question(question);
                questions.add(q);

                Button btnRemove = (Button) addQuestionView.findViewById(R.id.removeQuestion);
                btnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((LinearLayout) addQuestionView.getParent()).removeView(addQuestionView);
                        int pos = questions.indexOf(q);
                        questions.remove(pos);
                    }
                });

                mLayout.addView(addQuestionView);
                mTextView.setText("");
                mTextView.setHint("Question");
            }
        });

        sendSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "The number of questions is " + questions.size(), Toast.LENGTH_LONG).show();

                mListener.onSurveyAdded(mCourseKey, questions);
                getFragmentManager().popBackStack();
            }
        });
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSurveyAddListener) {
            mListener = (OnSurveyAddListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSurveyAddListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnSurveyAddListener {

        void onSurveyAdded(String courseKey, ArrayList<Question> questions);
    }
}
