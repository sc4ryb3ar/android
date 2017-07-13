package com.bitlove.fetlife.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class UrlUtil {

    public static void openUrl(Context context,String link) {
        if (link != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(link));
            context.startActivity(intent);
        }
    }
}
