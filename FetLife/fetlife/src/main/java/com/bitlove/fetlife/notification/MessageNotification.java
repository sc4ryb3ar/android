package com.bitlove.fetlife.notification;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.event.NewMessageEvent;
import com.bitlove.fetlife.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.view.screen.BaseActivity;
import com.bitlove.fetlife.view.screen.resource.ConversationsActivity;
import com.bitlove.fetlife.view.screen.resource.FriendRequestsActivity;
import com.bitlove.fetlife.view.screen.resource.MessagesActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MessageNotification extends OneSignalNotification {

    private static List<OneSignalNotification> notifications = new ArrayList<OneSignalNotification>();

    protected String conversationId;
    protected String nickname;

    public MessageNotification(String title, String message, String launchUrl, JSONObject additionalData, String id, String group) {
        super(title, message,launchUrl,additionalData,id, group);
        conversationId = additionalData.optString(NotificationParser.JSON_FIELD_STRING_CONVERSATIONID);
        nickname = additionalData.optString(NotificationParser.JSON_FIELD_STRING_NICKNAME);
    }

    @Override
    public boolean handle(FetLifeApplication fetLifeApplication) {
        if (conversationId != null) {
            FetLifeApiIntentService.startApiCall(fetLifeApplication, FetLifeApiIntentService.ACTION_APICALL_MESSAGES, conversationId);
        } else {
            throw new IllegalArgumentException("Missing field");
        }

        boolean conversationInForeground = false;
        boolean appInForeground = fetLifeApplication.isAppInForeground();

        if (appInForeground) {
            fetLifeApplication.getEventBus().post(new NewMessageEvent(conversationId));
            Activity foregroundActivity = fetLifeApplication.getForegroundActivity();
            if (foregroundActivity instanceof MessagesActivity) {
                conversationInForeground = conversationId.equals(((MessagesActivity)foregroundActivity).getConversationId());
            } else if (foregroundActivity instanceof  ConversationsActivity) {
                conversationInForeground = true;
            }
        }

        //TODO: display in app notification if the user is not on the same message screen
        return conversationInForeground;
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
            notificationManager.notify(OneSignalNotification.NOTIFICATION_ID_MESSAGE, notificationBuilder.build());
        }
    }

    @Override
     Intent getIntent(Context context) {
        Intent intent = StackedNotification.isSameConversations(notifications) != null ? MessagesActivity.createIntent(context,conversationId,nickname,null,true) : ConversationsActivity.createIntent(context,true);
        intent.putExtra(BaseActivity.EXTRA_NOTIFICATION_SOURCE_TYPE,NotificationParser.JSON_VALUE_TYPE_CONVERSATION_RESPONSE);
        return intent;
    }

    @Override
    @Deprecated
    public void onClick(FetLifeApplication fetLifeApplication) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            try {
//                TaskStackBuilder.create(fetLifeApplication).addNextIntent(ConversationsActivity.createIntent(fetLifeApplication, true)).addNextIntent(MessagesActivity.createIntent(fetLifeApplication, conversationId, nickname, null, true)).startActivities();
//            } catch (NullPointerException npe) {
//                //Apply workaround for OS bug
//                startLegacyConversationAndMessageActivity(fetLifeApplication);
//            }
//        } else {
//            startLegacyConversationAndMessageActivity(fetLifeApplication);
//        }
    }

//    private void startLegacyConversationAndMessageActivity(FetLifeApplication fetLifeApplication) {
//        ConversationsActivity.startActivity(fetLifeApplication, true);
//        MessagesActivity.startActivity(fetLifeApplication, conversationId, nickname, null, true);
//    }

    @Override
    public String getAssociatedPreferenceKey(Context context) {
        return context.getString(R.string.settings_key_notification_messages_enabled);
    }
}
