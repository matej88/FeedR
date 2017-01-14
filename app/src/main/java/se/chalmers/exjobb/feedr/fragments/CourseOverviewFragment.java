package se.chalmers.exjobb.feedr.fragments;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import se.chalmers.exjobb.feedr.R;
import se.chalmers.exjobb.feedr.adapters.CourseTabViewPagerAdapter;
import se.chalmers.exjobb.feedr.models.Course;
import se.chalmers.exjobb.feedr.models.Feedback;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CourseOverviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CourseOverviewFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_COURSE = "selectedCourse";
    private static final String ARG_COURSEKEY = "courseKey";

    private static final String FEEDBACKS = "feedbacks";

    private Course mCourse;
    private String courseKey;

    private double mCurrentRating;

    private TextView courseRating;

    private ArrayList<Feedback> feeds = new ArrayList<>();
    // Stores two fragments on the main page
    private ViewPager mViewPagerSingleCourse;

    // Enables to swipe through the fragments on main page
    private TabLayout mTabLayoutSingleCourse;

  //  private DatabaseReference mDataRef;
  // private DatabaseReference mRatingsRef;




    public CourseOverviewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param course Parameter 1.
     * @return A new instance of fragment CourseOverviewFragment.
     */

    public static CourseOverviewFragment newInstance(Course course, String courseKey) {
        CourseOverviewFragment fragment = new CourseOverviewFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_COURSE, course);
        args.putString(ARG_COURSEKEY, courseKey);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCourse = getArguments().getParcelable(ARG_COURSE);
            courseKey = getArguments().getString(ARG_COURSEKEY);
        }
            Log.d("CourseFragment", "onCreate");


//
//        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
//        DatabaseReference mFeedRef = mRef.child(FEEDBACKS);
//
//        mFeedRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Log.d("onDataChange", "inside the listener");
//
//                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
//
//                for(DataSnapshot child : children){
//                    Feedback feed = child.getValue(Feedback.class);
//                    feeds.add(feed);
//                }
//
//                Log.d("onDataChange", "size of feeds array" + feeds.size());
//
//                mCurrentRating = getRating(feeds);
//                courseRating.setText(Double.toString(mCurrentRating));
//                Log.d("insideView", "totalNrOfRatings " + mCurrentRating);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.d("onCancelled", "inside onCanceled");
//            }
//        });



    }

    @Override
    public void onStart() {
        super.onStart();

        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mFeedRef = mRef.child(FEEDBACKS);

        mFeedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("onDataChange", "inside the listener");

                Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                for(DataSnapshot child : children){
                    Feedback feed = child.getValue(Feedback.class);
                    feeds.add(feed);
                }

                Log.d("onDataChange", "size of feeds array" + feeds.size());

                mCurrentRating = getRating(feeds);
                courseRating.setText(Double.toString(mCurrentRating));
                Log.d("insideView", "totalNrOfRatings " + mCurrentRating);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("onCancelled", "inside onCanceled");
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_course_overview, container, false);
        Log.d("onCreateView", "inside onCreateView");
        TextView courseName = (TextView) view.findViewById(R.id.course_overview_name);
        TextView courseCode = (TextView) view.findViewById(R.id.course_overview_code);
        courseRating = (TextView) view.findViewById(R.id.course_overview_rating);

        courseName.setText(mCourse.getName());
        courseCode.setText(mCourse.getCode());
        setupTabs(view);

        return view;
    }

    public double getRating(ArrayList<Feedback> feeds){
        Log.d("getRating", "Inside getRatings and the size of the array is " + feeds.size());

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
        CourseTabViewPagerAdapter adapter = new CourseTabViewPagerAdapter(getActivity().getSupportFragmentManager());
        Fragment fragment = SurveyListTabFragment.newInstance(mCourse.getCode());
        adapter.addFrag(fragment, "Surveys");


        mViewPager.setAdapter(adapter);
    }


}
