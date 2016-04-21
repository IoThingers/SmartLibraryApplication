package com.google.android.gms.nearby.messages.samples.nearbybackgroundbeacons;

import android.app.Activity;
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
 * Created by sharique on 4/16/2016.
 */
public class CoursesActivity extends ListActivity {
    TextView content;
    SharedPreferences user;
    String TAG = CoursesActivity.class.getSimpleName();
    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.courses);

        user = getSharedPreferences(getApplicationContext().getPackageName(),
                Context.MODE_PRIVATE);
        //content = (TextView)findViewById(R.id.output);
        Log.i(TAG, "sharique oncreate called");
        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third - the Array of data

        List<String> courses = new ArrayList<>();
        RequestParams params = new RequestParams();
        params.put("user-id", user.getString("userid", "-1"));
        invokeWS(params, courses);
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, courses);

        // Assign adapter to List
        setListAdapter(adapter);
    }

    public void invokeWS(RequestParams params, final List<String> courses)
    {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(Utils.url + "courses/show-all-courses", params, new AsyncHttpResponseHandler() {
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
                JSONArray users = null;
                int responsecode;
                if (content != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(content);

                        users = jsonObj.getJSONArray("response");
                        responsecode = jsonObj.getInt("responseCode");
                        Log.i(TAG, "sharique responsecode " + responsecode + ", arraySize = " + users.length());
                        if(responsecode == 200)
                        {
                            for (int i = 0; i < users.length(); i++) {
                                courses.add(users.getJSONObject(i).get("name").toString() + "-" + users.getJSONObject(i).get("id").toString());
                                Log.i(TAG, "sharique courses " + i + " = " + users.getJSONObject(i).get("name").toString());
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

        Intent intent = new Intent(CoursesActivity.this, GroupActivity.class);
        intent.putExtra("cname", itemValue[0]);
        intent.putExtra("cid", itemValue[1]);
        startActivity(intent);

        //content.setText("Click : \n  Position :"+itemPosition+"  \n  ListItem : " +itemValue);

    }
}
