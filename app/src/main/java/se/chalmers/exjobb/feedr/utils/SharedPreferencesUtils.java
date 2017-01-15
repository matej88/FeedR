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

}
