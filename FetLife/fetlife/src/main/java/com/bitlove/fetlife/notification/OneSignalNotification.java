package com.bitlove.fetlife.notification;

import com.bitlove.fetlife.FetLifeApplication;

import org.json.JSONObject;

public abstract class OneSignalNotification {

    protected final String id;
    protected final String message;
    protected final String launchUrl;
    protected final JSONObject additionalData;

    public OneSignalNotification(String message, String launchUrl, JSONObject additionalData, String id) {
        this.message = message;
        this.launchUrl = launchUrl;
        this.additionalData = additionalData;
        this.id = id;
    }

    public abstract boolean process(FetLifeApplication fetLifeApplication);

    public abstract void onClick(FetLifeApplication fetLifeApplication);

}
