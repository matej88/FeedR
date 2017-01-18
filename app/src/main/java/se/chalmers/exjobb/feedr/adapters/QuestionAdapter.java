package se.chalmers.exjobb.feedr.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

import se.chalmers.exjobb.feedr.R;
import se.chalmers.exjobb.feedr.fragments.SurveyOverviewFragment;
import se.chalmers.exjobb.feedr.models.Answer;
import se.chalmers.exjobb.feedr.models.Question;
import se.chalmers.exjobb.feedr.models.Survey;


/**
 * Created by matej on 2017-01-17.
 */

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {

    private final SurveyOverviewFragment.OnSurveyQuestionClickedListener mQuestionClickedListener;
    private SurveyOverviewFragment mSurveyOverviewFragment;
    private String mSurveyKey;
    private ArrayList<Question> mQuestions;
    private DatabaseReference mQuestionsRef;
    private DatabaseReference mDataRef;



    public QuestionAdapter(SurveyOverviewFragment surveyOverviewFragment, String surveyKey,
                           SurveyOverviewFragment.OnSurveyQuestionClickedListener questionClickedListener) {

                mSurveyOverviewFragment = surveyOverviewFragment;
                mSurveyKey = surveyKey;
                mQuestionClickedListener = questionClickedListener;
                mQuestions = new ArrayList<Question>();

        mDataRef = FirebaseDatabase.getInstance().getReference();
        mQuestionsRef = mDataRef.child("surveys").child(surveyKey).child("questions");

        mQuestionsRef.addChildEventListener(new QuestionAdapter.QuestionsChildEventListener());

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_course,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mQuestionTextView.setText(mQuestions.get(position).getQuestion());
        holder.mQuestionKey = mQuestions.get(position).getKey();
    }

    @Override
    public int getItemCount() {
        return mQuestions.size();
    }

    private class QuestionsChildEventListener implements ChildEventListener{

        private void add(DataSnapshot dataSnapshot){
            Question question = dataSnapshot.getValue(Question.class);
            question.setKey(dataSnapshot.getKey());
            mQuestions.add(question);

        }

        private int remove(String key){
            for (Question q : mQuestions){
                if(q.getKey().equals(key)){
                    int foundPos = mQuestions.indexOf(q);
                    mQuestions.remove(q);
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView mQuestionTextView;
        private String mQuestionKey;

        public ViewHolder(View itemView) {
            super(itemView);
            mQuestionTextView = (TextView) itemView.findViewById(R.id.list_course_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            mQuestionClickedListener.onQuestionClicked(mQuestionKey);

        }
    }
}
