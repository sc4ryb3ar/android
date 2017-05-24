package com.bitlove.fetlife.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.view.screen.BaseActivity;
import com.bitlove.fetlife.view.screen.resource.FriendRequestsActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FriendRequestNotification extends OneSignalNotification {

    private static List<FriendRequestNotification> notifications = new ArrayList<FriendRequestNotification>();

    public FriendRequestNotification(String title, String message, String launchUrl, JSONObject additionalData, String id, String group) {
        super(title, message,launchUrl,additionalData,id,group);
    }

    @Override
    public boolean handle(FetLifeApplication fetLifeApplication) {
        return false;
    }

    public static void clearNotifications() {
        synchronized (notifications) {
            notifications.clear();
        }
    }

    @Override
    public void display(FetLifeApplication fetLifeApplication) {

        synchronized (notifications) {
        notifications.add(this);

        Intent contentIntent = getIntent(fetLifeApplication);

        PendingIntent contentPendingIntent =
                PendingIntent.getActivity(
                        fetLifeApplication,
                        0,
                        contentIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(fetLifeApplication)
                        .setLargeIcon(BitmapFactory.decodeResource(fetLifeApplication.getResources(),R.mipmap.app_icon_vanilla))
                        .setSmallIcon(R.drawable.ic_anonym_notif_small)
                        .setContentTitle("Title")
                        .setContentText("Text")
                        .setAutoCancel(true)
                        .setGroup(getClass().getSimpleName())
                        .setVisibility(Notification.VISIBILITY_SECRET)
                        .setContentIntent(contentPendingIntent)
                        .setVibrate(fetLifeApplication.getUserSessionManager().getNotificationVibration())
                        .setColor(fetLifeApplication.getUserSessionManager().getNotificationColor())
                        .setSound(fetLifeApplication.getUserSessionManager().getNotificationRingtone());

        NotificationCompat.InboxStyle inboxStyle =
                new NotificationCompat.InboxStyle();
        // Sets a title for the Inbox in expanded layout
        inboxStyle.setBigContentTitle("Big Title");
        inboxStyle.setSummaryText("Summary Text");
        // Moves events into the expanded layout
        for (int i=0; i < notifications.size(); i++) {
            inboxStyle.addLine(notifications.get(i).message);
        }
        // Moves the expanded layout object into the notification object.
        notificationBuilder.setStyle(inboxStyle);

        // Sets an ID for the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(fetLifeApplication);
        notificationManager.notify(OneSignalNotification.NOTIFICATION_ID_FRIEND_REQUEST, notificationBuilder.build());
        }
    }

    @Override
    Intent getIntent(Context context) {
        Intent intent = FriendRequestsActivity.createIntent(context,true);
        intent.putExtra(BaseActivity.EXTRA_NOTIFICATION_SOURCE_TYPE,NotificationParser.JSON_VALUE_TYPE_FRIEND_REQUEST);
        return intent;
    }

    @Override
    @Deprecated
    public void onClick(FetLifeApplication fetLifeApplication) {
//        FriendRequestsActivity.startActivity(fetLifeApplication, true);
    }

    @Override
    public String getAssociatedPreferenceKey(Context context) {
        return context.getString(R.string.settings_key_notification_friendrequests_enabled);
    }
}
