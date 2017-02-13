package se.chalmers.exjobb.feedr.fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Random;

import se.chalmers.exjobb.feedr.R;
import se.chalmers.exjobb.feedr.models.Feedback;
import se.chalmers.exjobb.feedr.utils.SharedPreferencesUtils;


public class LiveSessionFragment extends Fragment {

    private LineGraphSeries<DataPoint> series;
    private DatabaseReference mFeedbacksRef;
    private DatabaseReference mDataRef;
    private GraphView graph;
    private String sessionKey;
    private ArrayList<Feedback> feeds;
    private static int xVal  = 0;
    public LiveSessionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionKey = SharedPreferencesUtils.getCurrentSession(getContext());
        feeds = new ArrayList<>();
        mDataRef = FirebaseDatabase.getInstance().getReference();
        mFeedbacksRef = mDataRef.child("feedbacks");
        Query feedbackQuery = mFeedbacksRef.orderByChild("sessionKey").equalTo(sessionKey);
        feedbackQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> feedb = dataSnapshot.getChildren();

                for(DataSnapshot feed : feedb){
                    Feedback f = feed.getValue(Feedback.class);
                  addEntry(f);
              }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        mSessionRef = mDataRef.child("courses").child(SharedPreferencesUtils.getCurrentCourseKey(getContext())).child("sessions").child(SharedPreferencesUtils.getCurrentSession(getContext())).child("feedbacks");
//
//        mSessionRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Iterable<DataSnapshot> feedb = dataSnapshot.getChildren();
//
//                for(DataSnapshot feed : feedb){
//                    Feedback f = feed.getValue(Feedback.class);
//                    addEntry(f);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_live_session, container, false);

        graph = (GraphView) view.findViewById(R.id.graph);

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

                mFeedbacksRef.push().setValue(f);
            }
        });
        // data
        series = new LineGraphSeries<DataPoint>();

        graph.addSeries(series);
        //customize graph
        Viewport graphViewport = graph.getViewport();
        graphViewport.setYAxisBoundsManual(true);
        graphViewport.setMinY(1);
        graphViewport.setMaxY(5);
        graphViewport.setScrollable(true);

        graphViewport.setXAxisBoundsManual(true);
        graphViewport.setMinX(1);
        graphViewport.setMaxX(10);

        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);
        graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);


        return view;
    }

    private void addEntry(Feedback f){


            int rating = f.getRating();
            long timestamp = f.getTimestamp();


            Date date = new Date();
            date.setTime(timestamp*1000);

            DataPoint dp = new DataPoint(xVal++,rating);

            series.appendData(dp, true, 25 );






        }

}
