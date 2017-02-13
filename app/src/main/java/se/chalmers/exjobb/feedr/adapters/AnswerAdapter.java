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
import se.chalmers.exjobb.feedr.fragments.AnswerListFragment;
import se.chalmers.exjobb.feedr.models.Answer;
import se.chalmers.exjobb.feedr.utils.SharedPreferencesUtils;

/**
 * Created by matej on 2017-01-17.
 */

public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.ViewHolder> {
    private AnswerListFragment mAnswerListFragment;

    private ArrayList<Answer> mAnswers = new ArrayList<>();
    private String mQuestionKey;
    private DatabaseReference mAnswersRef;
    private DatabaseReference mDataRef;



    public AnswerAdapter(AnswerListFragment answerListFragment, String questionKey) {
        mAnswerListFragment = answerListFragment;
        mQuestionKey = questionKey;

        String surveyKey = SharedPreferencesUtils.getCurrentSurveyKey(answerListFragment.getContext());
        mDataRef = FirebaseDatabase.getInstance().getReference();
        mAnswersRef = mDataRef.child("surveys").child(surveyKey).child("answers");

        Query answersForQuestion = mAnswersRef.orderByChild("questionKey").equalTo(mQuestionKey);
        answersForQuestion.addChildEventListener(new AnswerAdapter.AnswersChildEventListener());



    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_course,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {


                holder.mAnswerTextView.setText(mAnswers.get(position).getAnswer());




    }

    @Override
    public int getItemCount() {
        return mAnswers.size();
    }

    public class AnswersChildEventListener implements ChildEventListener{

        private void add(DataSnapshot dataSnapshot){
            Answer answer = dataSnapshot.getValue(Answer.class);
            answer.setKey(dataSnapshot.getKey());
            mAnswers.add(answer);

        }

        private int remove(String key){
            for (Answer a : mAnswers){
                if(a.getKey().equals(key)){
                    int foundPos = mAnswers.indexOf(a);
                    mAnswers.remove(a);
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

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mAnswerTextView;
        private String answer;

        public ViewHolder(View itemView) {
            super(itemView);

            mAnswerTextView = (TextView) itemView.findViewById(R.id.card_course_name);
        }
    }
}
