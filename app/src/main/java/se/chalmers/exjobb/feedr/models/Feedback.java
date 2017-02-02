package se.chalmers.exjobb.feedr.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Map;

/**
 * Created by matej on 2017-01-04.
 */

public class Feedback  {

    private String username;
    private long timestamp;
    private String feedback;
    private int rating;

    public Feedback(){

    }
    public Feedback(String username,String feedback, int rating, long timestamp) {
        this.username = username;
        this.feedback = feedback;
        this.rating = rating;
        this.timestamp = timestamp;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
