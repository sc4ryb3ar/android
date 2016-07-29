package com.bitlove.fetlife.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;

public class CustomEmojiUtil {

    public static Spanned replaceEmojiTags(Context context, CharSequence text, int height) {
        int index;
        if ((index = TextUtils.indexOf(text, ":berry:cry:")) >= 0) {
            return getHtmlSequence(context, TextUtils.replace(text, new String[]{":berry:cry:"}, new CharSequence[]{"<img src=\"emoji_berry_cry\"/>"}), height);
        } else {
            return null;
        }
    }

    private static Spanned getHtmlSequence(final Context context, final CharSequence text, final int height) {
        return Html.fromHtml(text.toString(), new Html.ImageGetter() {
            @Override
            public Drawable getDrawable(String source) {
                int modifiedHeight = text.length() == "<img src=\"emoji_berry_cry\"/>".length() ? height*10 : height;
                int id = context.getResources().getIdentifier(source, "drawable", context.getPackageName());
                Drawable emoji = context.getResources().getDrawable(id);
                emoji.setBounds(0, 0, modifiedHeight, modifiedHeight);
                return emoji;
            }
        }, null);
    }
}
