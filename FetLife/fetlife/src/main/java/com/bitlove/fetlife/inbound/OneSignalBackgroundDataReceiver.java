package com.bitlove.fetlife.inbound;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.bitlove.fetlife.BuildConfig;
import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.notification.OneSignalNotification;

import org.json.JSONException;
import org.json.JSONObject;

public class OneSignalBackgroundDataReceiver extends WakefulBroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        Bundle dataBundle = intent.getBundleExtra("data");
        if (BuildConfig.DEBUG) {
            for (String key: dataBundle.keySet())
            {
                Log.d (getClass().getSimpleName(), key + " is a key in the bundle");
            }
        }
    }

}