package com.google.android.gms.nearby.messages.samples.nearbybackgroundbeacons;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.MessageFilter;
import com.google.android.gms.nearby.messages.Strategy;
import com.google.android.gms.nearby.messages.SubscribeOptions;

/**
 * Created by sharique on 4/13/2016.
 */
public class ActiveUserReceiver extends BroadcastReceiver implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    private static final String TAG = ActiveUserReceiver.class.getSimpleName();
    private GoogleApiClient mGoogleApiClient = null;
    private Context c;

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "sharique onConnected is called");
        subscribe(c);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "sharique onConnectionSuspended is called");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "sharique onConnectionFailed is called");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "sharique onReceive is called");
        c = context;
        if(mGoogleApiClient == null)
        {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addApi(Nearby.MESSAGES_API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            mGoogleApiClient.connect();
        }
    }

    private void subscribe(final Context context) {
        Log.i(TAG, "attempting to subscribe");

        // Clean start every time we start subscribing.
        Utils.clearCachedMessages(context);

        if (!mGoogleApiClient.isConnected()) {
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }

            return;
        }
        Log.i(TAG, "Nearby.Messages.subscribe");
        MessageFilter filter = new MessageFilter.Builder()
                .includeNamespacedType("capable-avatar-126623", "String")
                .build();
        SubscribeOptions options = new SubscribeOptions.Builder()
                .setFilter(filter)
                        // Finds messages attached to BLE beacons. See
                        // https://developers.google.com/beacons/
                .setStrategy(Strategy.BLE_ONLY)
                .build();

        Nearby.Messages.subscribe(mGoogleApiClient, getPendingIntent(context), options)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            context.startService(getBackgroundSubscribeServiceIntent(context));
                        } else {
                            Log.i(TAG, "could not subscribe");
                            // handleUnsuccessfulNearbyResult(status);
                        }
                    }
                });
    }

    private PendingIntent getPendingIntent(Context context) {
        return PendingIntent.getService(context, 0,
                getBackgroundSubscribeServiceIntent(context), PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private Intent getBackgroundSubscribeServiceIntent(Context context) {
        return new Intent(context, BackgroundSubscribeIntentService.class);
    }
}
