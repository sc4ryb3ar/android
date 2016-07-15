package com.bitlove.fetlife.notification;

import org.json.JSONObject;

public class NotificationParser {

    public static final String JSON_FIELD_JSONOBJECT_ADDITIONA_DATA = "a";
    public static final String JSON_FIELD_STRING_LAUNCHURL = "u";
    public static final String JSON_FIELD_STRING_ID = "i";

    public static final String JSON_FIELD_STRING_CONVERSATIONID = "conversation_id";

    private static final String JSON_FIELD_STRING_TYPE = "type";
    private static final String JSON_FIELD_STACKED_NOTIFICATION = "stacked_notifications";

    public OneSignalNotification parseNotification(String message, JSONObject notificationJson) {

        String url = notificationJson.optString(JSON_FIELD_STRING_LAUNCHURL);
        String id = notificationJson.optString(JSON_FIELD_STRING_ID);
        JSONObject additionalData = notificationJson.optJSONObject(JSON_FIELD_JSONOBJECT_ADDITIONA_DATA);

        return parseNotification(message, url, additionalData, id);
    }

    public OneSignalNotification parseNotification(String message, String launchUrl, JSONObject additionalData, String id) {

        if (additionalData == null && launchUrl == null) {
            return new UnknownNotification(message, launchUrl, additionalData, id);
        } else if (launchUrl != null) {
            return new UrlNotification(message, launchUrl, additionalData, id);
        }

        if (additionalData.has(JSON_FIELD_STACKED_NOTIFICATION)) {
            return new StackedNotification(message, launchUrl, additionalData, id);
        }

        String type = additionalData.optString(JSON_FIELD_STRING_TYPE).toLowerCase();

        switch (type) {
            case "info":
                return new InfoNotification(message, launchUrl, additionalData, id);
            case "new_version":
                return new VersionNotification(message, launchUrl, additionalData, id);
            case "conversation_created":
            case "message_created":
                return new MessageNotification(message, launchUrl, additionalData, id);
            case "friendrequest_created":
                return new FriendRequestNotification(message, launchUrl, additionalData, id);
            case "friend_created":
                return new FriendAddedNotification(message, launchUrl, additionalData, id);
            case "friend_deleted":
                return new FriendDeletedNotification(message, launchUrl, additionalData, id);
            case "url":
                return new UrlNotification(message, launchUrl, additionalData, id);
//                case "conversation_archived":
//                    return new ConversationArchivedNotification(message, launchUrl, additionalData, id);
            default:
                return new UnknownNotification(message, launchUrl, additionalData, id);
        }

    }

}
