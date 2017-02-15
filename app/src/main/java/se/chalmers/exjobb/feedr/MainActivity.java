package se.chalmers.exjobb.feedr;


import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.Map;

import se.chalmers.exjobb.feedr.fragments.AddSurveyFragment;

import se.chalmers.exjobb.feedr.fragments.AnswersListFragment;
import se.chalmers.exjobb.feedr.fragments.CourseListFragment;
import se.chalmers.exjobb.feedr.fragments.CourseOverviewFragment;
import se.chalmers.exjobb.feedr.fragments.FeedbackListTabFragment;
import se.chalmers.exjobb.feedr.fragments.LiveSessionFragment;
import se.chalmers.exjobb.feedr.fragments.LoginFragment;
import se.chalmers.exjobb.feedr.fragments.RegisterFragment;
import se.chalmers.exjobb.feedr.fragments.SessionsListTabFragment;
import se.chalmers.exjobb.feedr.fragments.SurveyListTabFragment;
import se.chalmers.exjobb.feedr.fragments.SurveyOverviewFragment;

import se.chalmers.exjobb.feedr.models.Course;

import se.chalmers.exjobb.feedr.models.Question;
import se.chalmers.exjobb.feedr.models.Survey;
import se.chalmers.exjobb.feedr.utils.SharedPreferencesUtils;

public class MainActivity extends AppCompatActivity implements

        CourseListFragment.OnCourseSelectedListener,
        SurveyListTabFragment.OnSurveyClickListener,
        AddSurveyFragment.OnSurveyAddListener,
        SurveyOverviewFragment.OnSurveyQuestionClickedListener,
        LoginFragment.OnLoginListener,
        RegisterFragment.onRegisterListener,
        CourseOverviewFragment.CourseOverviewCallback,
        FeedbackListTabFragment.FeedbackTabCallback,
        SessionsListTabFragment.OnSessionsListCallback,
        LiveSessionFragment.SessionCallback

{
    private DatabaseReference mDataRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private OnCompleteListener mOnCompleteListener;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // if the android version of the smartphone is android 5.0 or greater then disable the shadows from the GUI
        // These shadows are meant to make the GUI for older phones look like Google Material Design. This is done for the esthetics
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
           // findViewById(R.id.gradientShadow).setVisibility(View.GONE);
        }
        setupToolbar();
        mAuth = FirebaseAuth.getInstance();
        mDataRef = FirebaseDatabase.getInstance().getReference();
        mDataRef.keepSynced(true);


      //  setupNavigationDrawer();




        // make the LoginFragment first page
        switchToLoginFragment();

        initializeListeners();


    }

    private void switchToLoginFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, new LoginFragment(), "Login");
        ft.commit();
    }


    // ---------------------------- Initialize some GUI components ---------------------------------

    public void setupToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mTitle.setText("FeedLoop");
    }

//    public void setupNavigationDrawer(){
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);
//
//    }


    // Switch to CourseListFragment
    private void switchToCourseListFragment(String teacherUid) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        CourseListFragment fragment = CourseListFragment.newInstance(teacherUid);
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }

    // ---------------------------- Firebase user authentication -----------------------------------


    // Initialize listeners
    private void initializeListeners() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    final String teacherUid = user.getUid();
                    SharedPreferencesUtils.setIsTeacher(getApplicationContext(), true);
                    mDataRef.child("users/teachers/" + user.getUid() + "/name").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String teacherName = dataSnapshot.getValue(String.class);
                            // Save current teachers name in SharedPreferences
                            SharedPreferencesUtils.setCurrentTeacherName(getApplicationContext(), teacherName);

                            // Save current teachers uid in SharedPreferences
                            SharedPreferencesUtils.setCurrentTeacherUid(getApplicationContext(), teacherUid);
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    // if user exists go to CourseListFragment
                    switchToCourseListFragment(teacherUid);
                } else {
                    switchToLoginFragment();
                }
            }
        };

        // if Login is not complete then show AlertDialog error
        mOnCompleteListener = new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (!task.isSuccessful()) {
                    showLoginError("Login Failed");
                }

            }
        };
    }

    // Get the error message for failed login from LoginFragment
    private void showLoginError(String message) {
        LoginFragment loginFragment = (LoginFragment) getSupportFragmentManager().findFragmentByTag("Login");
        loginFragment.onLoginError(message);
    }


    // Check and connect user to a database when Login pressed in LoginFragment
    @Override
    public void onLogin(String email, String passwrod) {
        mAuth.signInWithEmailAndPassword(email, passwrod)
                .addOnCompleteListener(mOnCompleteListener);
    }

    // Sign out when user signs out
    @Override
    public void onLogout() {
        mAuth.signOut();
    }


    // Register user
    @Override
    public void onRegisterClicked(final String name, String email, String password) {
        final String userName = name;
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String userUid = mAuth.getCurrentUser().getUid();
                    DatabaseReference currentUserRef = mDataRef.child("users/teachers/" + userUid);
                    currentUserRef.child("name").setValue(userName);
                    switchToCourseListFragment(userUid);
                }
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    @Override
    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
            super.onBackPressed();
        //}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_signout) {
            onLogout();
        }

        return super.onOptionsItemSelected(item);
    }
