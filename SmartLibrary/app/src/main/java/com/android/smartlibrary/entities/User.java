package com.android.smartlibrary.entities;

import java.util.ArrayList;
import com.google.gson.annotations.SerializedName;


/**
 * Created by Suryansh Singh on 4/3/2016.
 */
public class User {
    @SerializedName("name")
    private String name;
    @SerializedName("id")
    private String ID;
    @SerializedName("major")
    private String major;
    @SerializedName("course_list")
    private ArrayList courseList = new ArrayList<Course>();

    public User(String n, String uID)
    {
        name = n;
        ID = uID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public ArrayList getCourseList() {
        return courseList;
    }

    public void setCourseList(ArrayList courseList) {
        this.courseList = courseList;
    }
}
