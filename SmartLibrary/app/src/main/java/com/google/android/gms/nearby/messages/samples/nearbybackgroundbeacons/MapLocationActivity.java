package com.google.android.gms.nearby.messages.samples.nearbybackgroundbeacons;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

public class MapLocationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_location);
        String TAG = MapLocationActivity.class.getSimpleName();
        ImageView img = (ImageView) findViewById(R.id.imageView);
        try {
            Resources res = getResources();
            String mDrawableName = "room" + getIntent().getStringExtra("roomid");
            int resID = res.getIdentifier(mDrawableName, "drawable", getPackageName());
            Log.i(TAG, "Suryansh MapLocator " + resID);
            Drawable drawable = res.getDrawable(resID);
            img.setImageDrawable(drawable);
        }
        catch(Exception e){
            Toast.makeText(getApplicationContext(),"Location Not Available! Try Later",Toast.LENGTH_LONG).show();
        }

    }
}
