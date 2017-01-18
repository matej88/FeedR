package se.chalmers.exjobb.feedr;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import se.chalmers.exjobb.feedr.fragments.AddSurveyFragment;
import se.chalmers.exjobb.feedr.fragments.AnswerListFragment;
import se.chalmers.exjobb.feedr.fragments.CourseListFragment;
import se.chalmers.exjobb.feedr.fragments.CourseOverviewFragment;
import se.chalmers.exjobb.feedr.fragments.SurveyListTabFragment;
import se.chalmers.exjobb.feedr.fragments.SurveyOverviewFragment;
import se.chalmers.exjobb.feedr.models.Answer;
import se.chalmers.exjobb.feedr.models.Course;
import se.chalmers.exjobb.feedr.models.Feedback;
import se.chalmers.exjobb.feedr.models.Question;
import se.chalmers.exjobb.feedr.models.Survey;
import se.chalmers.exjobb.feedr.utils.SharedPreferencesUtils;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        CourseListFragment.OnCourseSelectedListener,
        SurveyListTabFragment.OnSurveyClickListener,
        AddSurveyFragment.OnSurveyAddListener,
        SurveyOverviewFragment.OnSurveyQuestionClickedListener
        {
    private DatabaseReference mDataRef;
    private double[] dRatings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Add Course List Fragment whenever the Activity is created, make that fragment the main page

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragment_container, new CourseListFragment());
        ft.commit();

        mDataRef = FirebaseDatabase.getInstance().getReference();
        mDataRef.keepSynced(true);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        Fragment switchTo = null;

        // Handle navigation view item clicks here.
        switch(item.getItemId()){
            case R.id.nav_courses:
                switchTo = new CourseListFragment();
                break;
            case R.id.nav_surveys:
                break;
            case R.id.nav_logout:
                break;

        }

        if(switchTo != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_container, switchTo);
            ft.commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onCourseSelected(Course selectedCourse) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        CourseOverviewFragment fragment = CourseOverviewFragment.newInstance(selectedCourse);
        ft.replace(R.id.fragment_container, fragment);
        ft.addToBackStack("course_list");
        ft.commit();
    }

    @Override
    public void onSurveyClicked() {


        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        SurveyOverviewFragment fragment = new SurveyOverviewFragment();
        ft.replace(R.id.fragment_container, fragment);
        ft.addToBackStack("survey_overview");
        ft.commit();

    }

    @Override
    public void onAddSurveyClicked(String courseKey) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        AddSurveyFragment fragment = AddSurveyFragment.newInstance(courseKey);
        ft.replace(R.id.fragment_container, fragment);
        ft.addToBackStack("course_overview");
        ft.commit();
    }

    @Override
    public void onAddCourse(Course newCourse) {
            DatabaseReference mCoursesRef = mDataRef.child("courses");
        mCoursesRef.push().setValue(newCourse);
    }

    @Override
    public void onSurveyAdded(String courseKey, ArrayList<Question> questions) {
            Survey survey = new Survey(courseKey, "New Survey");

            DatabaseReference mSurveysRef = mDataRef.child("surveys");
            String key = mSurveysRef.push().getKey();
            mSurveysRef.child(key).setValue(survey);

             Map<String,Question> map = new HashMap<String, Question>();

            for ( int i = 0; i < questions.size(); i++){
                String temp = mSurveysRef.child(key).child("questions").push().getKey();
                map.put(temp,questions.get(i));
            }

             mSurveysRef.child(key).child("questions").setValue(map);



    }

            @Override
            public void onQuestionClicked(String questionKey) {
//
//                DatabaseReference mAnswersRef = mDataRef.child("surveys").child(courseKey).child("answers");
//                Query answersRef = mAnswersRef.orderByChild(Survey.COURSE_KEY).equalTo(courseKey);
//                surveysForCourseRef.addChildEventListener(new SurveysChildEventListener());

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                AnswerListFragment fragment = AnswerListFragment.newInstance(questionKey);
                ft.replace(R.id.fragment_container, fragment);
                ft.addToBackStack("answer_list");
                ft.commit();

            }
        }
