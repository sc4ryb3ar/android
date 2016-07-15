package com.bitlove.fetlife.notification;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.view.ConversationsActivity;

import org.json.JSONObject;

public class FriendAddedNotification extends OneSignalNotification {

    public FriendAddedNotification(String message, String launchUrl, JSONObject additionalData, String id) {
        super(message,launchUrl,additionalData,id);
    }

    @Override
    public boolean process(FetLifeApplication fetLifeApplication) {
        return true;
    }

    @Override
    public void onClick(FetLifeApplication fetLifeApplication) {
        //TODO: add support for other stacked notifications, currently there is no others
        ConversationsActivity.startActivity(fetLifeApplication);
    }
}
