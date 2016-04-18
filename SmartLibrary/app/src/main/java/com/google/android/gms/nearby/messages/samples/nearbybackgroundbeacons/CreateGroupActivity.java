package com.google.android.gms.nearby.messages.samples.nearbybackgroundbeacons;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.smartlibrary.entities.Group;
import com.android.smartlibrary.entities.User;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateGroupActivity extends AppCompatActivity {
    String TAG = CreateGroupActivity.class.getSimpleName();
    ArrayAdapter<String> adapter;
    SharedPreferences user;
    String item;
    List<String> courses = new ArrayList<>();
    ListView courseList;
    int positionSelected;
    JSONObject jsonObj;
    JSONArray users = null;
    Button register;
    Map courseNameIDMap = new HashMap<String, String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        user = getSharedPreferences(getApplicationContext().getPackageName(), Context.MODE_PRIVATE);
        register = (Button) findViewById(R.id.registerbutton);
        courseList = (ListView) findViewById(R.id.listView);
        invokeWS(courses);

        //Toast.makeText(getApplicationContext(),"the user id is "+userID,Toast.LENGTH_LONG).show();
        String groupName = findViewById(R.id.textView2).toString();

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, courses);

        // Assign adapter to List

        courseList.setAdapter(adapter);


        courseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                item = ((TextView) view).getText().toString();
                positionSelected = position;
                //Toast.makeText(getBaseContext(), String.valueOf(positionSelected), Toast.LENGTH_LONG).show();

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        register = (Button) findViewById(R.id.registerbutton);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item == null)
                    Toast.makeText(getBaseContext(), "Please Select a Course", Toast.LENGTH_LONG).show();
                else {
                    String courseName, roomName, groupName;
                    int roomID, courseID, userID;
                    EditText groupNameET = (EditText)findViewById(R.id.fname);
                    groupName = groupNameET.getText().toString();
                    courseName = item.trim();
                    courseID = Integer.parseInt(courseName.split("-")[1]);
                    courseName = courseName.split("-")[0];
                    /*for (int i=0; i< courses.size();i++){
                        if(courses.get(i).equals(courseName)){
                            courseID = String.valueOf(i+1);
                            break;
                        }
                    }*/
                    //courseID = courseIDName.get(item);
                    //Log.i(TAG,courseNameIDMap.toString());
                    /*if(courseNameIDMap.containsKey("ANALYSIS OF ALGORITHMS=3"))
                        Toast.makeText(getBaseContext(), courseName + " " + courseNameIDMap.get("ANALYSIS OF ALGORITHMS=3"), Toast.LENGTH_LONG).show();
                    //courseID = courseIDName.get(courseName).toString();*/
                    Bundle bund = getIntent().getExtras();
                    roomID = Integer.parseInt(bund.getString("roomid"));
                    roomName = null;
                    userID = Integer.parseInt(user.getString("userid", "-1"));

                    //public Group(String name, int creatorID, String creatorName, int roomID, String roomName,
                    // int courseID, String courseName){
                    Group group = new Group(groupName, userID, null, roomID, null, courseID, courseName);
                    invokeWSCreateGroup(group);
                    startActivity(new Intent(CreateGroupActivity.this, RegisterActivity.class));
                }


            }
        });
    }

    public void invokeWS(final List<String> courses)
    {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(Utils.url + "courses/show-all-courses", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, String content) {
                super.onSuccess(statusCode, content);
                Log.i(TAG, "courses API reponse " + content);
                /*Gson gson = new Gson();
                RestResponse<List<User>> restResponse = gson.fromJson(content, RestResponse.class);
                List<User> users = restResponse.getResponse();
                for(int i = 0; i < users.size(); i++)
                {
                    friends.add(users.get(i).getName());
                }*/
                JSONArray users = null;
                int responsecode;
                if (content != null) {
                    try {
                        jsonObj = new JSONObject(content);

                        users = jsonObj.getJSONArray("response");
                        responsecode = jsonObj.getInt("responseCode");
                        Log.i(TAG, "courses responsecode " + responsecode + ", arraySize = " + users.length());
                        if (responsecode == 200) {
                            for (int i = 0; i < users.length(); i++) {
                                courseNameIDMap.put(users.getJSONObject(i).get("name").toString().trim(), users.getJSONObject(i).get("id").toString().trim());
                                courses.add(users.getJSONObject(i).get("name").toString() + "-" + users.getJSONObject(i).get("id").toString());
                                Log.i(TAG, "sharique courses " + i + " = " + users.getJSONObject(i).get("name").toString());
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Groups WebApi response code failure", Toast.LENGTH_LONG).show();
                                }
                            });

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                super.onFailure(statusCode, error, content);
                //Log.i(TAG, "sharique onFailure " + content);
            }
        });
    }

    public void invokeWSCreateGroup(Group params)
    {
        AsyncHttpClient client = new AsyncHttpClient();
        HttpEntity body = null;
        try{
            body = new StringEntity(new Gson().toJson(params));
            client.post(getApplicationContext(), Utils.url + "groups/create-group", body, "application/json", new AsyncHttpResponseHandler(){

                int responsecode,groupID;
                @Override
                public void onSuccess(String response) {


                    try {
                        JSONObject jsonObj = new JSONObject(response);
                        responsecode = jsonObj.getInt("responseCode");

                        if(responsecode == 200){
                            groupID = jsonObj.getInt("response");
                            SharedPreferences userDetails = getSharedPreferences(getApplicationContext().getPackageName(),
                                    Context.MODE_PRIVATE);
                            SharedPreferences.Editor edit = userDetails.edit();
                            edit.putString("createdgroupid", String.valueOf(groupID));
                            edit.putString("joinedgroupid",String.valueOf(groupID));
                            edit.commit();
                            Toast.makeText(getApplicationContext(), "Group created succesfully with GroupID: " + String.valueOf(groupID), Toast.LENGTH_LONG).show();
                        }

                        else
                            Toast.makeText(getApplicationContext(), "Group creation failed ", Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                // When the response returned by REST has Http response code other than '200'
                @Override
                public void onFailure(int statusCode, Throwable error,
                                      String content) {
                    if(statusCode == 404){
                        Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                    }
                    // When Http response code is '500'
                    else if(statusCode == 500){
                        Toast.makeText(getApplicationContext(), "Create Group Something went wrong at server end", Toast.LENGTH_LONG).show();
                    }
                    // When Http response code other than 404, 500
                    else{
                        Toast.makeText(getApplicationContext(), "Create Group Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        catch (UnsupportedEncodingException ex)
        {
            Toast.makeText(getApplicationContext(), "UnsupportedEncodingException - Could not Group User", Toast.LENGTH_LONG).show();
        }
    }



}