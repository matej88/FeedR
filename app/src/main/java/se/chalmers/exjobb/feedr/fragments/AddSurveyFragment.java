package se.chalmers.exjobb.feedr.fragments;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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

    private EditText mSurveyName;
    private EditText mQuestion;
    private Button mButton;
    private Button sendSurvey;
    private ArrayList<Question> questions = new ArrayList<>();
    private String surveyName;
    private OnSurveyAddListener mListener;

    public AddSurveyFragment() {
        // Required empty public constructor
    }


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

        mQuestion = (EditText) view.findViewById(R.id.questionIn);
        mButton = (Button) view.findViewById(R.id.addQuestion);
        mLayout = (LinearLayout) view.findViewById(R.id.container);
        sendSurvey = (Button) view.findViewById(R.id.btn_add_survey);
        mSurveyName = (EditText) view.findViewById(R.id.add_survey_name_et);
        questions = new ArrayList<>();
        // if user pressed enter on keyboard on Email field, then go to the next
        mSurveyName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if(id == EditorInfo.IME_NULL) {
                    mQuestion.requestFocus();
                    return true;
                }

                return false;
            }
        });

        mQuestion.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if(id == EditorInfo.IME_NULL) {
                    mButton.requestFocus();
                    hideKeyboard();
                    return true;
                }

                return false;
            }
        });






        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater =
                        (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                final View addQuestionView = layoutInflater.inflate(R.layout.row_survey, null);

                surveyName = mSurveyName.getText().toString();
                if(surveyName.length() < 5){
                    Toast.makeText(getContext(), "Please enter longer name", Toast.LENGTH_SHORT).show();
                }
                TextView newQuestion = (TextView) addQuestionView.findViewById(R.id.newQuestion);

                String question = mQuestion.getText().toString();
                if(question.length() > 10) {
                    newQuestion.setText(question);
                }else{
                    Toast.makeText(getContext(), "Question is too short", Toast.LENGTH_SHORT).show();
                    return;
                }

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
                mQuestion.setText("");
                mQuestion.setHint("Question");
            }
        });

        sendSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    sendSurvey();
            }
        });
        return view;
    }

    public void sendSurvey(){
        View focusView = null;
        if(TextUtils.isEmpty(surveyName)){
            mSurveyName.setError("Field required");
            focusView = mSurveyName;
        }else if(questions.size() == 0){
            mQuestion.setError("Please enter some questions");
            focusView = mQuestion;
        }else{
            mListener.onSurveyAdded(mCourseKey, questions,surveyName);
            getFragmentManager().popBackStack();
        }

    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mSurveyName.getWindowToken(), 0);
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

        void onSurveyAdded(String courseKey, ArrayList<Question> questions, String surveyName);
    }
}
