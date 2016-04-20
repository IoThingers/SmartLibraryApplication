package com.google.android.gms.nearby.messages.samples.nearbybackgroundbeacons;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.smartlibrary.entities.User;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.http.impl.client.*;

import java.io.UnsupportedEncodingException;

/**
 * Created by sharique on 4/10/2016.
 */
public class HomeActivity extends Activity {

    private Button register;
    private EditText ufid, major;
    SharedPreferences user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = getSharedPreferences(getApplicationContext().getPackageName(),
                Context.MODE_PRIVATE);//getPreferences(MODE_PRIVATE);
        if(user.contains("userid"))
        {
            //Launch Activity
            startActivity(new Intent(HomeActivity.this, RegisterActivity.class));
        }

        setContentView(R.layout.register);
        ufid = (EditText)findViewById(R.id.ufid);
        major = (EditText)findViewById(R.id.major);
        register = (Button)findViewById(R.id.registerbutton);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
				
                invokeWS();
            }
        });
    }

    public void invokeWS()
    {
        AsyncHttpClient client = new AsyncHttpClient();
        HttpEntity body = null;
        User params  = new User(Integer.parseInt(ufid.getText().toString()), user.getString("username", ""), major.getText().toString(), user.getString("emailid", ""));
        try{
            body = new StringEntity(new Gson().toJson(params));
            client.post(getApplicationContext(), Utils.url + "users/create-user", body, "application/json", new AsyncHttpResponseHandler(){
                //client.get(Utils.url + "groups/add-user-to-group", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String response) {
                    try{
                        JSONObject jsonObject = new JSONObject(response);
                        int responsecode = jsonObject.getInt("responseCode");
                        if(responsecode == 200)
                        {
                            boolean responsemessage = jsonObject.getBoolean("response");
                            if(responsemessage)
                            {
                                SharedPreferences userDetails = getSharedPreferences(getApplicationContext().getPackageName(),
                                        Context.MODE_PRIVATE);
                                SharedPreferences.Editor edit = userDetails.edit();
                                edit.clear();
                                edit.putString("userid", ufid.getText().toString());
                                edit.putString("major", major.getText().toString());
                                edit.commit();
                                Toast.makeText(getApplicationContext(), "User created succesfully ", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(HomeActivity.this, RegisterActivity.class));
                            }

                            else
                                Toast.makeText(getApplicationContext(), "User creation failed ", Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "User creation response code = " + responsecode, Toast.LENGTH_LONG).show();
                        }
                    }
                    catch (JSONException ex)
                    {
                        ex.printStackTrace();
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
                        Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                    }
                    // When Http response code other than 404, 500
                    else{
                        Toast.makeText(getApplicationContext(), "Create User API Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        catch (UnsupportedEncodingException ex)
        {
            Toast.makeText(getApplicationContext(), "UnsupportedEncodingException - Could not register", Toast.LENGTH_LONG).show();
        }
    }
}
