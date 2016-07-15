package com.bitlove.fetlife.notification;

import org.json.JSONObject;

public class NotificationParser {

    public static final String JSON_FIELD_JSONOBJECT_ADDITIONA_DATA = "a";
    public static final String JSON_FIELD_STRING_LAUNCHURL = "launchURL";
    public static final String JSON_FIELD_STRING_TITLE = "title";
    public static final String JSON_FIELD_STRING_U = "u";
    public static final String JSON_FIELD_STRING_ID = "i";

    public static final String JSON_FIELD_STRING_CONVERSATIONID = "conversation_id";
    public static final String JSON_FIELD_STRING_NICKNAME = "nickname";

    private static final String JSON_FIELD_STRING_TYPE = "type";
    private static final String JSON_FIELD_STACKED_NOTIFICATION = "stacked_notifications";

//    public OneSignalNotification parseNotification(String message, JSONObject notificationJson) {
//
//        String url = notificationJson.optString(JSON_FIELD_STRING_U);
//        String id = notificationJson.optString(JSON_FIELD_STRING_ID);
//        JSONObject additionalData = notificationJson.optJSONObject(JSON_FIELD_JSONOBJECT_ADDITIONA_DATA);
//
//        return parseNotification(message, url, additionalData, id);
//    }

    public OneSignalNotification parseNotification(String title, String message, String launchUrl, JSONObject additionalData, String id) {

        if (additionalData == null && launchUrl == null) {
            return new UnknownNotification(title, message, launchUrl, additionalData, id);
        } else if (launchUrl != null) {
            return new InfoNotification(title, message, launchUrl, additionalData, id);
        }

        if (additionalData.has(JSON_FIELD_STACKED_NOTIFICATION)) {
            return new StackedNotification(title, message, launchUrl, additionalData, id);
        }

        String type = additionalData.optString(JSON_FIELD_STRING_TYPE).toLowerCase();

        switch (type) {
            case "info":
                return new InfoNotification(title, message, launchUrl, additionalData, id);
            case "version":
                return new VersionNotification(title, message, launchUrl, additionalData, id);
            case "conversation_created":
            case "message_created":
                return new MessageNotification(title, message, launchUrl, additionalData, id);
            case "friendship_request_created":
                return new FriendRequestNotification(title, message, launchUrl, additionalData, id);
            case "friendship_request_accepted":
                return new FriendAddedNotification(title, message, launchUrl, additionalData, id);
            case "friend_deleted":
                return new FriendDeletedNotification(title, message, launchUrl, additionalData, id);
//                case "conversation_archived":
//                    return new ConversationArchivedNotification(message, launchUrl, additionalData, id);
            default:
                return new UnknownNotification(title, message, launchUrl, additionalData, id);
        }

    }

}
