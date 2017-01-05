package se.chalmers.exjobb.feedr.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import se.chalmers.exjobb.feedr.R;
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
    private static final String ARG_COURSEKEY = "course_key";


    private String mCourseKey;


    private OnSurveyClickListener mListener;



    private DatabaseReference mDataRef;
    private DatabaseReference mSurveysRef;
    private RecyclerView mSurveyTabRecyclerView;


    public SurveyListTabFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     *
     * @return A new instance of fragment SurveyListTabFragment.
     */

    public static SurveyListTabFragment newInstance(String course_key) {
        SurveyListTabFragment fragment = new SurveyListTabFragment();
        Bundle args = new Bundle();
        args.putString(ARG_COURSEKEY, course_key);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCourseKey = getArguments().getString(ARG_COURSEKEY);

        }
        Log.d("onCreate", "inside ");
        mDataRef = FirebaseDatabase.getInstance().getReference();
        mSurveysRef = mDataRef.child("surveys");



    }

    @Override
    public void onStart() {
        super.onStart();



        FirebaseRecyclerAdapter<Survey, SurveyViewHolder> adapter =
                new FirebaseRecyclerAdapter<Survey, SurveyViewHolder>(
                        Survey.class,
                        R.layout.row_course,
                        SurveyViewHolder.class,
                        mSurveysRef
                ) {

                    @Override
                    protected void populateViewHolder(SurveyViewHolder viewHolder, Survey model, int position) {

                        final String surveyN = model.getSurveyName();
                        final String surveyK = model.getRefCode();
                        viewHolder.surveyName.setText(surveyN);
                       // viewHolder.surveyKey.setText(surveyK);



                    }
                };

        mSurveyTabRecyclerView.setAdapter(adapter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_survey_list_tab, container, false);
        mSurveyTabRecyclerView = (RecyclerView) view.findViewById(R.id.survey_tab_recyclerView);
        mSurveyTabRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onSurveyClicked(uri);
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
        void onSurveyClicked(Uri uri);
    }

    public static class SurveyViewHolder extends RecyclerView.ViewHolder {

        TextView surveyName;
        TextView surveyKey;

        public SurveyViewHolder(View itemView) {
            super(itemView);

            surveyName = (TextView) itemView.findViewById(R.id.list_course_name);
            //surveyKey = (TextView) itemView.findViewById(R.id.list_course_code);

        }

    }
}
