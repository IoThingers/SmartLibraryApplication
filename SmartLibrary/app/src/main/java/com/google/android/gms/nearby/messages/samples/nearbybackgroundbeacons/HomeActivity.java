package com.google.android.gms.nearby.messages.samples.nearbybackgroundbeacons;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by sharique on 4/10/2016.
 */
public class HomeActivity extends Activity {

    private Button register;
    private EditText username, password, confirmpassword, emailid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences user = getPreferences(MODE_PRIVATE);
        if(user.contains("username"))
        {
            //Launch Activity
            startActivity(new Intent(HomeActivity.this, RegisterActivity.class));
        }

        setContentView(R.layout.register);
        username = (EditText)findViewById(R.id.name);
        password = (EditText)findViewById(R.id.password);
        emailid = (EditText)findViewById(R.id.emailid);
        register = (Button)findViewById(R.id.registerbutton);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences userDetails = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor edit = userDetails.edit();
                edit.clear();
                edit.putString("username", username.getText().toString().trim());
                edit.putString("password", password.getText().toString().trim());
                edit.putString("emailid", emailid.getText().toString());
                edit.commit();
                startActivity(new Intent(HomeActivity.this, RegisterActivity.class));
            }
        });
    }
}
