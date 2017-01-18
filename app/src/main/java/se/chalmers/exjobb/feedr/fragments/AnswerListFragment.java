package se.chalmers.exjobb.feedr.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import se.chalmers.exjobb.feedr.R;
import se.chalmers.exjobb.feedr.adapters.AnswerAdapter;
import se.chalmers.exjobb.feedr.adapters.QuestionAdapter;
import se.chalmers.exjobb.feedr.models.Answer;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AnswerListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AnswerListFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    //private static final String ARG_ANSWERS = "answers";
    private static final String ARG_QUESTIONKEY = "questionKey";




    private String mQuestionKey;

    private AnswerAdapter mAdapter;

    public AnswerListFragment() {
        // Required empty public constructor
    }


    public static AnswerListFragment newInstance(String questionKey) {
        AnswerListFragment fragment = new AnswerListFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_answer_list, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.answer_list_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        registerForContextMenu(recyclerView);
        mAdapter = new AnswerAdapter(this, mQuestionKey);
        recyclerView.setAdapter(mAdapter);
        return view;
    }

}
