package se.chalmers.exjobb.feedr.fragments;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import se.chalmers.exjobb.feedr.R;
import se.chalmers.exjobb.feedr.adapters.CourseTabViewPagerAdapter;
import se.chalmers.exjobb.feedr.models.Course;
import se.chalmers.exjobb.feedr.models.Feedback;
import se.chalmers.exjobb.feedr.models.Session;
import se.chalmers.exjobb.feedr.utils.SharedPreferencesUtils;



public class CourseOverviewFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_COURSE = "course";

    private CourseOverviewCallback mListener;
    private Course mCourse;

    // Stores two fragments on the main page
    private ViewPager mViewPagerSingleCourse;

    // Enables to swipe through the fragments on main page
    private TabLayout mTabLayoutSingleCourse;


    private DatabaseReference mDataRef;
    private DatabaseReference mCourseRef;


    //GUI
    private TextView courseName;
    private TextView courseCode;
    private TextView courseStatus;
    private TextView openSession;
    private TextView courseRatings;
    private SwitchCompat onOffSwitch;
    private boolean courseIsOnline;
    private RelativeLayout onOffLayout;
    // to store number of sessions
    private int numberOfSess;

    // store ratings of all sesions
    private double courseRating;

    public CourseOverviewFragment() {
        // Required empty public constructor
    }

    public static CourseOverviewFragment newInstance(Course course) {
        CourseOverviewFragment fragment = new CourseOverviewFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_COURSE, course);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCourse = getArguments().getParcelable(ARG_COURSE);
        }
        //courseKey = SharedPreferencesUtils.getCurrentCourseKey(getContext());
        mDataRef = FirebaseDatabase.getInstance().getReference();

        //get the reference for the selected course
        mCourseRef = mDataRef.child("courses").child(mCourse.getKey());

        mCourseRef.addValueEventListener(new CourseValueEventListener());

        getFeedbacks();
    }

    private class CourseValueEventListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Course course = dataSnapshot.getValue(Course.class);
            mCourse = course;

            if(course.getSessions() != null){
                numberOfSess = course.getSessions().size();

            }else{
                numberOfSess = 0;
            }

            updateGUI(course);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }


    public void getFeedbacks(){
            DatabaseReference feedbackRefs = mDataRef.child("feedbacks");
            String courseKey = SharedPreferencesUtils.getCurrentCourseKey(getContext());
             final ArrayList<Feedback> feeds = new ArrayList<>();

            Query feedsQuery = feedbackRefs.orderByChild("courseKey").equalTo(courseKey);
            feedsQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                  Iterable<DataSnapshot> feedbacks = dataSnapshot.getChildren();


                    for( DataSnapshot ds : feedbacks){
                        Feedback f = ds.getValue(Feedback.class);
                        feeds.add(f);
                    }
                    getCourseRating(feeds);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
    }

    public void getCourseRating(ArrayList<Feedback> feeds){
        int totalNrOfRatings = feeds.size();
        int ratingsAdded = 0;
        for(int i = 0; i < totalNrOfRatings; i++){
            ratingsAdded += feeds.get(i).getRating();
        }
        if(ratingsAdded != 0) {
            double temp = ratingsAdded / totalNrOfRatings;
            Toast.makeText(getContext(), "Rating = " + temp, Toast.LENGTH_SHORT).show();
        }else{
            double temp = 0;
            Toast.makeText(getContext(), "Rating = " + temp, Toast.LENGTH_SHORT).show();
        }

    }
    public void updateRatings(double rating){
        courseRatings.setText(String.valueOf(rating));
       // Toast.makeText(getContext(), "Rating = " + rating, Toast.LENGTH_SHORT).show();
    }

    public void updateGUI(Course course){
        courseName.setText(course.getName());
        courseCode.setText(course.getCode());

        if(course.isOnline()){
            courseIsOnline = true;
            onOffLayout.setBackgroundColor(Color.parseColor("#66bb6a"));
            courseStatus.setText("Online");
            openSession.setText("Open session");
            openSession.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.startSession();
                }
            });
            onOffSwitch.setChecked(true);
        }else{
            courseIsOnline = false;
            onOffLayout.setBackgroundColor(Color.parseColor("#ef5350"));
            courseStatus.setText("Offline");
            openSession.setText("");
            onOffSwitch.setChecked(false);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();


    }

    @Override
    public void onResume() {
        super.onResume();
        updateGUI(mCourse);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final Context context = getContext();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_course_overview, container, false);

        courseName = (TextView) view.findViewById(R.id.course_overview_name);
        courseCode = (TextView) view.findViewById(R.id.course_overview_code);
        courseStatus = (TextView) view.findViewById(R.id.course_overview_status);
        courseRatings = (TextView) view.findViewById(R.id.course_overview_rating);
        openSession = (TextView) view.findViewById(R.id.course_overview_open_session);
        onOffSwitch = (SwitchCompat) view.findViewById(R.id.course_switch);
        onOffLayout = (RelativeLayout) view.findViewById(R.id.onoff_layout);
        switchListener();
        setupTabs(view);

        return view;
    }

    public void switchListener(){
        onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean switchedOn) {
                if(!courseIsOnline && switchedOn){
                    // set online value in database to true
                    DatabaseReference onlineRef = mCourseRef.child("online");
                    onlineRef.setValue(true);



                    // start a new session and store it in a database
                    DatabaseReference sessionRef = mCourseRef.child("sessions");
                    Session s = new Session(numberOfSess+1,false);
                    String sessionKey = sessionRef.push().getKey();
                    SharedPreferencesUtils.setCurrentSessionKey(getContext(), sessionKey);
                    sessionRef.child(sessionKey).setValue(s);
                    mListener.startSession();
                    Toast.makeText(getContext(), "The course was offline but is now online", Toast.LENGTH_SHORT).show();

                }else if(courseIsOnline && !switchedOn){
                    Toast.makeText(getContext(), "The course is offline", Toast.LENGTH_SHORT).show();
                    mCourseRef.child("online").setValue(false);
                }

            }
        });
    }



    public double getRating(ArrayList<Feedback> feeds){
        int totalNrOfRatings = feeds.size();
        int ratingsAdded = 0;
        for(int i = 0; i < totalNrOfRatings; i++){
            ratingsAdded += feeds.get(i).getRating();
        }
        double temp = ratingsAdded/totalNrOfRatings;
        return temp;
    }


    public void setupTabs(View view){
        mViewPagerSingleCourse = (ViewPager) view.findViewById(R.id.viewpager_single_course);
        //  if there are fragments added to ViewPager then set it up
        if (mViewPagerSingleCourse != null){
            setupViewPager(mViewPagerSingleCourse);
        }


        mTabLayoutSingleCourse = (TabLayout) view.findViewById(R.id.tabLayout_single_course);
        mTabLayoutSingleCourse.setupWithViewPager(mViewPagerSingleCourse);

        // What happens if user swipes through the fragments
        mTabLayoutSingleCourse.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // show the page that is swiped to
                mViewPagerSingleCourse.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    // Populate the adapter with fragments
    private void setupViewPager(ViewPager mViewPager){

        // call getChildFragmentManager when a fragment is inside another fragment
        CourseTabViewPagerAdapter adapter = new CourseTabViewPagerAdapter(getChildFragmentManager());
        Fragment fragment = SurveyListTabFragment.newInstance(mCourse.getKey());
        adapter.addFrag(fragment, "Surveys");
        Fragment fragment1 = FeedbackListTabFragment.newInstance(mCourse.getKey());
        adapter.addFrag(fragment1, "Feedbacks");

        mViewPager.setAdapter(adapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CourseOverviewCallback) {
            mListener = (CourseOverviewCallback) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement CourseOverviewCallback");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface CourseOverviewCallback {
        void startSession();
    }

}
