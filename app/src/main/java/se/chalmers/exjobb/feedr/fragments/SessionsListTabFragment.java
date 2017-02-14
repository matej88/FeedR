package se.chalmers.exjobb.feedr.fragments;

import android.content.Context;
import android.net.Uri;
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
import se.chalmers.exjobb.feedr.models.Session;
import se.chalmers.exjobb.feedr.utils.SharedPreferencesUtils;


public class SessionsListTabFragment extends Fragment {


    private static final String ARG_COURSEKEY = "current_course_key";



    private String mCurrentCourseKey;
    private RecyclerView mRecyclerView;
    private DatabaseReference mDataRef;
    private DatabaseReference mSessionsRef;


    private OnSessionsListCallback mListener;

    public SessionsListTabFragment() {
        // Required empty public constructor
    }



    public static SessionsListTabFragment newInstance(String courseKey) {
        SessionsListTabFragment fragment = new SessionsListTabFragment();
        Bundle args = new Bundle();
        args.putString(ARG_COURSEKEY, courseKey);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCurrentCourseKey = getArguments().getString(ARG_COURSEKEY);
        }


        mDataRef = FirebaseDatabase.getInstance().getReference();
        mSessionsRef = mDataRef.child("courses").child(mCurrentCourseKey).child("sessions");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sessions_list_tab, container, false);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.sessions_tab_recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        final FirebaseRecyclerAdapter<Session, SessionsViewHolder> adapter2 = new FirebaseRecyclerAdapter<Session, SessionsViewHolder>(

                Session.class,
                R.layout.row_session,
                SessionsViewHolder.class,
                mSessionsRef
        ) {
            @Override
            protected void populateViewHolder(SessionsViewHolder viewHolder, Session model, int position) {
                        String sessionKey = getRef(position).getKey();

                        SharedPreferencesUtils.setCurrentSessionKey(getContext(),sessionKey);
                        int id = model.getSessionId();
                        viewHolder.mListener = mListener;
                        viewHolder.setSessionId(id);
                        viewHolder.sessionK = sessionKey;
            }
        };

        mRecyclerView.setAdapter(adapter2);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSessionsListCallback) {
            mListener = (OnSessionsListCallback) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSessionsListCallback");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnSessionsListCallback {

        void onSessionClicked(String sessionKey);
    }

    public static class SessionsViewHolder extends RecyclerView.ViewHolder{
        private TextView sessionID;
        private String sessionK;
        private OnSessionsListCallback mListener;

        public SessionsViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onSessionClicked(sessionK);
                }
            });
            sessionID = (TextView) itemView.findViewById(R.id.row_session_tv);
        }

        public void setSessionId(int id){
            sessionID.setText("Session " + id );
        }


    }
}
