package se.chalmers.exjobb.feedr.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;

import se.chalmers.exjobb.feedr.R;
import se.chalmers.exjobb.feedr.fragments.CourseListFragment;
import se.chalmers.exjobb.feedr.models.Course;
import se.chalmers.exjobb.feedr.utils.SharedPreferencesUtils;

/**
 * Created by matej on 2017-01-15.
 */

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {

    private final CourseListFragment mCourseListFragment;

    private final CourseListFragment.OnCourseSelectedListener mCourseSelectedListener;
    private DatabaseReference mCoursesRef;
    private DatabaseReference mDataRef;
    private ArrayList<Course> mCourses = new ArrayList<>();

    public CourseAdapter(CourseListFragment courseListFragment,
                         CourseListFragment.OnCourseSelectedListener listener){


        mCourseListFragment = courseListFragment;
        mCourseSelectedListener = listener;
        mDataRef = FirebaseDatabase.getInstance().getReference();
        mCoursesRef = mDataRef.child("courses");

        Query query = mCoursesRef.orderByChild("teacher").equalTo("Uno Holmer");
        query.addChildEventListener(new CoursesChildEventListener());

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_course, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mCourseNameTextView.setText(mCourses.get(position).getName());
        holder.mCourseCodeTextView.setText(mCourses.get(position).getCode());
    }

    @Override
    public int getItemCount() {
       return mCourses.size();
    }

    class CoursesChildEventListener implements ChildEventListener{

        private void add(DataSnapshot dataSnapshot){
            Course course = dataSnapshot.getValue(Course.class);
            course.setKey(dataSnapshot.getKey());
            mCourses.add(course);

        }

        private int remove (String key) {
            for(Course course : mCourses){
                if(course.getKey().equals(key)){
                    int foundPos = mCourses.indexOf(course);
                    mCourses.remove(course);
                    return foundPos;

                }
            }
            return -1;
        }
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            add(dataSnapshot);
            notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                remove(dataSnapshot.getKey());
            add(dataSnapshot);
            notifyDataSetChanged();
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            int position = remove(dataSnapshot.getKey());
            if (position >= 0) {
                notifyItemRemoved(position);
            }
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mCourseNameTextView;
        private TextView mCourseCodeTextView;


        public ViewHolder(View itemView){
            super(itemView);
            mCourseNameTextView = (TextView) itemView.findViewById(R.id.list_course_name);
            mCourseCodeTextView = (TextView) itemView.findViewById(R.id.list_course_code);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            SharedPreferencesUtils.setCurrentCourseKey(mCourseListFragment.getContext(), mCourses.get(getAdapterPosition()).getKey());
            Course course = mCourses.get(getAdapterPosition());
            mCourseSelectedListener.onCourseSelected(course);
        }
    }
}
