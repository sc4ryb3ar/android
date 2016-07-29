package com.bitlove.fetlife.widget;

import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.bitlove.fetlife.util.CustomEmojiUtil;

public class EmojiEditText extends TextInputEditText {

    private CharSequence unmodifiedText = "";

    public EmojiEditText(Context context) {
        super(context);
    }

    public EmojiEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EmojiEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    boolean skipNext = false;

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        if (text == null) {
            unmodifiedText = "";
            super.onTextChanged(text, start, lengthBefore, lengthAfter);
            return;
        }
        if (unmodifiedText == null) {
            unmodifiedText = "";
        }
        if (!skipNext) {
            unmodifiedText = TextUtils.concat(unmodifiedText.subSequence(0, start), text.subSequence(start, start + lengthAfter));
        }
        Spanned emojiText = CustomEmojiUtil.replaceEmojiTags(getContext(), text, getLineHeight());
        if (emojiText == null) {
            skipNext = false;
            super.onTextChanged(text, start, lengthBefore, lengthAfter);
        } else {
            skipNext = true;
            setText(emojiText);
        }
    }

    public CharSequence getUnmodifiedText() {
        return unmodifiedText;
    }
}
