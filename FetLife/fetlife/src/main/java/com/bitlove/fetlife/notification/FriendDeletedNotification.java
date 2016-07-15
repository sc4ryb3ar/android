package com.bitlove.fetlife.notification;

import com.bitlove.fetlife.FetLifeApplication;

import org.json.JSONObject;

public class FriendDeletedNotification extends OneSignalNotification {

    public FriendDeletedNotification(String title, String message, String launchUrl, JSONObject additionalData, String id) {
        super(title, message,launchUrl,additionalData,id);
    }

    @Override
    public boolean process(FetLifeApplication fetLifeApplication) {
        return true;
    }

    @Override
    public void onClick(FetLifeApplication fetLifeApplication) {
    }
}
