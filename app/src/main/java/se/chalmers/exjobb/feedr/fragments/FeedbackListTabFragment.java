package se.chalmers.exjobb.feedr.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.w3c.dom.Text;

import se.chalmers.exjobb.feedr.R;
import se.chalmers.exjobb.feedr.models.Course;
import se.chalmers.exjobb.feedr.models.Feedback;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FeedbackListTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeedbackListTabFragment extends Fragment {

    private static final String ARG_COURSECODE = "courseCode";




    private String mCourseCode;

    private DatabaseReference mDataRef;
    private DatabaseReference mFeedbacksRef;
    private RecyclerView mRecyclerView;
    private Query feedbacksForCurrentCourse;



    public FeedbackListTabFragment() {
        // Required empty public constructor
    }


    public static FeedbackListTabFragment newInstance(String courseCode) {
        FeedbackListTabFragment fragment = new FeedbackListTabFragment();
        Bundle args = new Bundle();
        args.putString(ARG_COURSECODE, courseCode);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCourseCode = getArguments().getString(ARG_COURSECODE);

        }

        mDataRef = FirebaseDatabase.getInstance().getReference();
        mFeedbacksRef = mDataRef.child("feedbacks");

         feedbacksForCurrentCourse = mFeedbacksRef.orderByKey().equalTo(mCourseCode);




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feedbacks, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.feedbacks_tab_recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        FirebaseRecyclerAdapter<Feedback, FeedbacksViewHolder> adapter =
                new FirebaseRecyclerAdapter<Feedback, FeedbacksViewHolder>(
                        Feedback.class,
                        R.layout.row_course,
                        FeedbacksViewHolder.class,
                        feedbacksForCurrentCourse
                ) {
                    @Override
                    protected void populateViewHolder(FeedbacksViewHolder viewHolder, Feedback model, int position) {

                        String username = model.getUsername();
                        String feedback = model.getFeedback();

                        viewHolder.mUsernameTextView.setText(username);
                        viewHolder.mFeedbackTextView.setText(feedback);

                    }


                };

        mRecyclerView.setAdapter(adapter);


        return view;
    }

    public static class FeedbacksViewHolder extends RecyclerView.ViewHolder{

        private TextView mUsernameTextView;
        private TextView mFeedbackTextView;

        public FeedbacksViewHolder(View itemView) {
            super(itemView);

            mFeedbackTextView = (TextView) itemView.findViewById(R.id.list_course_name);
            mUsernameTextView = (TextView) itemView.findViewById(R.id.list_course_code);
        }


    }

}
