package com.bitlove.fetlife.view.activity.component;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.support.v7.app.NotificationCompat;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.event.AuthenticationFailedEvent;
import com.bitlove.fetlife.event.PictureUploadFailedEvent;
import com.bitlove.fetlife.event.PictureUploadFinishedEvent;
import com.bitlove.fetlife.event.PictureUploadStartedEvent;
import com.bitlove.fetlife.event.ServiceCallCancelEvent;
import com.bitlove.fetlife.event.ServiceCallCancelRequestEvent;
import com.bitlove.fetlife.event.ServiceCallFailedEvent;
import com.bitlove.fetlife.event.ServiceCallFinishedEvent;
import com.bitlove.fetlife.event.ServiceCallStartedEvent;
import com.bitlove.fetlife.event.VideoChunkUploadCancelEvent;
import com.bitlove.fetlife.event.VideoChunkUploadCancelRequestEvent;
import com.bitlove.fetlife.event.VideoChunkUploadFailedEvent;
import com.bitlove.fetlife.event.VideoChunkUploadFinishedEvent;
import com.bitlove.fetlife.event.VideoChunkUploadStartedEvent;
import com.bitlove.fetlife.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.model.service.ServiceCallCancelReceiver;
import com.bitlove.fetlife.view.activity.BaseActivity;

import java.util.HashMap;
import java.util.Map;

public class EventDisplayHandler {

    private static int PICTURE_UPLOAD_NOTIFICATION_ID = 42;
    private static int VIDEO_UPLOAD_NOTIFICATION_ID = 1042;
    private static Map<String,Integer> notificationIdMap = new HashMap<>();

    public void onAuthenticationFailed(BaseActivity baseActivity, AuthenticationFailedEvent authenticationFailedEvent) {
        baseActivity.showToast(baseActivity.getString(R.string.error_authentication_failed));
    }

    public void onServiceCallFailed(BaseActivity baseActivity, ServiceCallFailedEvent serviceCallFailedEvent) {
        if (serviceCallFailedEvent instanceof PictureUploadFailedEvent
                || FetLifeApiIntentService.ACTION_APICALL_UPLOAD_PICTURE.equals(serviceCallFailedEvent.getServiceCallAction())) {
            baseActivity.showToast(baseActivity.getString(R.string.message_image_upload_failed));
        } else if (serviceCallFailedEvent instanceof VideoChunkUploadFailedEvent) {
            VideoChunkUploadFailedEvent videoChunkUploadFailedEvent = (VideoChunkUploadFailedEvent) serviceCallFailedEvent;
            dismissNotification(baseActivity, getNotificationIdFromMediaId(videoChunkUploadFailedEvent.getVideoId()));
            baseActivity.showToast(videoChunkUploadFailedEvent.isCancelled() ? baseActivity.getString(R.string.message_video_upload_cancelled) : baseActivity.getString(R.string.message_video_upload_failed));
        } else if (FetLifeApiIntentService.ACTION_APICALL_UPLOAD_VIDEO.equals(serviceCallFailedEvent.getServiceCallAction())) {
            baseActivity.showToast(baseActivity.getString(R.string.message_video_upload_failed));
        } else {
            if (serviceCallFailedEvent.isServerConnectionFailed()) {
                baseActivity.showToast(baseActivity.getResources().getString(R.string.error_connection_failed));
            } else {
                baseActivity.showToast(baseActivity.getResources().getString(R.string.error_apicall_failed));
            }
        }
    }

    public void onServiceCallFinished(BaseActivity baseActivity, ServiceCallFinishedEvent serviceCallFinishedEvent) {
        if (serviceCallFinishedEvent instanceof PictureUploadFinishedEvent) {
            dismissNotification(baseActivity, getNotificationIdFromMediaId(((PictureUploadFinishedEvent)serviceCallFinishedEvent).getPictureId()));
            baseActivity.showToast(baseActivity.getString(R.string.message_image_upload_finished));
        } else if (serviceCallFinishedEvent instanceof VideoChunkUploadFinishedEvent) {
            VideoChunkUploadFinishedEvent videoChunkUploadFinishedEvent = (VideoChunkUploadFinishedEvent) serviceCallFinishedEvent;
            if (videoChunkUploadFinishedEvent.getChunk() == videoChunkUploadFinishedEvent.getChunkCount()) {
                dismissNotification(baseActivity, getNotificationIdFromMediaId(((VideoChunkUploadFinishedEvent)serviceCallFinishedEvent).getVideoId()));
                baseActivity.showToast(baseActivity.getString(R.string.message_video_upload_finished));
            }
        }
    }

