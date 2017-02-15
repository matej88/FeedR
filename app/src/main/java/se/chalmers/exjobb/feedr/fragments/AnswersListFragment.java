package se.chalmers.exjobb.feedr.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import se.chalmers.exjobb.feedr.R;
import se.chalmers.exjobb.feedr.models.Answer;
import se.chalmers.exjobb.feedr.utils.SharedPreferencesUtils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AnswersListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AnswersListFragment extends Fragment {
    private static final String ARG_QUESTIONKEY = "questionKey";

    private String mQuestionKey;
    private RecyclerView mRecyclerView;
    private DatabaseReference mDataRef;
    private DatabaseReference mAnswersRef;
    private String courseKey;


    public AnswersListFragment() {
        // Required empty public constructor
    }



    public static AnswersListFragment newInstance(String questionKey) {
        AnswersListFragment fragment = new AnswersListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_QUESTIONKEY, questionKey);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mQuestionKey = getArguments().getString(ARG_QUESTIONKEY);
        }

        String surveyKey = SharedPreferencesUtils.getCurrentSurveyKey(getContext());
        courseKey = SharedPreferencesUtils.getCurrentCourseKey(getContext());
        mDataRef = FirebaseDatabase.getInstance().getReference();
        mAnswersRef = mDataRef.child("surveys").child(surveyKey).child("answers");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_answers_list, container, false);

        mRecyclerView = (RecyclerView)view.findViewById(R.id.answers_recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        final FirebaseRecyclerAdapter<Answer, AnswersViewHolder> adapter3 = new FirebaseRecyclerAdapter<Answer, AnswersViewHolder>(
                Answer.class,
                R.layout.row_session,
                AnswersViewHolder.class,
                mAnswersRef.orderByChild("questionKey").equalTo(mQuestionKey)
        ) {
            @Override
            protected void populateViewHolder(AnswersViewHolder viewHolder, Answer model, int position) {
                        String answer = model.getAnswer();
                        viewHolder.answer.setText(answer);
            }
        };


        mRecyclerView.setAdapter(adapter3);
        return view;
    }

     public static class AnswersViewHolder extends RecyclerView.ViewHolder {
         private TextView answer;

         public AnswersViewHolder(View itemView) {
             super(itemView);

             answer = (TextView) itemView.findViewById(R.id.row_session_tv);
         }
     }
}
