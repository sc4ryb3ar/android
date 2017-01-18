package com.bitlove.fetlife.util;

import com.bitlove.fetlife.model.pojos.Message;
import com.crashlytics.android.Crashlytics;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public class MessageDuplicationDebugUtil {

    private static final int BODY_TRASHOLD = 3;

    private static String lastTypedHash, lastSentHash;

    public static void checkTypedMessage(Message message) {
        String body = message.getBody();
        if (body == null || body.trim().length() <= BODY_TRASHOLD) {
            lastTypedHash = null;
            return;
        }
        String hashBase = message.getSenderId() + message.getBody();
        String hash = calculateHash(hashBase);
        if (hash.equals(lastTypedHash)) {
            Crashlytics.logException(new Exception("Typed Message match; body length: " + body.length()));
        }
        lastTypedHash = hash;
    }

    public static void checkSentMessage(Message message) {
        String body = message.getBody();
        if (body == null || body.trim().length() <= BODY_TRASHOLD) {
            lastSentHash = null;
            return;
        }
        String hashBase = message.getSenderId() + message.getBody();
        String hash = calculateHash(hashBase);
        if (hash.equals(lastSentHash)) {
            Crashlytics.logException(new Exception("Sent Message match; body length: " + body.length()));
        }
        lastSentHash = hash;
    }

    private static String calculateHash(String hashBase) {
        try {
            return HashUtil.SHA1(hashBase);
        } catch (UnsupportedEncodingException|NoSuchAlgorithmException e) {
            return "";
        }
    }

}
