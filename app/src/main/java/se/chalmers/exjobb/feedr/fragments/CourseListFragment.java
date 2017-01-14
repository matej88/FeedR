package se.chalmers.exjobb.feedr.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

import se.chalmers.exjobb.feedr.R;
import se.chalmers.exjobb.feedr.models.Course;
import se.chalmers.exjobb.feedr.models.Feedback;


public class CourseListFragment extends Fragment {

    public static final String COURSES = "courses";
    private OnCourseSelectedListener mClickListener;
    private DatabaseReference mDataRef;
    private DatabaseReference mCoursesRef;
    private RecyclerView mRecyclerView;

    public CourseListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_courses_list, container, false);

        mDataRef = FirebaseDatabase.getInstance().getReference();
        mCoursesRef = mDataRef.child(COURSES);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.course_list_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                  .setAction("Action", null).show();

                showAddCourseDialog(null);

            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();



        FirebaseRecyclerAdapter<Course, CoursesViewHolder> adapter =
                new FirebaseRecyclerAdapter<Course, CoursesViewHolder>(
                        Course.class,
                        R.layout.row_course,
                        CoursesViewHolder.class,
                        mCoursesRef
                ) {
                    @Override
                    protected void populateViewHolder(CoursesViewHolder viewHolder, Course model, int position) {

                        final String courseKey = this.getRef(position).getKey();
                        final Course selC = model;
                        viewHolder.courseNameTextView.setText(model.getName());
                        viewHolder.courseCodeTextView.setText(model.getCode());

                        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mClickListener.onCourseSelected(selC, courseKey);
                                }
                        });
                    }
                };

        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCourseSelectedListener) {
            mClickListener = (OnCourseSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCourseSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mClickListener = null;
        mCoursesRef.onDisconnect();

    }



    @SuppressLint("InflateParams")
    public void showAddCourseDialog(final Course course){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_add_course, null);

        final EditText courseNameEditText = (EditText) view.findViewById(R.id.dialog_add_course_name);
        final EditText courseCodeEditText = (EditText) view.findViewById(R.id.dialog_add_course_code);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String courseName = courseNameEditText.getText().toString();
                        String courseCode = courseCodeEditText.getText().toString();
                        Course c = new Course(courseName,courseCode,"Uno Holmer");

                        mClickListener.onAddCourse(c);
                        Toast.makeText(getActivity(), "Created Course", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, null);

        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();


//
//        DialogFragment df = new DialogFragment() {
//            @NonNull
//            @Override
//            public Dialog onCreateDialog(Bundle savedInstanceState) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                builder.setTitle("Add Course");
//
//                final View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_add_course, null);
//                builder.setView(view);
//                final EditText courseNameEditText = (EditText) view.findViewById(R.id.dialog_add_course_name);
//                final EditText courseCodeEditText = (EditText) view.findViewById(R.id.dialog_add_course_code);
//
//                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        String courseName = courseNameEditText.getText().toString();
//                        String courseCode = courseCodeEditText.getText().toString();
//                        Course c = new Course(courseName,courseCode,"Uno Holmer");
//
//                        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                              .setAction("Action", null).show();
//                    }
//                });
//                builder.setNegativeButton(android.R.string.cancel, null);
//
//                return builder.create();
//            }
//        };
//        df.show(this.getFragmentManager(), "Add course");
    }

    public static class CoursesViewHolder extends RecyclerView.ViewHolder {

        TextView courseNameTextView;
        TextView courseCodeTextView;

        public CoursesViewHolder(View itemView) {
            super(itemView);

            courseNameTextView = (TextView) itemView.findViewById(R.id.list_course_name);
            courseCodeTextView = (TextView) itemView.findViewById(R.id.list_course_code);
        }




    }


    public interface OnCourseSelectedListener {
        void onCourseSelected(Course selectedCourse, String courseKey);
       void onAddCourse(Course newCourse);
    }
}
