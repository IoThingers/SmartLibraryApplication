package com.google.android.gms.nearby.messages.samples.nearbybackgroundbeacons;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sharique on 4/17/2016.
 */
public class PlaceActivity extends Activity {
    private ImageView section1_image, section2_image, section3_image;
    String TAG = PlaceActivity.class.getSimpleName();
    String content;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        content = bundle.get("jsonresponse").toString();
        Log.i(TAG, "sharique content = " + content);
        setContentView(R.layout.place);
        section1_image = (ImageView) findViewById(R.id.imageView);
        section2_image = (ImageView) findViewById(R.id.imageView2);
        section3_image = (ImageView) findViewById(R.id.imageView3);

        int responsecode;
        ArrayList<Integer> percentage = new ArrayList<Integer>();
        if (content != null) {
            try {
                JSONObject jsonObj = new JSONObject(content);
                responsecode = jsonObj.getInt("responseCode");
                if (responsecode == 200) {
                    JSONArray sections = jsonObj.getJSONArray("response");
                    for(int i = 0; i < sections.length(); i++)
                    {
                        //percentage.add(((sections.getJSONObject(i).getInt("count")*100)/sections.getJSONObject(i).getInt("capacity")));
                        double count = sections.getJSONObject(i).getInt("count");
                        double capacity = sections.getJSONObject(i).getInt("capacity");
                        final int per = (int)((count*100)/capacity);

                        switch(i)
                        {
                            case 0:
                                if(per >= 75)//Red
                                {
                                    section1_image.setBackgroundResource(R.drawable.section1_red);
                                }
                                else if(per >= 30)//Yellow
                                {
                                    section1_image.setBackgroundResource(R.drawable.section1_yellow);
                                }
                                else //Green
                                {
                                    section1_image.setBackgroundResource(R.drawable.section1_green);
                                }
                                break;
                            case 1:
                                if(per >= 75)//Red
                                {
                                    section2_image.setBackgroundResource(R.drawable.section2_red);
                                }
                                else if(per >= 30)//Yellow
                                {
                                    section2_image.setBackgroundResource(R.drawable.section2_yellow);
                                }
                                else //Green
                                {
                                    section2_image.setBackgroundResource(R.drawable.section2_green);
                                }
                                break;
                            case 2:
                                if(per >= 75)//Red
                                {
                                    section3_image.setBackgroundResource(R.drawable.section3_red);
                                }
                                else if(per >= 30)//Yellow
                                {
                                    section3_image.setBackgroundResource(R.drawable.section3_yellow);
                                }
                                else //Green
                                {
                                    section3_image.setBackgroundResource(R.drawable.section3_green);
                                }
                                break;
                            default:
                                break;
                        }


                    }
                } else {
                    Toast.makeText(getApplicationContext(), "invokeWSSections WebApi response code failure", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();


    }
}
