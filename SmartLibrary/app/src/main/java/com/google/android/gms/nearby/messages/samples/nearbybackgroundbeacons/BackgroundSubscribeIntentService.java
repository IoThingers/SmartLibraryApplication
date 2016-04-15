/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.gms.nearby.messages.samples.nearbybackgroundbeacons;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.smartlibrary.entities.User;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.util.List;


/**
 * While subscribed in the background (see {@link MainActivityFragment#subscribe()}), this
 * service shows a persistent notification with the current set of messages from nearby beacons.
 * Nearby launches this service when a message is found or lost, and this service updates the
 * notification, then stops itself.
 */
public class BackgroundSubscribeIntentService extends IntentService {
    private static final int MESSAGES_NOTIFICATION_ID = 1;
    private static final int NUM_MESSAGES_IN_NOTIFICATION = 5;
    private static final String TAG = BackgroundSubscribeIntentService.class.getSimpleName();
    SharedPreferences user;
    public BackgroundSubscribeIntentService() {
        super("BackgroundSubscribeIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "sharique service onCreate() called");
        user = getSharedPreferences(getApplicationContext().getPackageName(),
                Context.MODE_PRIVATE);

        //updateNotification();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "sharique onHandleIntent called");
        if (intent != null) {
            Nearby.Messages.handleIntent(intent, new MessageListener() {
                @Override
                public void onFound(Message message) {
                    Log.i(TAG, "sharique onFound called");
                    Utils.saveFoundMessage(getApplicationContext(), message);
                    RequestParams params = new RequestParams();
                    params.put("user-id", user.getString("userid", ""));
                    params.put("is-active", "true");
                    invokeWSUserActive(params);
                    //updateNotification();
                }

                @Override
                public void onLost(Message message) {
                    Utils.removeLostMessage(getApplicationContext(), message);
                    Log.i(TAG, "sharique onLost called");
                    RequestParams params = new RequestParams();
                    params.put("user-id", user.getString("userid", ""));
                    params.put("is-active", "false");
                    invokeWSUserActive(params);
                    //updateNotification();
                }
            });
        }
    }

    private void updateNotification() {
        List<String> messages = Utils.getCachedMessages(getApplicationContext());
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent launchIntent = new Intent(getApplicationContext(), MainActivity.class);
        launchIntent.setAction(Intent.ACTION_MAIN);
        launchIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0,
                launchIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        String contentTitle = getContentTitle(messages);
        String contentText = getContentText(messages);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.star_on)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(contentText))
                .setOngoing(true)
                .setContentIntent(pi);
        notificationManager.notify(MESSAGES_NOTIFICATION_ID, notificationBuilder.build());
    }

    public static void cancelNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(MESSAGES_NOTIFICATION_ID);
    }

    private String getContentTitle(List<String> messages) {
        switch (messages.size()) {
            case 0:
                return getResources().getString(R.string.scanning);
            case 1:
                return getResources().getString(R.string.one_message);
            default:
                return getResources().getString(R.string.many_messages, messages.size());
        }
    }

    private String getContentText(List<String> messages) {
        String newline = System.getProperty("line.separator");
        if (messages.size() < NUM_MESSAGES_IN_NOTIFICATION) {
            return TextUtils.join(newline, messages);
        }
        return TextUtils.join(newline, messages.subList(0, NUM_MESSAGES_IN_NOTIFICATION)) +
                newline + "&#8230;";
    }

    public void invokeWSUserActive(RequestParams params)
    {
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(Utils.url + "users/set-user-activity", params, new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(String response) {

            }
            // When the response returned by REST has Http response code other than '200'
            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {

            }
        });

    }

    /*public void invokeWS()
    {
        AsyncHttpClient client = new AsyncHttpClient();
        HttpEntity body = null;
        try{
            body = new StringEntity("user-id=61961544&is-active=true"); //new StringEntity(new Gson().toJson(params));
            client.post(getApplicationContext(), Utils.url + "users/set-user-activity", body, "application/json", new AsyncHttpResponseHandler(){
                //client.get(Utils.url + "groups/add-user-to-group", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String response) {
                    //Toast.makeText(getApplicationContext(), "Registered succesfully " + response, Toast.LENGTH_LONG).show();
                }
                // When the response returned by REST has Http response code other than '200'
                @Override
                public void onFailure(int statusCode, Throwable error,
                                      String content) {
                    if(statusCode == 404){
                        //Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                    }
                    // When Http response code is '500'
                    else if(statusCode == 500){
                        //Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                    }
                    // When Http response code other than 404, 500
                    else{
                        //Toast.makeText(getApplicationContext(), "Unexpected Error occcured! [Most common Error: Device might not be connected to Internet or remote server is not up and running]", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        catch (UnsupportedEncodingException ex)
        {
            //Toast.makeText(getApplicationContext(), "UnsupportedEncodingException - Could not register", Toast.LENGTH_LONG).show();
        }


    }*/
}