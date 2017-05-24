package com.bitlove.fetlife.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.model.pojos.fetlife.db.NotificationHistoryItem;
import com.bitlove.fetlife.model.pojos.fetlife.db.NotificationHistoryItem_Table;
import com.bitlove.fetlife.view.screen.BaseActivity;
import com.bitlove.fetlife.view.screen.resource.FriendRequestsActivity;
import com.bitlove.fetlife.view.screen.resource.NotificationHistoryActivity;
import com.onesignal.OneSignal;
import com.raizlabs.android.dbflow.sql.language.Select;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InfoNotification extends OneSignalNotification {

    private static List<InfoNotification> notifications = new ArrayList<InfoNotification>();

    protected String collapseId;

    public InfoNotification(String title, String message, String launchUrl, JSONObject additionalData, String id, String group) {
        super(title, message,launchUrl,additionalData,id,group);
        collapseId = additionalData != null ? additionalData.optString(NotificationParser.JSON_FIELD_STRING_COLLAPSE_ID) : null;
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
                        .setContentTitle(title)
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setGroup(getClass().getSimpleName())
                        .setVisibility(Notification.VISIBILITY_SECRET)
                        .setContentIntent(contentPendingIntent)
                        .setVibrate(fetLifeApplication.getUserSessionManager().getNotificationVibration())
                        .setColor(fetLifeApplication.getUserSessionManager().getNotificationColor())
                        .setSound(fetLifeApplication.getUserSessionManager().getNotificationRingtone());

        int notificationId = ++OneSignalNotification.NOTIFICATION_ID_INFO_INTERVAL;

        // Sets an ID for the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(fetLifeApplication);
        notificationManager.notify(notificationId, notificationBuilder.build());

        onNotificationDisplayed(fetLifeApplication, notificationId);
        }
    }

    @Override
    Intent getIntent(Context context) {
        if (launchUrl != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse(launchUrl));
            return intent;
        } else {
            Intent intent = NotificationHistoryActivity.createIntent(context,true);
            intent.putExtra(BaseActivity.EXTRA_NOTIFICATION_SOURCE_TYPE,NotificationParser.JSON_VALUE_TYPE_INFO);
            return intent;
        }
    }

    @Override
    public void onNotificationDisplayed(FetLifeApplication fetLifeApplication, int notificationId) {
        NotificationHistoryItem notificationHistoryItem = createNotificationItem(notificationId);
        if (collapseId != null && collapseId.trim().length() != 0) {
            NotificationHistoryItem toBeCollapsedNotification = new Select().from(NotificationHistoryItem.class).where(NotificationHistoryItem_Table.collapseId.eq(notificationHistoryItem.getCollapseId())).querySingle();
            if (toBeCollapsedNotification != null) {
                OneSignal.cancelNotification(toBeCollapsedNotification.getDisplayId());
                toBeCollapsedNotification.delete();
            }
        }
        notificationHistoryItem.save();
    }

    protected NotificationHistoryItem createNotificationItem(int notificationId) {
        NotificationHistoryItem notificationItem = new NotificationHistoryItem();
        notificationItem.setDisplayId(notificationId);
        notificationItem.setDisplayHeader(title);
        notificationItem.setDisplayMessage(message);
        notificationItem.setLaunchUrl(launchUrl);
        notificationItem.setCollapseId(collapseId);
        return notificationItem;
    }

    @Override
    @Deprecated
    public void onClick(FetLifeApplication fetLifeApplication) {
//        if (launchUrl != null && launchUrl.trim().length() != 0) {
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.setData(Uri.parse(launchUrl));
//            fetLifeApplication.startActivity(intent);
//        } else {
//            NotificationHistoryActivity.startActivity(fetLifeApplication, true);
//        }
    }

    @Override
    public String getAssociatedPreferenceKey(Context context) {
        return context.getString(R.string.settings_key_notification_info_enabled);
    }

}
