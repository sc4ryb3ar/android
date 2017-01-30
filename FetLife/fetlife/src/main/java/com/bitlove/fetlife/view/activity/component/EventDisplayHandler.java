package com.bitlove.fetlife.view.activity.component;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v7.app.NotificationCompat;
import android.util.SparseArray;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.event.AuthenticationFailedEvent;
import com.bitlove.fetlife.event.PictureUploadFailedEvent;
import com.bitlove.fetlife.event.PictureUploadFinishedEvent;
import com.bitlove.fetlife.event.PictureUploadStartedEvent;
import com.bitlove.fetlife.event.ServiceCallFailedEvent;
import com.bitlove.fetlife.event.ServiceCallFinishedEvent;
import com.bitlove.fetlife.event.ServiceCallStartedEvent;
import com.bitlove.fetlife.event.VideoChunkUploadFailedEvent;
import com.bitlove.fetlife.event.VideoChunkUploadFinishedEvent;
import com.bitlove.fetlife.event.VideoChunkUploadStartedEvent;
import com.bitlove.fetlife.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.view.activity.BaseActivity;

public class EventDisplayHandler {

    private static int PICTURE_UPLOAD_NOTIFICATION_ID = 42;
    private static int VIDEO_UPLOAD_NOTIFICATION_ID = 1042;
    private static SparseArray<String> notificationIdMap = new SparseArray<>();

    public void onAuthenticationFailed(BaseActivity baseActivity, AuthenticationFailedEvent authenticationFailedEvent) {
        baseActivity.showToast(baseActivity.getString(R.string.error_authentication_failed));
    }

    public void onServiceCallFailed(BaseActivity baseActivity, ServiceCallFailedEvent serviceCallFailedEvent) {
        if (serviceCallFailedEvent instanceof PictureUploadFailedEvent
                || FetLifeApiIntentService.ACTION_APICALL_UPLOAD_PICTURE.equals(serviceCallFailedEvent.getServiceCallAction())) {
            baseActivity.showToast(baseActivity.getString(R.string.message_image_upload_failed));
        } else if (serviceCallFailedEvent instanceof VideoChunkUploadFailedEvent
                || FetLifeApiIntentService.ACTION_APICALL_UPLOAD_VIDEO.equals(serviceCallFailedEvent.getServiceCallAction())
                || FetLifeApiIntentService.ACTION_APICALL_UPLOAD_VIDEO_CHUNK.equals(serviceCallFailedEvent.getServiceCallAction())) {
            //TODO(VID): Add correct text
            baseActivity.showToast("Video upload failed");
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
            int notificationIdIdx = notificationIdMap.indexOfValue(((PictureUploadFinishedEvent)serviceCallFinishedEvent).getPictureId());
            int notificationId = notificationIdMap.keyAt(notificationIdIdx);
            dismissNotification(baseActivity, notificationId);
            baseActivity.showToast(baseActivity.getString(R.string.message_image_upload_finished));
        } else if (serviceCallFinishedEvent instanceof VideoChunkUploadFinishedEvent) {
            int notificationIdIdx = notificationIdMap.indexOfValue(((VideoChunkUploadFinishedEvent)serviceCallFinishedEvent).getVideoId());
            int notificationId = notificationIdMap.keyAt(notificationIdIdx);
            dismissNotification(baseActivity, notificationId);
            //TODO(VID): Add correct text
            baseActivity.showToast("Your video has been uploaded");
        }
    }

    public void onServiceCallStarted(BaseActivity baseActivity, ServiceCallStartedEvent serviceCallStartedEvent) {
        if (serviceCallStartedEvent instanceof PictureUploadStartedEvent) {
            notificationIdMap.put(PICTURE_UPLOAD_NOTIFICATION_ID, ((PictureUploadStartedEvent)serviceCallStartedEvent).getPictureId());
            showProgressNotification(baseActivity, PICTURE_UPLOAD_NOTIFICATION_ID++, 0, 0, false);
            baseActivity.showToast(baseActivity.getString(R.string.message_image_upload_started));
        } else if (serviceCallStartedEvent instanceof VideoChunkUploadStartedEvent) {
            VideoChunkUploadStartedEvent videoChunkUploadStartedEvent = (VideoChunkUploadStartedEvent) serviceCallStartedEvent;
            notificationIdMap.put(VIDEO_UPLOAD_NOTIFICATION_ID, videoChunkUploadStartedEvent.getVideoId());
            showProgressNotification(baseActivity, VIDEO_UPLOAD_NOTIFICATION_ID++, videoChunkUploadStartedEvent.getChunk(), videoChunkUploadStartedEvent.getChunkCount(), false);
            baseActivity.showToast(baseActivity.getString(R.string.message_image_upload_started));
        }
    }

    private void showProgressNotification(BaseActivity baseActivity, int notificationId, int progress, int maxProgress, boolean displayControlButtons) {
        NotificationManager notifyManager = (NotificationManager) baseActivity.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(baseActivity);
        //TODO(VID): Add correct text and icons
        //TODO(VID): set not cancelable
        builder.setContentTitle("Picture Download")
                .setContentText("Download in progress")
                .setSmallIcon(R.mipmap.app_icon_kinky);
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
