package se.chalmers.exjobb.feedr.models;

/**
 * Created by matej on 2017-01-04.
 */

public class Feedback {

    private String username;
    private String courseKey;
    private String feedback;
    private double rating;

    public Feedback(){

    }
    public Feedback(String username, String courseKey, String feedback, double rating) {
        this.username = username;
        this.courseKey = courseKey;
        this.feedback = feedback;
        this.rating = rating;
    }

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

    public double getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
