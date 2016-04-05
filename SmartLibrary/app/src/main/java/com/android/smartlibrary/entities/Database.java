package com.android.smartlibrary.entities;


import java.util.HashMap;
import java.util.Map;

/**
 * Created by Suryansh Singh on 4/3/2016.
 */
public class Database {
   /* ArrayList groupList = new ArrayList<Group>();
    ArrayList userList = new ArrayList<User>();
    ArrayList roomList = new ArrayList<Room>();
    ArrayList courseList = new ArrayList<Course>();*/
    Map groupList = new HashMap<String,Group>();
    Map userList = new HashMap<String,User>();
    Map roomList  = new HashMap<String,Room>();
    Map courseList = new HashMap<String,Course>();
}
