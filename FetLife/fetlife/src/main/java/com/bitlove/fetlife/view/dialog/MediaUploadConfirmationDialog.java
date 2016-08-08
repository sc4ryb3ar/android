package com.bitlove.fetlife.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import com.bitlove.fetlife.R;
import com.bitlove.fetlife.model.service.FetLifeApiIntentService;

public class MediaUploadConfirmationDialog extends DialogFragment {

    private static final String ARGUMENT_MEDIA_URI = "ARGUMENT_MEDIA_URI";
    private static final String ARGUMENT_DELETE_AFTER_UPLOAD = "ARGUMENT_DELETE_AFTER_UPLOAD";
    private static final String FRAGMENT_TAG = MediaUploadConfirmationDialog.class.getSimpleName();

    public static MediaUploadConfirmationDialog newInstance(String mediaUri, boolean deleteAfterUpload) {
        MediaUploadConfirmationDialog mediaUploadConfirmationDialog = new MediaUploadConfirmationDialog();
        Bundle args = new Bundle();
        args.putString(ARGUMENT_MEDIA_URI, mediaUri);
        args.putBoolean(ARGUMENT_DELETE_AFTER_UPLOAD, deleteAfterUpload);
        mediaUploadConfirmationDialog.setArguments(args);
        return mediaUploadConfirmationDialog;
    }

    public static void show(Activity activity, String mediaUri, boolean deleteAfterUpload) {
        FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
        Fragment prev = activity.getFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = newInstance(mediaUri, deleteAfterUpload);
        newFragment.show(ft, FRAGMENT_TAG);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity(), R.style.AppTheme_AlertDialog);
        alertDialogBuilder.setTitle(R.string.title_media_picture_upload_confirmation);
        alertDialogBuilder.setInverseBackgroundForced(true);
        alertDialogBuilder.setMessage(R.string.message_media_picture_upload_confirmation);
        alertDialogBuilder.setPositiveButton(R.string.button_media_picture_upload_confirmation, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FetLifeApiIntentService.startApiCall(getActivity(), FetLifeApiIntentService.ACTION_APICALL_UPLOAD_PICTURE, getArguments().getString(ARGUMENT_MEDIA_URI), Boolean.toString(getArguments().getBoolean(ARGUMENT_DELETE_AFTER_UPLOAD)));
            }
        });
        alertDialogBuilder.setNegativeButton(R.string.button_media_picture_upload_cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return alertDialogBuilder.create();
    }
}
