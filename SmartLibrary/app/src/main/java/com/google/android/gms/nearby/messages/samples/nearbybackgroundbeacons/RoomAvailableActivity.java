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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoomAvailableActivity extends AppCompatActivity {
    String TAG = RoomAvailableActivity.class.getSimpleName();
    ListView roomList;
    ArrayAdapter<String> adapter;
    List<String> rooms = new ArrayList<>();
    Button register;
    String item;
    SharedPreferences user;
    int positionSelected;
    Map<String, String> roomsAvailable = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_available);
        Bundle bnd = getIntent().getExtras();
        user = getSharedPreferences(getApplicationContext().getPackageName(), Context.MODE_PRIVATE);
        roomList = (ListView) findViewById(R.id.listView);
        String [] beaconIDs = bnd.getString("beaconIDs").split(" ");
        for(int i=0; i<beaconIDs.length; i++){
            invokeWS(beaconIDs[i]);
        }
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, rooms);
        roomList.setAdapter(adapter);

        register = (Button) findViewById(R.id.registerbutton);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(item==null)
                    Toast.makeText(getApplicationContext(),"Please Select A Room",Toast.LENGTH_LONG).show();
                else if(roomsAvailable.get(item).toString().equals("false")){
                    Toast.makeText(getApplicationContext(),"Sorry The Room Is Occupied",Toast.LENGTH_LONG).show();
                }
                else if(!user.getString("joinedgroupid","-1").equals("-1"))
                    Toast.makeText(getApplicationContext(),"Sorry you are already enrolled in a Group!",Toast.LENGTH_LONG).show();
                else{

                    invokeWSRoomID(item.split("-")[1]);
                }
            }
        });

        roomList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                item = ((TextView) view).getText().toString();
                positionSelected = position;
                //Toast.makeText(getBaseContext(), String.valueOf(positionSelected), Toast.LENGTH_LONG).show();

            }
        });
    }

    public void invokeWS(String roomID)
    {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(Utils.url + "rooms/get-room-details?room-id=" + roomID, new AsyncHttpResponseHandler() {
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
                JSONObject users = null;
                int responsecode;
                if (content != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(content);

                        users = jsonObj.getJSONObject("response");
                        responsecode = jsonObj.getInt("responseCode");

                        if (responsecode == 200) {
                            roomsAvailable.put((users.get("name").toString() + "-" + users.get("id").toString()), users.get("available").toString());
                            rooms.add(users.get("name").toString() + "-" + users.get("id").toString());
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
                Toast.makeText(RoomAvailableActivity.this, "Sorry server not available! Please try again.", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "Suryansh Get room name failed " + content);
            }
        });
    }

    public void invokeWSRoomID(String params){
        final String roomID = params;
        // Make RESTful webservice call using AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://anyonethere.cloudapp.net:8080/rooms/is-room-available?room-id="+roomID, new AsyncHttpResponseHandler() {

            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {

                try {
                    // JSON Object

                    JSONObject obj = new JSONObject(response);


                    /*JSONArray responseArray = null;
                    responseArray = obj.getJSONArray("response");*/
                    if (obj.get("response").toString().equals("false"))
                        Toast.makeText(getApplicationContext(), "Sorry Room: "+roomID+"not avaiable!", Toast.LENGTH_LONG).show();
                    else if(obj.get("response").toString().equals("true")){
                        Intent i = new Intent(RoomAvailableActivity.this, CreateGroupActivity.class);
                        i.putExtra("roomid",roomID);
                        startActivity(i);
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();

                }
            }

            // When the response returned by REST has Http response code other than '200'
            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {
                // Hide Progress Dialog
                /*progressDialog.hide();*/
                // When Http response code is '404'
                if (statusCode == 404) {
                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
