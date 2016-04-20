package com.android.smartlibrary.entities;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;


/**
 * Created by Suryansh Singh on 4/3/2016.
 */
public class User
{
    private int ufid;
    private String name;
    private String major;
    private boolean active;
    private List<Course> courses;
    private String email;

    public User(int _ufid, String _name, String _major, String _emailid)
    {
        ufid = _ufid;
        name = _name;
        major = _major;
        email = _emailid;
    }
    /**
     * @return the ufid
     */
    public int getUfid()
    {
        return ufid;
    }

    /**
     * @param ufid
     *            the ufid to set
     */
    public void setUfid(int ufid)
    {
        this.ufid = ufid;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @return the major
     */
    public String getMajor()
    {
        return major;
    }

    /**
     * @param major
     *            the major to set
     */
    public void setMajor(String major)
    {
        this.major = major;
    }

    /**
     * @return the active
     */
    public boolean isActive()
    {
        return active;
    }

    /**
     * @param active
     *            the active to set
     */
    public void setActive(boolean active)
    {
        this.active = active;
    }

    /**
     * @return the courses
     */
    public List<Course> getCourses()
    {
        return courses;
    }

    /**
     * @param courses
     *            the courses to set
     */
    public void setCourses(List<Course> courses)
    {
        this.courses = courses;
    }

    /**
     * @return the email
     */
    public String getEmail()
    {
        return email;
    }

    /**
     * @param email
     *            the email to set
     */
    public void setEmail(String email)
    {
        this.email = email;
    }

}