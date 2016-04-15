package com.google.android.gms.nearby.messages.samples.nearbybackgroundbeacons;

import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Contacts;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.android.smartlibrary.entities.RestResponse;
import com.android.smartlibrary.entities.User;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sharique on 4/4/2016.
 */
public class FriendsActivity extends ListActivity {
    TextView content;
    SharedPreferences user;
    String TAG = FriendsActivity.class.getSimpleName();

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

        List<String> friends = new ArrayList<>();
        RequestParams params = new RequestParams();
        params.put("user-id", "65132049");//user.getString("userid", ""));
        invokeWS(params, friends);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, friends);

        // Assign adapter to List
        setListAdapter(adapter);
    }

    public void invokeWS(RequestParams params, final List<String> friends)
    {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(Utils.url + "users/get-friends-of-user", params, new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, String content) {
                super.onSuccess(statusCode, content);
                Log.i(TAG, "sharique friends " + content);
                Gson gson = new Gson();
                RestResponse<List<User>> restResponse = gson.fromJson(content, RestResponse.class);
                List<User> users = restResponse.getResponse();
                for(int i = 0; i < users.size(); i++)
                {
                    friends.add(users.get(i).getName());
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                super.onFailure(statusCode, error, content);
                Log.i(TAG, "sharique onFailure " + content);
            }
        });
    }


    /*@Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        super.onListItemClick(l, v, position, id);

        // ListView Clicked item index
        int itemPosition     = position;

        // ListView Clicked item value
        String  itemValue    = (String) l.getItemAtPosition(position);

        content.setText("Click : \n  Position :"+itemPosition+"  \n  ListItem : " +itemValue);

    }*/
}
