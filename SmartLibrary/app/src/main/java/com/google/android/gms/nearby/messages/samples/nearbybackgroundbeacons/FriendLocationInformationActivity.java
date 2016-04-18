package com.google.android.gms.nearby.messages.samples.nearbybackgroundbeacons;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FriendLocationInformationActivity extends AppCompatActivity {
    TextView fname,gname,coursename,creatorname, roomname;
    Button flocation, gjoin;
    LinearLayout.LayoutParams layout_params;
    String clickedgroupname, clickedcoursename, clickedcreatorname, clickedroomname,groupid,roomid;
    String TAG = FriendLocationInformationActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_location_information);
        final Intent intent = getIntent();
        gname = (TextView) findViewById(R.id.gname);
        coursename = (TextView) findViewById(R.id.coursename);
        creatorname = (TextView) findViewById(R.id.creator);
        roomname = (TextView) findViewById(R.id.roomname);
        flocation = (Button) findViewById(R.id.flocation);
        gjoin = (Button) findViewById(R.id.gjoin);
        flocation.setText("Locate ("+ intent.getStringExtra("name")+") On Map");
        gjoin.setText("Join ("+ intent.getStringExtra("name")+ "'s) Group");
        String groupString = intent.getStringExtra("group");

        layout_params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        fname = (TextView) findViewById(R.id.fname);
        fname.setText("Friend Name : " + intent.getStringExtra("name"));
        try {
            JSONObject group = new JSONObject(groupString);
            roomid = group.getString("roomId");
            groupid = group.getString("id");
            ArrayList<String> members = new ArrayList<String>();
            JSONArray users = null;
            clickedroomname = group.getString("roomName");
            roomname.setText("Friend's Room Location : " + clickedroomname);
            clickedgroupname = group.getString("name");
            gname.setText("Friend's Group Name : " + clickedgroupname);
            clickedcoursename = group.getString("courseName");
            coursename.setText("Group's Course Name : " + clickedcoursename);
            clickedcreatorname = group.getString("creatorName");
            creatorname.setText("Group's Creator Name : " + clickedcreatorname);

            users = group.getJSONArray("members");
            String tempString = "Current members:";
            final TextView temp = (TextView) findViewById(R.id.members);
            for (int i = 0; i < users.length(); i++) {
                //members.add(users.getJSONObject(i).get("name").toString());

                tempString += "\n"+users.getJSONObject(i).get("name").toString();


                Log.i(TAG, "sharique group members " + i + " = " + users.getJSONObject(i).get("name").toString());
            }
            temp.setText(tempString);



        } catch (JSONException e) {
            e.printStackTrace();
        }
        gjoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestParams p = new RequestParams();
                SharedPreferences user = getSharedPreferences(getApplicationContext().getPackageName(), Context.MODE_PRIVATE);
                p.put("user-id", intent.getStringExtra("id"));
                p.put("group-id", groupid);
                invokeWStoJoin(p);
            }
        });
        flocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FriendLocationInformationActivity.this,MapLocationActivity.class);
                i.putExtra("roomid",roomid);
                startActivity(i);
            }
        });


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
                                    Toast.makeText(getApplicationContext(), "Group join failed. Are you already in a group?", Toast.LENGTH_LONG).show();
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
}
