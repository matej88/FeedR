package se.chalmers.exjobb.feedr.fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.Series;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Random;

import se.chalmers.exjobb.feedr.R;
import se.chalmers.exjobb.feedr.adapters.LiveSessionAdapter;
import se.chalmers.exjobb.feedr.models.Feedback;
import se.chalmers.exjobb.feedr.utils.SharedPreferencesUtils;


public class LiveSessionFragment extends Fragment {


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

            }
        };



        Button btn = (Button) view.findViewById(R.id.send);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String courseKey = SharedPreferencesUtils.getCurrentCourseKey(getContext());
                Random ran = new Random();
                int low = 1;
                int high = 6;
                int res = ran.nextInt(high - low) + low;

                long unixTime = System.currentTimeMillis() / 1000L;


                Feedback f = new Feedback("feed", res , unixTime, sessionKey, courseKey,false);

                mFeedsRef.push().setValue(f);
            }
        });

        mRecyclerView.setAdapter(adapterX);
        return view;
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
}
