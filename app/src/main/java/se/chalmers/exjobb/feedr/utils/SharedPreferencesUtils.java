package se.chalmers.exjobb.feedr.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by matej on 2017-01-15.
 */

public class SharedPreferencesUtils {

    public static String getCurrentCourseKey(Context context){
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS, Activity.MODE_PRIVATE);
        return prefs.getString(Constants.COURSE_KEY, "");
    }

    public static void setCurrentCourseKey(Context context, String courseKey){
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.COURSE_KEY, courseKey);
        editor.commit();
    }

    public static String getCurrentSurveyKey (Context context){
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS, Activity.MODE_PRIVATE);
        return prefs.getString(Constants.SURVEY_KEY, "");
    }

    public static void setCurrentSurveyKey(Context context, String surveyKey){
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.SURVEY_KEY, surveyKey);
        editor.commit();
    }

}
