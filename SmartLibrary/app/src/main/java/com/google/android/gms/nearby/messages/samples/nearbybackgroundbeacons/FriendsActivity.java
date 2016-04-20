package com.google.android.gms.nearby.messages.samples.nearbybackgroundbeacons;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Contacts;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import com.android.smartlibrary.entities.RestResponse;
import com.android.smartlibrary.entities.User;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sharique on 4/4/2016.
 */
public class FriendsActivity extends ListActivity {
    TextView content;
    SharedPreferences user;
    String TAG = FriendsActivity.class.getSimpleName();
    ArrayAdapter<String> adapter;
    List<String> friends = new ArrayList<>();
    Map<String,String> friendNameID = new HashMap<>();
    String  itemValue;
    int positionSelected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends);

        user = getSharedPreferences(getApplicationContext().getPackageName(),
                Context.MODE_PRIVATE);
        content = (TextView)findViewById(R.id.output);
        Log.i(TAG, "sharique oncreate called");
        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third - the Array of data


        RequestParams params = new RequestParams();
        params.put("user-id", user.getString("userid", "-1"));
        //params.put("user-id", "65132049");
        //Test Suryansh
        Log.i(TAG, "Current UserID:" + user.getString(("userid"), "-1"));
        invokeWS(params, friends);
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, friends);

        // Assign adapter to List
        setListAdapter(adapter);

    }


    public void invokeWS(RequestParams params, final List<String> friends)
    {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(Utils.url + "users/get-friends-of-user", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, String content) {
                super.onSuccess(statusCode, content);
                Log.i(TAG, "sharique friends " + content);
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

                        responsecode = responsecode = jsonObj.getInt("responseCode");
                        ;
                        if (responsecode == 200) {

                            for (int i = 0; i < users.length(); i++) {
                                friendNameID.put(users.getJSONObject(i).get("name").toString(), users.getJSONObject(i).get("ufid").toString());
                                friends.add(users.getJSONObject(i).get("name").toString());
                                Log.i(TAG, "sharique friend " + i + " = " + users.getJSONObject(i).get("name").toString());
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Friends WebApi response code failure", Toast.LENGTH_LONG).show();
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

    public void invokeWSFindFriendLocation(RequestParams params)
    {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(Utils.url + "groups/get-group-details-by-user", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, String content) {
                super.onSuccess(statusCode, content);
                Log.i(TAG, "Suryansh Friend Info Location  " + content);
                /*Gson gson = new Gson();
                RestResponse<List<User>> restResponse = gson.fromJson(content, RestResponse.class);
                List<User> users = restResponse.getResponse();
                for(int i = 0; i < users.size(); i++)
                {
                    friends.add(users.get(i).getName());
                }*/
                JSONObject users = null;
                int responsecode;
                if (content != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(content);
                        users = jsonObj.getJSONObject("response");

                        responsecode = responsecode = jsonObj.getInt("responseCode");
                        ;
                        if (responsecode == 200) {

                            if (users == null) {
                                Toast.makeText(getApplicationContext(), "Friend is not near any beacon or Enrolled in a group", Toast.LENGTH_LONG).show();
                            } else {
                                Intent i = new Intent(FriendsActivity.this, FriendLocationInformationActivity.class);
                                i.putExtra("group",users.toString());
                                i.putExtra("name",itemValue);
                                i.putExtra("id",friendNameID.get(itemValue));
                                startActivity(i);
                                adapter.notifyDataSetChanged();
                            }


                        } else {
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Friends WebApi response code failure", Toast.LENGTH_LONG).show();
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
                Log.i(TAG, "suryansh onFailure " + content);
                Toast.makeText(getApplicationContext(), "Friend is not near any beacon or Enrolled in a group", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        super.onListItemClick(l, v, position, id);

        // ListView Clicked item index
        int itemPosition     = position;

        // ListView Clicked item value
        itemValue    = (String) l.getItemAtPosition(position);



        RequestParams params = new RequestParams();

                //params.put("user-id", user.getString("userid", "-1"));
                //Test Suryansh

        params.put("user-id", friendNameID.get(itemValue));
        //hard codded suryansh remove
        //params.put("user-id","258369147");
        Log.i(TAG, "List Selected User:" + params.toString());
        invokeWSFindFriendLocation(params);
                //Toast.makeText(getBaseContext(), String.valueOf(positionSelected), Toast.LENGTH_LONG).show();

    }
}
