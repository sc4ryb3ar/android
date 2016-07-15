package com.bitlove.fetlife.notification;

import com.bitlove.fetlife.FetLifeApplication;

import org.json.JSONObject;

public class StackedNotification extends OneSignalNotification {

    public StackedNotification(String title, String message, String launchUrl, JSONObject additionalData, String id) {
        super(title, message, launchUrl, additionalData, id);
    }

    @Override
    public boolean process(FetLifeApplication fetLifeApplication) {
        //Should not be invoked
        return false;
    }

    @Override
    public void onClick(FetLifeApplication fetLifeApplication) {

    }
}
