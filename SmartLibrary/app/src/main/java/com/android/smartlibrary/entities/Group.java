package com.android.smartlibrary.entities;

import java.util.ArrayList;

/**
 * Created by Suryansh Singh on 4/3/2016.
 */
public class Group {
    private String name;
    private String userID;
    private String courseID;
    private String userCreator;
    private ArrayList userList = new ArrayList<User>();
    public Group(String name, String courseID, String userCreator){
        this.name = name;
        this.courseID = courseID;
        this.userCreator = userCreator;
    }
}
