package com.bitlove.fetlife.notification;

import com.bitlove.fetlife.FetLifeApplication;

import org.json.JSONObject;

public class FriendAddedNotification extends OneSignalNotification {

    public FriendAddedNotification(String message, String launchUrl, JSONObject additionalData, String id) {
        super(message,launchUrl,additionalData,id);
    }

    @Override
    public void process(FetLifeApplication fetLifeApplication) {

    }

    @Override
    public void onClick(FetLifeApplication fetLifeApplication) {

    }
}
