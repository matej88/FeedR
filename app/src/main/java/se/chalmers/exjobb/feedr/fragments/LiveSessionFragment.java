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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.firebase.ui.database.FirebaseRecyclerAdapter;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;

import java.util.Random;

import se.chalmers.exjobb.feedr.R;
import se.chalmers.exjobb.feedr.adapters.LiveSessionAdapter;
import se.chalmers.exjobb.feedr.models.Feedback;
import se.chalmers.exjobb.feedr.utils.SharedPreferencesUtils;


public class LiveSessionFragment extends Fragment {

    private SessionCallback mListener;
    private String sessionKey;
    private LiveSessionAdapter mAdapter;

    private DatabaseReference mDataRef;
    private DatabaseReference mFeedsRef;

    private TextView mRating;
    private TextView mNrofFeeds;
    private RecyclerView mRecyclerView;

    private Query liveQuery;

    private int numberOfFeedbacks;
    private double sessionRating;

    public LiveSessionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionKey = SharedPreferencesUtils.getCurrentSession(getContext());
        String courseKey = SharedPreferencesUtils.getCurrentCourseKey(getContext());
        mAdapter = new LiveSessionAdapter(this,courseKey,sessionKey);

        mDataRef = FirebaseDatabase.getInstance().getReference();
        mFeedsRef = mDataRef.child("feedbacks");

        liveQuery = mFeedsRef.orderByChild("sessionKey").equalTo(sessionKey);

    }

    @Override
    public void onResume() {
        super.onResume();
        setRatings(sessionRating);
        setFeedbackNumber(numberOfFeedbacks);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_live_session, container, false);

        mRating = (TextView) view.findViewById(R.id.ls_rating);
        mNrofFeeds = (TextView) view.findViewById(R.id.ls_feedbacks);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.session_feedback_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        final FirebaseRecyclerAdapter<Feedback, LiveSessionFeedViewHolder> adapterX = new FirebaseRecyclerAdapter<Feedback, LiveSessionFeedViewHolder>(
                Feedback.class,
                R.layout.row_feedback,
                LiveSessionFeedViewHolder.class,
                liveQuery

        ) {
            @Override
            protected void populateViewHolder(LiveSessionFeedViewHolder viewHolder, Feedback model, int position) {
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



        mRecyclerView.setAdapter(adapterX);
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
                mListener.questionReplyLive(answer, feedbackKey);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);

        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void setFeedbackNumber(int size){
        numberOfFeedbacks = size;
        mNrofFeeds.setText(Integer.toString(numberOfFeedbacks));

    }
    public void setRatings(double rating){
        sessionRating = rating;
        String result = String.format("%.1f", sessionRating);
        mRating.setText(result);

    }

    public static class LiveSessionFeedViewHolder extends RecyclerView.ViewHolder {
        private TextView feedback;
        private TextView timestamp;
        private TextView replied;
        private TextView rating;

        public LiveSessionFeedViewHolder(View itemView) {
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
        if (context instanceof SessionCallback) {
            mListener = (SessionCallback) context;
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
    public interface SessionCallback {
        void questionReplyLive(String answer, String feedbackKey);

    }
}
