package se.chalmers.exjobb.feedr.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import se.chalmers.exjobb.feedr.R;
import se.chalmers.exjobb.feedr.models.Course;
import se.chalmers.exjobb.feedr.models.Feedback;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CourseOverviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CourseOverviewFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_COURSE = "course";
    private static final String ARG_COURSEKEY = "courseKey";
    private static final String FEEDBACKS = "feedbacks";

    private Course mCourse;
    private String courseKey;

    private DatabaseReference mDataRef;
    private DatabaseReference mRatingsRef;




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


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_course_overview, container, false);
        final List<Double> ratings = new ArrayList<>();

        mDataRef = FirebaseDatabase.getInstance().getReference();
        mRatingsRef = mDataRef.child(FEEDBACKS);
        ratings.clear();
        mRatingsRef.orderByChild(courseKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> children = dataSnapshot.getChildren().iterator();

                while(children.hasNext()){
                    DataSnapshot item = children.next();
                    Feedback feed;
                    double rating;
                    feed = item.getValue(Feedback.class);
                    rating = feed.getRating();
                    ratings.add(rating);

                }

                Toast.makeText(getActivity(), String.valueOf(ratings.size()), Toast.LENGTH_LONG);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        TextView courseName = (TextView) view.findViewById(R.id.course_overview_name);
        TextView courseCode = (TextView) view.findViewById(R.id.course_overview_code);

        courseName.setText(mCourse.getName());
        courseCode.setText(courseKey);

        return view;
    }

}
