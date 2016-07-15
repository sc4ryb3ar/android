package com.bitlove.fetlife.notification;

import com.bitlove.fetlife.FetLifeApplication;

import org.json.JSONObject;

public class InfoNotification extends UrlNotification {

    public InfoNotification(String message, String launchUrl, JSONObject additionalData, String id) {
        super(message,launchUrl,additionalData,id);
    }

    @Override
    public boolean process(FetLifeApplication fetLifeApplication) {
        //TODO: save notification
        return false;
    }

    @Override
    public void onClick(FetLifeApplication fetLifeApplication) {
        if (launchUrl != null) {
            super.onClick(fetLifeApplication);
        } else {
            //TODO: open notification history
        }
    }
}
