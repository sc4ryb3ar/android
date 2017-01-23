package com.bitlove.fetlife.notification;

import com.bitlove.fetlife.BuildConfig;
import com.bitlove.fetlife.FetLifeApplication;
import com.onesignal.OSNotification;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OSNotificationPayload;
import com.onesignal.OSNotificationReceivedResult;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NotificationParser {

    public static final String JSON_FIELD_STRING_LAUNCHURL = "launchURL";
    public static final String JSON_FIELD_STRING_TITLE = "title";
    public static final String JSON_FIELD_STRING_GROUP = "grp";

    public static final String JSON_FIELD_STRING_CONVERSATIONID = "conversation_id";
    public static final String JSON_FIELD_STRING_NICKNAME = "nickname";

    public static final String JSON_FIELD_STRING_COLLAPSE_ID = "collapse_id";
    public static final java.lang.String JSON_FIELD_STRING_VERSION = "version";

    private static final String JSON_FIELD_STRING_TYPE = "type";
    private static final java.lang.String JSON_FIELD_INT_MIN_VERSION = "min_version";
    private static final java.lang.String JSON_FIELD_INT_MAX_VERSION = "max_version";

    public static final String JSON_VALUE_GROUP_INFO = "info";
    public static final String JSON_VALUE_GROUP_FETLIFE_MESSAGE = "fetlife_messages";

    public static final String JSON_VALUE_TYPE_INFO = "info";

    public static final String JSON_VALUE_TYPE_NEW_CONVERSATION = "conversation_new";
    public static final String JSON_VALUE_TYPE_CONVERSATION_RESPONSE = "conversation_response";
    public static final String JSON_VALUE_TYPE_FRIEND_REQUEST = "friend_request";

    //Backward compatibility

    public static final String JSON_VALUE_TYPE_LEGACY_VERSION = "version";
    public static final String JSON_VALUE_GROUP_LEGACY_FETLIFE = "fetlife";
    public static final String JSON_VALUE_GROUP_LEGACY_MESSAGE = "messages";
    public static final String JSON_VALUE_TYPE_LEGACY_CONVERSATION_CREATED = "conversation_created";
    public static final String JSON_VALUE_TYPE_LEGACY_MESSAGE_CREATED = "message_created";
    public static final String JSON_VALUE_TYPE_LEGACY_FRIEND_REQUEST_CREATED = "friendship_request_created";

    public OneSignalNotification parseNotification(FetLifeApplication fetLifeApplication, OSNotificationOpenResult osNotificationOpenResult) {

        OSNotification notification = osNotificationOpenResult.notification;
        List<OSNotificationPayload> groupedNotifications = notification.groupedNotifications;

        if (groupedNotifications == null || groupedNotifications.isEmpty()) {
            return parseNotification(fetLifeApplication, notification.payload);
        }

        List<OneSignalNotification> subNotificationList = new ArrayList<>(groupedNotifications.size());

        for (OSNotificationPayload payload : groupedNotifications) {
            OneSignalNotification subNotification = parseNotification(fetLifeApplication, payload);
            subNotificationList.add(subNotification);
        }

        StackedNotification stackedNotification = new StackedNotification(notification.payload.title, notification.payload.groupMessage, notification.payload.launchURL, notification.payload.additionalData, notification.payload.notificationID, notification.payload.groupKey, subNotificationList);
        return stackedNotification;
    }

    public OneSignalNotification parseNotification(FetLifeApplication fetLifeApplication, OSNotificationReceivedResult osNotificationReceivedResult) {
        return parseNotification(fetLifeApplication, osNotificationReceivedResult.payload);
    }

    private OneSignalNotification parseNotification(FetLifeApplication fetLifeApplication, OSNotificationPayload osNotificationPayload) {

        JSONObject additionalData = osNotificationPayload.additionalData;
        String id = osNotificationPayload.notificationID;
        String title = osNotificationPayload.title;
        String message = osNotificationPayload.body;
        String launchUrl = osNotificationPayload.launchURL;
        String group = osNotificationPayload.groupKey;

        //Check Version relevance

        String minVersion = additionalData != null ? additionalData.optString(JSON_FIELD_INT_MIN_VERSION) : null;
        if (minVersion != null && minVersion.trim().length() > 0) {
            try {
                int minVersionInt = Integer.parseInt(minVersion);
                if (minVersionInt > fetLifeApplication.getVersionNumber()) {
                    return new UnknownNotification(title, message, launchUrl, additionalData, id, group);
                }
            } catch (NumberFormatException nfe) {
                //skip
            }
        }

        String maxVersion = additionalData != null ? additionalData.optString(JSON_FIELD_INT_MAX_VERSION) : null;
        if (maxVersion != null && maxVersion.trim().length() > 0) {
            try {
                int maxVersionInt = Integer.parseInt(maxVersion);
                if (maxVersionInt < fetLifeApplication.getVersionNumber()) {
                    return new UnknownNotification(title, message, launchUrl, additionalData, id, group);
                }
            } catch (NumberFormatException nfe) {
                //skip
            }
        }

        //Check Type

        String type = additionalData != null ? additionalData.optString(JSON_FIELD_STRING_TYPE) : "";
        if (type != null) {
            type.toLowerCase();
        }

        switch (type) {
            case JSON_VALUE_TYPE_INFO:
                return new InfoNotification(title, message, launchUrl, additionalData, id, group);
            case JSON_VALUE_TYPE_LEGACY_VERSION:
                return new VersionNotification(title, message, launchUrl, additionalData, id, group);
            case JSON_VALUE_TYPE_LEGACY_CONVERSATION_CREATED:
            case JSON_VALUE_TYPE_LEGACY_MESSAGE_CREATED:
            case JSON_VALUE_TYPE_NEW_CONVERSATION:
            case JSON_VALUE_TYPE_CONVERSATION_RESPONSE:
                return new MessageNotification(title, message, launchUrl, additionalData, id, group);
            case JSON_VALUE_TYPE_FRIEND_REQUEST:
            case JSON_VALUE_TYPE_LEGACY_FRIEND_REQUEST_CREATED:
                return new FriendRequestNotification(title, message, launchUrl, additionalData, id, group);
//            case "friendship_request_accepted":
//                return new FriendAddedNotification(title, message, launchUrl, additionalData, id, group);
//            case "friend_deleted":
//                return new FriendDeletedNotification(title, message, launchUrl, additionalData, id, group);
//            case "conversation_archived":
//                return new ConversationArchivedNotification(message, launchUrl, additionalData, id, group);
            default:
                if (title != null || message != null) {
                    return new InfoNotification(title, message, launchUrl, additionalData, id, group);
                } else {
                    return new UnknownNotification(title, message, launchUrl, additionalData, id, group);
                }
        }

    }

}
