package com.bitlove.fetlife.util;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Build;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    private static final int DEFAULT_CHUNK_SIZE = (int) (1.1f * 1024f * 1024f);

    public static String[] splitFile(Context context, Uri uri, String name) throws IOException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return splitFileV19(context, uri, name);
        } else {
            return splitFileLegacy(context, uri, name);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static String[] splitFileV19(Context context, Uri uri, String name) throws IOException {
        ContentResolver contentResolver = context.getContentResolver();

        List<String> chunkUris = new ArrayList<>();
        int partCounter = 1;

        int sizeOfFiles = DEFAULT_CHUNK_SIZE;
        byte[] buffer = new byte[sizeOfFiles];

        File outputDir = context.getCacheDir();

        try (BufferedInputStream bis = new BufferedInputStream(
                contentResolver.openInputStream(uri))) {

            int tmp ;
            while ((tmp = bis.read(buffer)) > 0) {
                File newFile = File.createTempFile(name, "" + partCounter, outputDir);
                try (FileOutputStream out = new FileOutputStream(newFile)) {
                    out.write(buffer, 0, tmp);
                }
                chunkUris.add(Uri.fromFile(newFile).toString());
            }
        }
        return chunkUris.toArray(new String[chunkUris.size()]);
    }

    private static String[] splitFileLegacy(Context context, Uri uri, String name) throws IOException {
        ContentResolver contentResolver = context.getContentResolver();

        List<String> chunkUris = new ArrayList<>();
        int partCounter = 1;

        int sizeOfFiles = DEFAULT_CHUNK_SIZE;
        byte[] buffer = new byte[sizeOfFiles];

        File outputDir = context.getCacheDir();
        BufferedInputStream bis = new BufferedInputStream(
                contentResolver.openInputStream(uri));

        try {
            int tmp;
            while ((tmp = bis.read(buffer)) > 0) {
                File newFile = File.createTempFile(name, "" + partCounter, outputDir);
                FileOutputStream out = new FileOutputStream(newFile);
                try {
                    out.write(buffer, 0, tmp);
                } finally {
                    if (out != null) {
                        out.close();
                    }
                }
                chunkUris.add(Uri.fromFile(newFile).toString());
            }
        } finally {
            if (bis != null) {
                bis.close();
            }
        }
        return chunkUris.toArray(new String[chunkUris.size()]);
    }
}
