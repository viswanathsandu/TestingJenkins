package com.education.corsalite.utils;

import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by vissu on 7/13/16.
 */

public class Gzip {

    public static String compress(String str) throws IOException {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        try {
            byte[] blockcopy = ByteBuffer
                    .allocate(4)
                    .order(java.nio.ByteOrder.LITTLE_ENDIAN)
                    .putInt(str.length())
                    .array();
            ByteArrayOutputStream os = new ByteArrayOutputStream(str.length());
            GZIPOutputStream gos = new GZIPOutputStream(os);
            gos.write(str.getBytes());
            gos.close();
            os.close();
            byte[] compressed = new byte[4 + os.toByteArray().length];
            System.arraycopy(blockcopy, 0, compressed, 0, 4);
            System.arraycopy(os.toByteArray(), 0, compressed, 4,
                    os.toByteArray().length);
            return Base64.encodeToString(compressed, Base64.DEFAULT);
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
        return str;
    }

    public static String decompress(String str) throws IOException {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        try {
            byte[] compressed = Base64.decode(str, Base64.DEFAULT);
            if (compressed.length > 4) {
                GZIPInputStream gzipInputStream = new GZIPInputStream(
                        new ByteArrayInputStream(compressed, 4, compressed.length - 4));
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                for (int value = 0; value != -1; ) {
                    value = gzipInputStream.read();
                    if (value != -1) {
                        baos.write(value);
                    }
                }
                gzipInputStream.close();
                baos.close();
                String sReturn = new String(baos.toByteArray(), "UTF-8");
                return sReturn;
            }
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
        return str;
    }
}
