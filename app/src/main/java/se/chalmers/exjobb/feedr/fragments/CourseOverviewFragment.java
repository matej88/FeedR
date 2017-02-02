package se.chalmers.exjobb.feedr.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import se.chalmers.exjobb.feedr.R;
import se.chalmers.exjobb.feedr.adapters.CourseTabViewPagerAdapter;
import se.chalmers.exjobb.feedr.models.Course;
import se.chalmers.exjobb.feedr.models.Feedback;
import se.chalmers.exjobb.feedr.models.Session;
import se.chalmers.exjobb.feedr.utils.SharedPreferencesUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CourseOverviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CourseOverviewFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_COURSE = "course";
    private DatabaseReference mDataRef;
    private DatabaseReference onlineRef;
    private DatabaseReference sessionNr;
    private DatabaseReference sessionRef;

    private TextView courseName;
    private TextView courseCode;

    private Switch onOff;
    private TextView onOffTV;

    private Course mCourse;
    private TextView courseRating;

    private Boolean isOnline;
    private Boolean sessionOnline;
    private ArrayList<Feedback> feeds = new ArrayList<>();
    // Stores two fragments on the main page
    private ViewPager mViewPagerSingleCourse;

    // Enables to swipe through the fragments on main page
    private TabLayout mTabLayoutSingleCourse;

    private int sessNr;




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
        mDataRef = FirebaseDatabase.getInstance().getReference();
        onlineRef = mDataRef.child("courses").child(mCourse.getKey()).child("online");
        sessionRef = mDataRef.child("courses").child(mCourse.getKey()).child("sessions");
        sessionNr = mDataRef.child("courses").child(mCourse.getKey()).child("sessions");
        getSessionNr();

        onlineRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                isOnline = (Boolean) dataSnapshot.getValue();
                oniOffi();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }




    public void getSessionNr(){

        sessionNr.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                    int i = 0;

                if(children != null) {
                    for (DataSnapshot s : children) {
                            i++;
                    }
                }else{
                    Toast.makeText(getContext(), "Sessions = null", Toast.LENGTH_SHORT).show();
                }



                sessNr = i;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final Context context = getContext();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_course_overview, container, false);


        courseName = (TextView) view.findViewById(R.id.course_overview_name);
        courseCode = (TextView) view.findViewById(R.id.course_overview_code);
        courseName.setText(mCourse.getName());
        courseCode.setText(mCourse.getCode());

        onOffTV = (TextView) view.findViewById(R.id.textViewOnOff);
        onOff = (Switch) view.findViewById(R.id.course_switch);


        onOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean online) {
                if(!isOnline && online){
                    onlineRef.setValue(true);

                    Toast.makeText(context, "Its online now", Toast.LENGTH_SHORT).show();
                    isOnline = true;

                    Session s = new Session(sessNr+1,false, ServerValue.TIMESTAMP);
                    String key = sessionRef.push().getKey();
                    SharedPreferencesUtils.setCurrentSessionKey(context, key);
                    sessionRef.child(key).setValue(s);
                    Toast.makeText(context, "session key is " + key, Toast.LENGTH_SHORT).show();
                    openSessionsFragment();
                }
                else if(!online){
                    onlineRef.setValue(false);

                    Toast.makeText(context, "its offline now", Toast.LENGTH_LONG).show();
                }
            }
        });
        setupTabs(view);

        return view;
    }

    private void openSessionsFragment(){
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        LiveSessionFragment fragment = new LiveSessionFragment();
        ft.replace(R.id.fragment_container, fragment);
        ft.addToBackStack("back_to_course_overview");
        ft.commit();
    }
    public void oniOffi(){
        if(isOnline){
            onOff.setChecked(true);
            onOffTV.setText("Online");
            TextView check = (TextView) getView().findViewById(R.id.textView3);
            check.setText("View session");
            check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openSessionsFragment();
                }
            });
        } else{
            onOff.setChecked(false);
            onOffTV.setText("Offi");

        }
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

    public void updateGui(Course course){
        courseName.setText(course.getName());
        courseCode.setText(course.getCode());

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


    private class CourseEventListener implements ChildEventListener {


        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            mCourse = dataSnapshot.getValue(Course.class);
            Toast.makeText(getContext(), "This is course code" + mCourse.getName(), Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                mCourse = dataSnapshot.getValue(Course.class);
                Toast.makeText(getContext(), "This is course code" + mCourse.getName(), Toast.LENGTH_SHORT).show();
                updateGui(mCourse);

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
}
