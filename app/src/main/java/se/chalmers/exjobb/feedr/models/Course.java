package se.chalmers.exjobb.feedr.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by matej on 2017-01-03.
 */

public class Course implements Parcelable {

    private String name;
    private String code;
    private String teacher;

    @JsonIgnore
    private String key;


    public Course() {
    }

    public Course(String name, String code, String teacher){
            this.name = name;
            this.code = code;
            this.teacher = teacher;
    }


    protected Course(Parcel in) {
        name = in.readString();
        code = in.readString();
        teacher = in.readString();
    }

    public static final Creator<Course> CREATOR = new Creator<Course>() {
        @Override
        public Course createFromParcel(Parcel in) {
            return new Course(in);
        }

        @Override
        public Course[] newArray(int size) {
            return new Course[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(code);
        parcel.writeString(teacher);
    }
}
