package se.chalmers.exjobb.feedr.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by matej on 2017-01-04.
 */

public class Feedback implements Parcelable {

    private String username;
    private String courseKey;
    private String feedback;
    private int rating;

    public Feedback(){

    }
    public Feedback(String username, String courseKey, String feedback, int rating) {
        this.username = username;
        this.courseKey = courseKey;
        this.feedback = feedback;
        this.rating = rating;
    }

    protected Feedback(Parcel in) {
        username = in.readString();
        courseKey = in.readString();
        feedback = in.readString();
        rating = in.readInt();
    }

    public static final Creator<Feedback> CREATOR = new Creator<Feedback>() {
        @Override
        public Feedback createFromParcel(Parcel in) {
            return new Feedback(in);
        }

        @Override
        public Feedback[] newArray(int size) {
            return new Feedback[size];
        }
    };

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCourseKey() {
        return courseKey;
    }

    public void setCourseKey(String courseKey) {
        this.courseKey = courseKey;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(username);
        parcel.writeString(courseKey);
        parcel.writeString(feedback);
        parcel.writeInt(rating);
    }
}