    public void onServiceCallStarted(BaseActivity baseActivity, ServiceCallStartedEvent serviceCallStartedEvent) {
        if (serviceCallStartedEvent instanceof PictureUploadStartedEvent) {
            notificationIdMap.put(((PictureUploadStartedEvent)serviceCallStartedEvent).getPictureId(),PICTURE_UPLOAD_NOTIFICATION_ID);
            showProgressNotification(baseActivity, PICTURE_UPLOAD_NOTIFICATION_ID++, baseActivity.getString(R.string.notification_picture_upload_title), baseActivity.getString(R.string.notification_media_upload_text_inprogress), 0, 0, null);
            baseActivity.showToast(baseActivity.getString(R.string.message_image_upload_started));
        } else if (serviceCallStartedEvent instanceof VideoChunkUploadStartedEvent) {
            VideoChunkUploadStartedEvent videoChunkUploadStartedEvent = (VideoChunkUploadStartedEvent) serviceCallStartedEvent;
            if (videoChunkUploadStartedEvent.getChunk() == 1) {
                notificationIdMap.put(videoChunkUploadStartedEvent.getVideoId(),VIDEO_UPLOAD_NOTIFICATION_ID);
                showProgressNotification(baseActivity, VIDEO_UPLOAD_NOTIFICATION_ID, baseActivity.getString(R.string.notification_video_upload_title), baseActivity.getString(R.string.notification_media_upload_text_inprogress), 0, 0, ServiceCallCancelReceiver.createVideoCancelPendingIntent(baseActivity, VIDEO_UPLOAD_NOTIFICATION_ID, videoChunkUploadStartedEvent.getVideoId()));
                VIDEO_UPLOAD_NOTIFICATION_ID++;
                baseActivity.showToast(baseActivity.getString(R.string.message_video_upload_started));
            }
        }
    }

    public void onServiceCallCancelRequested(BaseActivity baseActivity, ServiceCallCancelRequestEvent serviceCallCancelRequestEvent) {
        if (serviceCallCancelRequestEvent instanceof VideoChunkUploadCancelRequestEvent) {
            VideoChunkUploadCancelRequestEvent videoChunkUploadCancelRequestEvent = (VideoChunkUploadCancelRequestEvent) serviceCallCancelRequestEvent;
            showProgressNotification(baseActivity, getNotificationIdFromMediaId(videoChunkUploadCancelRequestEvent.getVideoId()), baseActivity.getString(R.string.notification_video_upload_title), baseActivity.getString(R.string.notification_media_upload_text_being_cancelled), 0, 0, null);
            baseActivity.showToast(baseActivity.getString(R.string.message_video_upload_being_cancelled));
        }
    }

    public void onServiceCallCancelProcessed(BaseActivity baseActivity, ServiceCallCancelEvent serviceCallCancelEvent) {
        if (serviceCallCancelEvent instanceof VideoChunkUploadCancelEvent) {
            VideoChunkUploadCancelEvent videoChunkUploadCancelEvent = (VideoChunkUploadCancelEvent) serviceCallCancelEvent;
            if (videoChunkUploadCancelEvent.isCancelSucceed()) {
                dismissNotification(baseActivity, getNotificationIdFromMediaId(((VideoChunkUploadCancelEvent)serviceCallCancelEvent).getVideoId()));
                baseActivity.showToast(baseActivity.getString(R.string.message_video_upload_cancelled));
            } else {
                baseActivity.showToast(baseActivity.getString(R.string.message_video_upload_cancel_failed));
            }
        }
    }

    private int getNotificationIdFromMediaId(String mediaId) {
        int notificationId = notificationIdMap.get(mediaId);
        return notificationId;
    }

    private void showProgressNotification(BaseActivity baseActivity, int notificationId, String title, String text, int progress, int maxProgress, PendingIntent cancelIntent) {
        NotificationManager notifyManager = (NotificationManager) baseActivity.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(baseActivity);
        //TODO(VID): set not cancelable
        builder.setContentTitle(title)
                .setContentText(text)
                .setOngoing(true)
                .setAutoCancel(false)
                .setSmallIcon(R.drawable.ic_stat_onesignal_default);

        if (cancelIntent != null) {
            //TODO(VID): Add correct icon and text
            builder.addAction(android.R.drawable.ic_menu_delete, baseActivity.getString(android.R.string.cancel), cancelIntent);
        }

        if (maxProgress > 0) {
            builder.setProgress(maxProgress, progress, false);
        } else {
            builder.setProgress(0, 0, true);
        }
        notifyManager.notify(notificationId, builder.build());
    }

    private void dismissNotification(BaseActivity baseActivity, int notificationId) {
        NotificationManager notifyManager = (NotificationManager) baseActivity.getSystemService(Context.NOTIFICATION_SERVICE);
        notifyManager.cancel(notificationId);
    }

}
