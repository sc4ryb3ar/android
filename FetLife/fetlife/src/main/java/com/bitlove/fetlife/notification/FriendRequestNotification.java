package com.bitlove.fetlife.notification;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.view.FriendRequestsActivity;

import org.json.JSONObject;

/**
 * Created by Titan on 8/4/2016.
 */
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
        FriendRequestsActivity.startActivity(fetLifeApplication);
    }
}
