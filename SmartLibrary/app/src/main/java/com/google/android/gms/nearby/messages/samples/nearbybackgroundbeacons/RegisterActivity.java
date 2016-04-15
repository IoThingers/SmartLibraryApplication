package com.google.android.gms.nearby.messages.samples.nearbybackgroundbeacons;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.MessageFilter;
import com.google.android.gms.nearby.messages.Strategy;
import com.google.android.gms.nearby.messages.SubscribeOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by sharique on 4/4/2016.
 */
public class RegisterActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = RegisterActivity.class.getSimpleName();

    // Constants for persisting values to Bundle.
    private static final String KEY_SUB_STATE = "sub-state";
    private static final String KEY_RESOLVING_ERROR = "resolving-error";

    // Progress Dialog Object
    ProgressDialog prgDialog;

    // Error Msg TextView Object
    TextView errorMsg;

    // Enum to track subscription state.
    private enum SubState {
        NOT_SUBSCRIBING,
        ATTEMPTING_TO_SUBSCRIBE,
        SUBSCRIBING,
        ATTEMPTING_TO_UNSUBSCRIBE
    }

    /**
     * Backing data structure for {@code mNearbyMessagesArrayAdapter}.
     */
    private List<String> mNearbyMessagesList = new ArrayList<>();

    private boolean mResolvingError = false;

    /**
     * Entry point to Google Play Services.
     */
    private GoogleApiClient mGoogleApiClient;

    private PendingIntent pendingIntent;

    // Fields for tracking subscription state.
    private SubState mSubState = SubState.NOT_SUBSCRIBING;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_main);
        final Button creategroup = (Button) findViewById(R.id.create);
        final Button joingroup = (Button) findViewById(R.id.join);
        final Button myfriends = (Button) findViewById(R.id.friends);
        final Button delmygroup = (Button) findViewById(R.id.delete);
        creategroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mSubState) {
                    case NOT_SUBSCRIBING:
                    case ATTEMPTING_TO_UNSUBSCRIBE:
                        mSubState = SubState.ATTEMPTING_TO_SUBSCRIBE;
                        subscribe();
                        break;
                    case SUBSCRIBING:
                    case ATTEMPTING_TO_SUBSCRIBE:
                        mSubState = SubState.ATTEMPTING_TO_UNSUBSCRIBE;
                        unsubscribe();
                        break;
                }
            }
        });

        myfriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, FriendsActivity.class));
            }
        });

        joingroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, GroupActivity.class));
            }
        });

        delmygroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //we will send api call on cloud
            }
        });


        // Instantiate Progress Dialog object
        prgDialog = new ProgressDialog(this);
        // Set Progress Dialog Text
        prgDialog.setMessage("Please wait...");
        // Set Cancelable as False
        prgDialog.setCancelable(false);
        RequestParams params = new RequestParams();
        params.put("group-id", "123");
        params.put("user-id", "123");
        invokeWS(params);


        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.MONTH, 6);
        calendar.set(Calendar.YEAR, 2013);
        calendar.set(Calendar.DAY_OF_MONTH, 13);

        calendar.set(Calendar.HOUR_OF_DAY, 20);
        calendar.set(Calendar.MINUTE, 48);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.AM_PM,Calendar.PM);

        Intent myIntent = new Intent(RegisterActivity.this, ActiveUserReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(RegisterActivity.this, 0, myIntent,0);

        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 30 * 1, pendingIntent);

    }


    private void subscribe() {
        Log.i(TAG, "attempting to subscribe");

        // Clean start every time we start subscribing.
        Utils.clearCachedMessages(this);

        if (!mGoogleApiClient.isConnected()) {
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
            Toast.makeText(this, "Press button again - Google API not connected",
                    Toast.LENGTH_LONG).show();
            return;
        }

        MessageFilter filter = new MessageFilter.Builder()
                .includeNamespacedType("capable-avatar-126623", "String")
                .build();
        SubscribeOptions options = new SubscribeOptions.Builder()
                .setFilter(filter)
                        // Finds messages attached to BLE beacons. See
                        // https://developers.google.com/beacons/
                .setStrategy(Strategy.BLE_ONLY)
                .build();

        Nearby.Messages.subscribe(mGoogleApiClient, getPendingIntent(), options)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            Log.i(TAG, "subscribed successfully");
                            mSubState = SubState.SUBSCRIBING;
                            // Start background service for handling the notification.
                            startService(getBackgroundSubscribeServiceIntent());
                        } else {
                            Log.i(TAG, "could not subscribe");
                            handleUnsuccessfulNearbyResult(status);
                        }
                    }
                });
    }

    private void unsubscribe() {
        Log.i(TAG, "attempting to unsubscribe from background updates");
        if (!mGoogleApiClient.isConnected()) {
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
            Toast.makeText(this, "Press button again - Google API not connected",
                    Toast.LENGTH_LONG).show();
            return;
        }

        Nearby.Messages.unsubscribe(mGoogleApiClient, getPendingIntent())
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            Log.i(TAG, "unsubscribed successfully");
                            mSubState = SubState.NOT_SUBSCRIBING;
                            BackgroundSubscribeIntentService.cancelNotification(getApplicationContext());
                        } else {
                            Log.i(TAG, "could not unsubscribe");
                            handleUnsuccessfulNearbyResult(status);
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Nearby.MESSAGES_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        getSharedPreferences(getApplicationContext().getPackageName(),
                Context.MODE_PRIVATE)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        getSharedPreferences(getApplicationContext().getPackageName(),
                Context.MODE_PRIVATE)
                .unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    private PendingIntent getPendingIntent() {
        return PendingIntent.getService(this, 0,
                getBackgroundSubscribeServiceIntent(), PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private Intent getBackgroundSubscribeServiceIntent() {
        return new Intent(this, BackgroundSubscribeIntentService.class);
    }

    private void handleUnsuccessfulNearbyResult(Status status) {
        Log.i(TAG, "processing error, status = " + status);
        if (status.hasResolution()) {

            // This is to avoid showing the dialog twice.
            if (!mResolvingError) {
                try {
                    status.startResolutionForResult(this, Constants.REQUEST_RESOLVE_ERROR);
                    mResolvingError = true;
                } catch (IntentSender.SendIntentException unlikely) {
                    Log.e(TAG, "Exception when starting resolution", unlikely);
                }
            }
        } else if (!status.isSuccess()) {
            Log.e(TAG, "Could not resolve error. Status: " + status);
            //resetToDefaultState();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "GoogleApiClient connected");
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.w(TAG, "GoogleApiClient connection suspended: "
                + connectionSuspendedCauseToString(cause));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // For simplicity, we don't handle connection failure thoroughly in this sample. Refer to
        // the following Google Play services doc for more details:
        // http://developer.android.com/google/auth/api-client.html
        Log.w(TAG, "connection to GoogleApiClient failed");
    }

    private static String connectionSuspendedCauseToString(int cause) {
        switch (cause) {
            case CAUSE_NETWORK_LOST:
                return "CAUSE_NETWORK_LOST";
            case CAUSE_SERVICE_DISCONNECTED:
                return "CAUSE_SERVICE_DISCONNECTED";
            default:
                return "CAUSE_UNKNOWN: " + cause;
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.i(TAG, "sharique onSharedPreferenceChanged called");
        if (TextUtils.equals(key, Constants.KEY_CACHED_MESSAGES)) {
            List<String> messages = new ArrayList<>(Utils.getCachedMessages(this));
            mNearbyMessagesList.clear();
            for (String message : messages) {
                mNearbyMessagesList.add(message);
                Toast.makeText(this, "found = " + message,
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void invokeWS(RequestParams params){
        // Show Progress Dialog
        prgDialog.show();
        // Make RESTful webservice call using AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://anyonethere.cloudapp.net:8080/groups/add-user-to-group", params, new AsyncHttpResponseHandler() {
        //client.get("http://192.168.2.2:9999/useraccount/login/dologin",params ,new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http response code '200'
            @Override
            public void onSuccess(String response) {
                // Hide Progress Dialog
                prgDialog.hide();
                try {
                    // JSON Object
                    Toast.makeText(getApplicationContext(), "You are successfully logged in! = "+ response, Toast.LENGTH_LONG).show();
                    JSONObject obj = new JSONObject(response);
                    // When the JSON response has status boolean value assigned with true
                    /*if(obj.getBoolean("status")){
                        Toast.makeText(getApplicationContext(), "You are successfully logged in!", Toast.LENGTH_LONG).show();
                        // Navigate to Home screen
                        //navigatetoHomeActivity();
                    }
                    // Else display error message
                    else{
                        errorMsg.setText(obj.getString("error_msg"));
                        Toast.makeText(getApplicationContext(), obj.getString("error_msg"), Toast.LENGTH_LONG).show();
                    }*/

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
                prgDialog.hide();
                // When Http response code is '404'
                if(statusCode == 404){
                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if(statusCode == 500){
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else{
                    Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
