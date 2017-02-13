package se.chalmers.exjobb.feedr.fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import se.chalmers.exjobb.feedr.R;
import se.chalmers.exjobb.feedr.models.Feedback;
import se.chalmers.exjobb.feedr.utils.SharedPreferencesUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FeedbackListTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeedbackListTabFragment extends Fragment {

    private static final String ARG_COURSECODE = "courseCode";

    private FeedbackTabCallback mListener;


    private String mCourseKey;
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
            mCourseKey = getArguments().getString(ARG_COURSECODE);

        }

            mDataRef = FirebaseDatabase.getInstance().getReference();
             mFeedbacksRef = mDataRef.child("feedbacks");

            feedbacksForCurrentCourse = mFeedbacksRef.orderByChild("courseKey").equalTo(mCourseKey).limitToLast(10);




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feedbacks, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.feedbacks_tab_recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        final FirebaseRecyclerAdapter<Feedback, FeedbacksViewHolder> adapter =
                new FirebaseRecyclerAdapter<Feedback, FeedbacksViewHolder>(
                        Feedback.class,
                        R.layout.row_feedback,
                        FeedbacksViewHolder.class,
                        feedbacksForCurrentCourse
                ) {
                    @Override
                    protected void populateViewHolder(FeedbacksViewHolder viewHolder, Feedback model, int position) {
                        final  Feedback feed = model;
                        feed.setFeedbackKey(this.getRef(position).getKey());
                        String feedback = model.getFeedback();
                        int rating = model.getRating();
                        long timestamp = model.getTimestamp();
                        boolean replied = model.isReplied();
                        boolean isTeacher = SharedPreferencesUtils.getIsTeacher(getContext());
                        viewHolder.feedback.setText(feedback);
                        viewHolder.rating.setText(Integer.toString(rating));
                        viewHolder.setReplied(replied);
                        viewHolder.setTime(timestamp);

                        if(!replied && isTeacher){
                            viewHolder.replied.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    showAddAnswerDialog(feed);
                                }
                            });
                        }else if(replied){
                            viewHolder.replied.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    showAnswerDialog(feed);
                                }
                            });
                        }


                    }


                };

        mRecyclerView.setAdapter(adapter);


        return view;
    }


    @SuppressLint("InflateParams")
    public void showAnswerDialog(final Feedback feed){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_show_answer, null);
        final TextView answer = (TextView) view.findViewById(R.id.show_answ_answer_et);
        final TextView feedQuestion = (TextView) view.findViewById(R.id.show_feedback_text);
        final String feedbackKey = feed.getFeedbackKey();
        feedQuestion.setText(feed.getFeedback());
        answer.setText(feed.getAnswer());

        builder.setNegativeButton(android.R.string.cancel, null);

        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    @SuppressLint("InflateParams")
    public void showAddAnswerDialog(final Feedback feed){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_add_answer, null);
        final EditText addAnswer = (EditText) view.findViewById(R.id.add_answ_answer_et);
        final TextView feedQuestion = (TextView) view.findViewById(R.id.feedback_text);
        feedQuestion.setText(feed.getFeedback());
        final String feedbackKey = feed.getFeedbackKey();


        builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String answer = addAnswer.getText().toString();
                mListener.questionReply(answer, feedbackKey);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);

        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public static class FeedbacksViewHolder extends RecyclerView.ViewHolder{

        private TextView feedback;
        private TextView timestamp;
        private TextView replied;
        private TextView rating;

        public FeedbacksViewHolder(View itemView) {
            super(itemView);

             feedback = (TextView) itemView.findViewById(R.id.feedback_text);
             timestamp = (TextView) itemView.findViewById(R.id.feedback_timestamp);
             replied = (TextView) itemView.findViewById(R.id.feedback_replied);
            rating = (TextView) itemView.findViewById(R.id.feedback_rating);

        }

        public void setReplied(boolean rep){
            if(rep){
                replied.setTextColor(Color.parseColor("#229EE6"));

            }
        }

        public void setTime(long time){
            try{
                DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date netDate = (new Date(time));
                String t = sdf.format(netDate);
                timestamp.setText(t);
            }
            catch(Exception ex){
                timestamp.setText("N/A");
            }
        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FeedbackListTabFragment.FeedbackTabCallback) {
            mListener = (FeedbackListTabFragment.FeedbackTabCallback) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement questionReply");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    public interface FeedbackTabCallback {
        void questionReply(String answer, String feedbackKey);

    }
}
