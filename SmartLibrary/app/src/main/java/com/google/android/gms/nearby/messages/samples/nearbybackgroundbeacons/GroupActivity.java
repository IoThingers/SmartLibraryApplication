package com.google.android.gms.nearby.messages.samples.nearbybackgroundbeacons;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sharique on 4/4/2016.
 */
public class GroupActivity extends ListActivity {
    TextView content;
    SharedPreferences user;
    String TAG = GroupActivity.class.getSimpleName();
    ArrayAdapter<String> adapter;
    String courseid;
    String coursename;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        courseid = intent.getStringExtra("cid");
        coursename = intent.getStringExtra("cname");
        setContentView(R.layout.groups);

        user = getSharedPreferences(getApplicationContext().getPackageName(),
                Context.MODE_PRIVATE);
        content = (TextView)findViewById(R.id.output);
        Log.i(TAG, "sharique oncreate called courseid =" + courseid);
        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third - the Array of data

        List<String> groups = new ArrayList<>();
        RequestParams params = new RequestParams();
        params.put("course-id", courseid);
        invokeWS(params, groups);
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, groups);

        // Assign adapter to List
        setListAdapter(adapter);
    }

    public void invokeWS(RequestParams params, final List<String> groups)
    {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(Utils.url + "groups/show-groups-for-course", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, String content) {
                super.onSuccess(statusCode, content);
                Log.i(TAG, "sharique groups " + content);
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
                        JSONObject jsonObj = new JSONObject(content);

                        // Getting JSON Array node
                        users = jsonObj.getJSONArray("response");
                        responsecode = jsonObj.getInt("responseCode");
                        if(responsecode == 200)
                        {
                            for (int i = 0; i < users.length(); i++) {
                                groups.add(users.getJSONObject(i).get("name").toString() + "-" + users.getJSONObject(i).get("id").toString());
                                Log.i(TAG, "sharique groups " + i + " = " + users.getJSONObject(i).get("name").toString());
                            }
                            adapter.notifyDataSetChanged();
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
    protected void onListItemClick(ListView l, View v, int position, long id) {

        super.onListItemClick(l, v, position, id);

        // ListView Clicked item index
        int itemPosition     = position;

        // ListView Clicked item value
        String[]  itemValue    = ((String) l.getItemAtPosition(position)).split("-");

        Intent intent = new Intent(GroupActivity.this, GroupInfoActivity.class);
        intent.putExtra("gname", itemValue[0]);
        intent.putExtra("gid", itemValue[1]);
        startActivity(intent);

        //content.setText("Click : \n  Position :"+itemPosition+"  \n  ListItem : " +itemValue);

    }

}
