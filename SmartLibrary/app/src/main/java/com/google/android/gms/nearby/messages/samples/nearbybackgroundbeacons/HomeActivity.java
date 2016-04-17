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
    private EditText username, password, confirmpassword, emailid, major;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences user = getSharedPreferences(getApplicationContext().getPackageName(),
                Context.MODE_PRIVATE);//getPreferences(MODE_PRIVATE);
        if(user.contains("username"))
        {
            //Launch Activity
            startActivity(new Intent(HomeActivity.this, RegisterActivity.class));
        }

        setContentView(R.layout.register);
        username = (EditText)findViewById(R.id.fname);
        password = (EditText)findViewById(R.id.password);
        emailid = (EditText)findViewById(R.id.emailid);
        major = (EditText)findViewById(R.id.major);
        register = (Button)findViewById(R.id.registerbutton);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences userDetails = getSharedPreferences(getApplicationContext().getPackageName(),
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = userDetails.edit();
                edit.clear();
                edit.putString("username", username.getText().toString().trim());
                edit.putString("password", password.getText().toString().trim());
                edit.putString("userid", emailid.getText().toString());
                edit.putString("major", major.getText().toString());
                edit.commit();

                User user = new User(Integer.parseInt(emailid.getText().toString()), username.getText().toString(), major.getText().toString());

                invokeWS(user);
                startActivity(new Intent(HomeActivity.this, RegisterActivity.class));
            }
        });
    }

    public void invokeWS(User params)
    {
        AsyncHttpClient client = new AsyncHttpClient();
        HttpEntity body = null;
        try{
            body = new StringEntity(new Gson().toJson(params));
            client.post(getApplicationContext(), Utils.url + "users/create-user", body, "application/json", new AsyncHttpResponseHandler(){
                //client.get(Utils.url + "groups/add-user-to-group", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String response) {
                    Toast.makeText(getApplicationContext(), "User created succesfully " + response, Toast.LENGTH_LONG).show();
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
