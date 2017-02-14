package se.chalmers.exjobb.feedr.adapters;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

import se.chalmers.exjobb.feedr.fragments.LiveSessionFragment;
import se.chalmers.exjobb.feedr.models.Feedback;
import se.chalmers.exjobb.feedr.utils.SharedPreferencesUtils;

public class LiveSessionAdapter {

    private LiveSessionFragment mFragment;
    private DatabaseReference mDataRef;
    private DatabaseReference mCourseRef;
    private DatabaseReference mFeedsRef;
    private DatabaseReference mSessionsRef;
    private String sessionKey;
    private String mCourseKey;

    private ArrayList<Feedback> feeds = new ArrayList<>();

    public LiveSessionAdapter(LiveSessionFragment mFragment, String mCourseKey, String sessionKey) {
        this.mFragment = mFragment;
        this.mCourseKey = mCourseKey;
        this.sessionKey = sessionKey;


        mDataRef = FirebaseDatabase.getInstance().getReference();
        mCourseRef = mDataRef.child("courses").child(mCourseKey);
        mSessionsRef = mCourseRef.child("sessions");
        mFeedsRef = mDataRef.child("feedbacks");
        getFeeds();
    }

    public void getRatings(ArrayList<Feedback> feeds){
        int sumOfRatings = 0;
        int numberOfRatings = feeds.size();
        double overallRating = 0;
        for(Feedback f : feeds){
            int rating = f.getRating();
            sumOfRatings += rating;
        }

        overallRating = (double) sumOfRatings/numberOfRatings;

        mFragment.setRatings(overallRating);
        mFragment.setFeedbackNumber(feeds.size());
    }
    public void getFeeds(){

        Query feedsQuery = mFeedsRef.orderByChild("sessionKey").equalTo(sessionKey);
        feedsQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Feedback feed = dataSnapshot.getValue(Feedback.class);
                feeds.add(feed);
                getRatings(feeds);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
