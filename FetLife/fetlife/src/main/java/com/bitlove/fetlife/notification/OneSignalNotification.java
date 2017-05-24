package com.bitlove.fetlife.notification;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.bitlove.fetlife.FetLifeApplication;

import org.json.JSONObject;

public abstract class OneSignalNotification {

    public static final int NOTIFICATION_ID_ANONYM = 10;
    public static final int NOTIFICATION_ID_FRIEND_REQUEST = 20;
    public static final int NOTIFICATION_ID_MESSAGE = 30;
    public static int NOTIFICATION_ID_INFO_INTERVAL = 100;

    protected final String id;
    protected final String title;
    protected final String message;
    protected final String launchUrl;
    protected final String group;
    protected final JSONObject additionalData;

    public OneSignalNotification(String title, String message, String launchUrl, JSONObject additionalData, String id, String group) {
        this.title = title;
        this.message = message;
        this.launchUrl = launchUrl;
        this.additionalData = additionalData;
        this.group = group;
        this.id = id;
    }

    public void display(FetLifeApplication fetLifeApplication) {}

    Intent getIntent(Context context) {
        return null;
    }

    public abstract boolean handle(FetLifeApplication fetLifeApplication);

    public void onNotificationDisplayed(FetLifeApplication fetLifeApplication, int notificationId) {}

    public abstract void onClick(FetLifeApplication fetLifeApplication);

    public abstract String getAssociatedPreferenceKey(Context context);

    public boolean isEnabled(FetLifeApplication fetLifeApplication) {
        SharedPreferences sharedPreferences = fetLifeApplication.getUserSessionManager().getActiveUserPreferences();
        return sharedPreferences.getBoolean(getAssociatedPreferenceKey(fetLifeApplication), true);
    }

}
