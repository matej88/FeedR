package se.chalmers.exjobb.feedr.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by matej on 2017-01-06.
 */

public class Answer implements Parcelable {

    @JsonIgnore
    private String key;

    private String username;
    private String answer;
    private String questionKey;


    public Answer() {
    }

    public Answer(String username, String answer) {
        this.username = username;
        this.answer = answer;
    }

    protected Answer(Parcel in) {
        key = in.readString();
        username = in.readString();
        answer = in.readString();
        questionKey = in.readString();
    }

    public static final Creator<Answer> CREATOR = new Creator<Answer>() {
        @Override
        public Answer createFromParcel(Parcel in) {
            return new Answer(in);
        }

        @Override
        public Answer[] newArray(int size) {
            return new Answer[size];
        }
    };

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getQuestionKey() {
        return questionKey;
    }

    public void setQuestionKey(String questionKey) {
        this.questionKey = questionKey;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(key);
        parcel.writeString(username);
        parcel.writeString(answer);
        parcel.writeString(questionKey);
    }
}
