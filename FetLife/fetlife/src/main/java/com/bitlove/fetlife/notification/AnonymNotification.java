package com.bitlove.fetlife.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.view.screen.BaseActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AnonymNotification {

    private static List<AnonymNotification> notifications = new ArrayList<AnonymNotification>();

    private final OneSignalNotification oneSignalNotification;

    public AnonymNotification(OneSignalNotification oneSignalNotification) {
        this.oneSignalNotification = oneSignalNotification;
    }

    public static void clearNotifications() {
        synchronized (notifications) {
            notifications.clear();
        }
    }

    public void display(FetLifeApplication fetLifeApplication) {
        synchronized (notifications) {

        notifications.add(this);

        Intent contentIntent = oneSignalNotification.getIntent(fetLifeApplication);
        contentIntent.putExtra(BaseActivity.EXTRA_NOTIFICATION_SOURCE_TYPE,getClass().getSimpleName());

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
                        .setContentTitle(fetLifeApplication.getString(R.string.noification_title_new_one_or_more_notification))
                        .setAutoCancel(true)
                        .setGroup(getClass().getSimpleName())
                        .setVisibility(Notification.VISIBILITY_SECRET)
                        .setContentIntent(contentPendingIntent)
                        .setVibrate(fetLifeApplication.getUserSessionManager().getNotificationVibration())
                        .setColor(fetLifeApplication.getUserSessionManager().getNotificationColor())
                        .setSound(fetLifeApplication.getUserSessionManager().getNotificationRingtone());

        if (notifications.size() > 1) {
            notificationBuilder.setContentText(fetLifeApplication.getString(R.string.noification_text_new_anonym_notifications, notifications.size()));
        } else {
            notificationBuilder.setContentText(fetLifeApplication.getString(R.string.noification_text_new_anonym_notification));
        }

//        NotificationCompat.InboxStyle inboxStyle =
//                new NotificationCompat.InboxStyle();
//        // Sets a title for the Inbox in expanded layout
//        inboxStyle.setBigContentTitle("Title - Notification");
//        inboxStyle.setSummaryText("You have " + notifications.size()+ " Notifications.");
//        // Moves events into the expanded layout
//        for (int i=0; i < notifications.size(); i++) {
//            inboxStyle.addLine("Private Message");
//        }
//        // Moves the expanded layout object into the notification object.
//        notificationBuilder.setStyle(inboxStyle);

        // Sets an ID for the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(fetLifeApplication);
        notificationManager.notify(OneSignalNotification.NOTIFICATION_ID_ANONYM, notificationBuilder.build());
        }
    }
}
