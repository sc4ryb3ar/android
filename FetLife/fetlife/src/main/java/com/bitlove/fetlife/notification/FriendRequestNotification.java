package com.bitlove.fetlife.notification;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.view.activity.FriendRequestsActivity;

import org.json.JSONObject;

public class FriendRequestNotification extends OneSignalNotification {

    public FriendRequestNotification(String title, String message, String launchUrl, JSONObject additionalData, String id, String group) {
        super(title, message,launchUrl,additionalData,id,group);
    }

    @Override
    public boolean handle(FetLifeApplication fetLifeApplication) {
        return false;
    }

    @Override
    public void onClick(FetLifeApplication fetLifeApplication) {
        FriendRequestsActivity.startActivity(fetLifeApplication, true);
    }
}
