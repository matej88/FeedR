package se.chalmers.exjobb.feedr.adapters;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import se.chalmers.exjobb.feedr.fragments.CourseOverviewFragment;
import se.chalmers.exjobb.feedr.models.Feedback;
import se.chalmers.exjobb.feedr.models.Session;


public class CourseOverviewAdapter {

    private final CourseOverviewFragment mOverviewFragment;
    private DatabaseReference mDataRef;
    private DatabaseReference mCourseRef;
    private DatabaseReference mFeedRef;
    private DatabaseReference mSurveyRef;
    private String mCourseKey;

    private ArrayList<Feedback> feeds = new ArrayList<>();


    public CourseOverviewAdapter(CourseOverviewFragment mOverviewFragment, String courseKey) {
        this.mOverviewFragment = mOverviewFragment;
        mCourseKey = courseKey;
        mDataRef = FirebaseDatabase.getInstance().getReference();
        mCourseRef = mDataRef.child("courses").child(courseKey);
        mFeedRef = mDataRef.child("feedbacks");

        getFeeds();
        getSessions();
        getStudentNumber();
    }

    /************************** Ratings & feedbacks ************************************/

    public void getFeeds(){
        Query feedsQuery = mFeedRef.orderByChild("courseKey").equalTo(mCourseKey);
        feedsQuery.addChildEventListener(new FeedsChildEventListener());

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
        Log.d("TAG", "The rating is " + overallRating);
        mOverviewFragment.setRatings(overallRating);
        mOverviewFragment.setFeedbackNumber(feeds.size());
    }
    class FeedsChildEventListener implements ChildEventListener{


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
    }


    /************ Sessions ***************/

    public void getSessions(){
        Query sessionsRef = mCourseRef.child("sessions");
        sessionsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Session> sessions = new ArrayList<Session>();

                Iterable<DataSnapshot> sess = dataSnapshot.getChildren();

                for(DataSnapshot ds : sess){
                    Session s = ds.getValue(Session.class);
                    sessions.add(s);
                }

                mOverviewFragment.setSessionsNumber(sessions.size());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    /************ NrOfStudents ***************/

    public void getStudentNumber(){

        mCourseRef.child("subscribers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long temp = dataSnapshot.getChildrenCount();
                int size = (int) temp;
                mOverviewFragment.setSubscribersNumber(size);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
