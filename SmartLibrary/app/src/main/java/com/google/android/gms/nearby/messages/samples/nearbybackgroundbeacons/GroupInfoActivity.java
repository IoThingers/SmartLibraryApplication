package com.google.android.gms.nearby.messages.samples.nearbybackgroundbeacons;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.smartlibrary.entities.Group;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sharique on 4/16/2016.
 */
public class GroupInfoActivity extends Activity {
    String TAG = GroupActivity.class.getSimpleName();
    String groupid;
    String groupname;
    LinearLayout myLinearLayout;
    LinearLayout.LayoutParams layout_params;
    SharedPreferences user;
    View view;
    TextView gname, coursename, creatorname, roomname;
    Button joinbutton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        groupid = intent.getStringExtra("gid");
        groupname = intent.getStringExtra("gname");
        user = getSharedPreferences(getApplicationContext().getPackageName(),
                Context.MODE_PRIVATE);
        setContentView(R.layout.group_info);
        gname = (TextView) findViewById(R.id.gname);
        coursename = (TextView) findViewById(R.id.coursename);
        creatorname = (TextView) findViewById(R.id.creator);
        roomname = (TextView) findViewById(R.id.roomname);
        myLinearLayout = (LinearLayout) findViewById(R.id.linearlayout);
        joinbutton = (Button) findViewById(R.id.joingroup);
        joinbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestParams p = new RequestParams();
                p.put("user-id", user.getString("userid", "-1"));
                p.put("group-id", groupid);
                invokeWStoJoin(p);
            }
        });
        layout_params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        List<String> users = new ArrayList<>();
        RequestParams params = new RequestParams();
        params.put("group-id", groupid);
        invokeWS(params, users);
    }
    private void invokeWStoJoin(RequestParams params)
    {
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(Utils.url + "groups/add-user-to-group", params, new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(String content) {
                Log.i(TAG, "sharique succesfully joined group");


                int responsecode;
                if (content != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(content);
                        responsecode = jsonObj.getInt("responseCode");
                        Log.i(TAG, "sharique responsecode " + responsecode);
                        if(responsecode == 200)
                        {
                            SharedPreferences userDetails = getSharedPreferences(getApplicationContext().getPackageName(),
                                    Context.MODE_PRIVATE);
                            SharedPreferences.Editor edit = userDetails.edit();
                            edit.putString("joinedgroupid", groupid);
                            edit.commit();
                            Toast.makeText(getApplicationContext(), "Succesfully joined group", Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Group joun failed. Are you having one?", Toast.LENGTH_LONG).show();
                                }
                            });

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            // When the response returned by REST has Http response code other than '200'
            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {
                Log.i(TAG,"sharique joining group failed");
            }
        });
    }
    private void invokeWS(RequestParams params, final List<String> courses)
    {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(Utils.url + "groups/get-group-details", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, String content) {
                super.onSuccess(statusCode, content);
                Log.i(TAG, "sharique reponse " + content);
                /*Gson gson = new Gson();
                RestResponse<List<User>> restResponse = gson.fromJson(content, RestResponse.class);
                List<User> users = restResponse.getResponse();
                for(int i = 0; i < users.size(); i++)
                {
                    friends.add(users.get(i).getName());
                }*/
                JSONObject group = null;
                int responsecode;
                if (content != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(content);

                        group = jsonObj.getJSONObject("response");
                        responsecode = jsonObj.getInt("responseCode");
                        Log.i(TAG, "sharique responsecode " + responsecode);
                        if(responsecode == 200)
                        {
                            String clickedgroupname, clickedcoursename, clickedcreatorname, clickedroomname;
                            ArrayList<String> members = new ArrayList<String>();
                            JSONArray users = null;
                            clickedgroupname = group.getString("name");
                            gname.setText("Group Name : " + clickedgroupname);
                            clickedcoursename = group.getString("courseName");
                            coursename.setText("Course Name : " + clickedcoursename);
                            clickedcreatorname = group.getString("creatorName");
                            creatorname.setText("Creator Name : " + clickedcreatorname);
                            clickedroomname = group.getString("roomName");
                            roomname.setText("Room Name : " + clickedroomname);
                            users = group.getJSONArray("members");

                            for (int i = 0; i < users.length(); i++) {
                                //members.add(users.getJSONObject(i).get("name").toString());
                                final TextView temp = new TextView(GroupInfoActivity.this);
                                temp.setText(users.getJSONObject(i).get("name").toString());
                                temp.setLayoutParams(layout_params);
                                myLinearLayout.addView(temp, layout_params);

                                Log.i(TAG, "sharique group members " + i + " = " + users.getJSONObject(i).get("name").toString());
                            }
                        }
                        else
                        {
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
                Log.i(TAG, "sharique onFailure " + content);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