//
//    @SuppressWarnings("StatementWithEmptyBody")
//    @Override
//    public boolean onNavigationItemSelected(MenuItem item) {
//
//        Fragment switchTo = null;
//
//        // Handle navigation view item clicks here.
//        switch (item.getItemId()) {
//            case R.id.nav_courses:
//                switchTo = new CourseListFragment();
//                break;
//            case R.id.nav_surveys:
//                break;
//            case R.id.nav_logout:
//                onLogout();
//
//        }
//
//        if (switchTo != null) {
//            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//            ft.replace(R.id.fragment_container, switchTo);
//            ft.commit();
//        }
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }



    // ------------------------------------------  Listeners from fragments  --------------------------------------------------------------


    // Go to RegisterFragment when Register button pressed in LoginFragment
    @Override
    public void onRegisterMe() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        RegisterFragment fragment = new RegisterFragment();
        ft.replace(R.id.fragment_container, fragment);
        ft.addToBackStack("back_to_login");
        ft.commit();
    }

    // Go to CourseOverviewFragment when a course in CourseListFragment is selected
    @Override
    public void onCourseSelected(Course c) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        CourseOverviewFragment fragment = CourseOverviewFragment.newInstance(c);
        ft.replace(R.id.fragment_container, fragment);
        ft.addToBackStack("back_to_course_list");
        ft.commit();
    }

    // Go to selected survey from SurveyListFragment
    @Override
    public void onSurveyClicked() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        SurveyOverviewFragment fragment = new SurveyOverviewFragment();
        ft.replace(R.id.fragment_container, fragment);
        ft.addToBackStack("back_to_survey_overview");
        ft.commit();

    }

    // Go to AddSurveyFragment when Add button is clicked in SurveyListFragment
    @Override
    public void onAddSurveyClicked(String courseKey) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        AddSurveyFragment fragment = AddSurveyFragment.newInstance(courseKey);
        ft.replace(R.id.fragment_container, fragment);
        ft.addToBackStack("back_to_course_overview");
        ft.commit();
    }

    // Save the course to database when Add button is pressed in CreateCourse ( CourseListFragment > AlertDialog )
    @Override
    public void onAddCourse(Course newCourse) {
        DatabaseReference mCoursesRef = mDataRef.child("courses");
        mCoursesRef.push().setValue(newCourse);

        String teacherUid = SharedPreferencesUtils.getCurrentTeacherUid(getApplicationContext());
        DatabaseReference mTeachersRef = mDataRef.child("users/teachers/" + teacherUid).child("courses");
        mTeachersRef.child(newCourse.getCode()).setValue(true);
    }

    // Save a survey to a database
    @Override
    public void onSurveyAdded(String courseKey, ArrayList<Question> questions, String surveyName) {

        Survey survey = new Survey(courseKey, surveyName);
        DatabaseReference mSurveysRef = mDataRef.child("surveys");
        // Save the survey key in database
        String key = mSurveysRef.push().getKey();
        mSurveysRef.child(key).setValue(survey);
        Map<String, Question> map = new HashMap<>();

        // Create question keys in database and pair it with questions ( key, question)
        for (int i = 0; i < questions.size(); i++) {
            String temp = mSurveysRef.child(key).child("questions").push().getKey();
            map.put(temp, questions.get(i));
        }

        // push questions to the Firebase
        mSurveysRef.child(key).child("questions").setValue(map);
    }


    // Open list of answers when user selects the question
    @Override
    public void onQuestionClicked(String questionKey) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        AnswersListFragment fragment = AnswersListFragment.newInstance(questionKey);
        ft.replace(R.id.fragment_container, fragment);
        ft.addToBackStack("back_to_answer_list");
        ft.commit();
    }

    @Override
    public void startSession() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        LiveSessionFragment fragment = new LiveSessionFragment();
        ft.replace(R.id.fragment_container, fragment);
        ft.addToBackStack("back_to_course_overview");
        ft.commit();
    }

    @Override
    public void questionReply(String answer, String feedbackKey) {
            DatabaseReference feedAnswer = mDataRef.child("feedbacks").child(feedbackKey);
        feedAnswer.child("answer").setValue(answer);
        feedAnswer.child("isReplied").setValue(true);
    }



    @Override
    public void onSessionClicked(String sessionKey) {
        SharedPreferencesUtils.setCurrentSessionKey(getApplicationContext(), sessionKey);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        LiveSessionFragment fragment = new LiveSessionFragment();
        ft.replace(R.id.fragment_container, fragment);
        ft.addToBackStack("back_to_course_overview");
        ft.commit();
    }

    @Override
    public void questionReplyLive(String answer, String feedbackKey) {
        DatabaseReference feedAnswer = mDataRef.child("feedbacks").child(feedbackKey);
        feedAnswer.child("answer").setValue(answer);
        feedAnswer.child("isReplied").setValue(true);
    }

    @Override
    public void onDeleteCourse(Course c) {
        String courseCode = c.getCode();

        String courseKey = SharedPreferencesUtils.getCurrentCourseKey(getApplicationContext());

        DatabaseReference courseRef = mDataRef.child("courses").child(courseKey);
        courseRef.removeValue();

        Toast.makeText(this, "Deleted " + courseCode, Toast.LENGTH_SHORT).show();
    }
}
