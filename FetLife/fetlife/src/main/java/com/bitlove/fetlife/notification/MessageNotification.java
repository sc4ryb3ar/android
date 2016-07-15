package com.bitlove.fetlife.notification;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.event.NewMessageEvent;
import com.bitlove.fetlife.model.service.FetLifeApiIntentService;

import org.json.JSONObject;

public class MessageNotification extends OneSignalNotification {

    private String conversationId;

    public MessageNotification(String message, String launchUrl, JSONObject additionalData, String id) {
        super(message,launchUrl,additionalData,id);
    }

    @Override
    public void process(FetLifeApplication fetLifeApplication) {

        conversationId = additionalData.optString(NotificationParser.JSON_FIELD_STRING_CONVERSATIONID);
        if (conversationId != null) {
            FetLifeApiIntentService.startApiCall(fetLifeApplication, FetLifeApiIntentService.ACTION_APICALL_MESSAGES, conversationId);
        } else {
            throw new IllegalArgumentException("Missing field");
        }

    }

    @Override
    public void onClick(FetLifeApplication fetLifeApplication) {
        fetLifeApplication.getEventBus().post(new NewMessageEvent(conversationId));
    }
}
