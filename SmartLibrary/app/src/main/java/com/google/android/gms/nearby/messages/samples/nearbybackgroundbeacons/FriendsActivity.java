package com.google.android.gms.nearby.messages.samples.nearbybackgroundbeacons;

import android.app.ListActivity;
import android.os.Bundle;
import android.provider.Contacts;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * Created by sharique on 4/4/2016.
 */
public class FriendsActivity extends ListActivity {
    TextView content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends);


        content = (TextView)findViewById(R.id.output);

        //listView = (ListView) findViewById(R.id.list);
        String[] values = new String[] { "Sharique", "Rahul", "Hero", "Hamza", "Suryansh" };

        // Define a new Adapter
        // First parameter - Context
        // Second parameter - Layout for the row
        // Third - the Array of data

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, values);


        // Assign adapter to List
        setListAdapter(adapter);
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
