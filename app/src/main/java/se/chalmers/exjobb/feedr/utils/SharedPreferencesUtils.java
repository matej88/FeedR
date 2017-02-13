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

    public static String getCurrentTeacherName (Context context){
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS, Activity.MODE_PRIVATE);
        return prefs.getString(Constants.TEACHER_NAME, "");
    }


    public static void setCurrentTeacherName(Context context, String teacherName){
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.TEACHER_NAME, teacherName);
        editor.commit();
    }

    public static String getCurrentTeacherUid (Context context){
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS, Activity.MODE_PRIVATE);
        return prefs.getString(Constants.TEACHER_UID, "");
    }

    public static void setCurrentTeacherUid(Context context, String teacherUid){
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.TEACHER_UID, teacherUid);
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

    public static String getCurrentSurveyName (Context context){
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS, Activity.MODE_PRIVATE);
        return prefs.getString(Constants.SURVEY_NAME, "");
    }

    public static void setCurrentSurveyName(Context context, String surveyKey){
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.SURVEY_NAME, surveyKey);
        editor.commit();
    }


    public static String getCurrentSession (Context context){
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS, Activity.MODE_PRIVATE);
        return prefs.getString(Constants.SESSION_KEY, "");
    }

    public static void setCurrentSessionKey(Context context, String sessionKey){
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.SESSION_KEY, sessionKey);
        editor.commit();
    }

}
