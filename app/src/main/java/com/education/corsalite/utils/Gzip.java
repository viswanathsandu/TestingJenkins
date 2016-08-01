package com.education.corsalite.utils;

import android.text.TextUtils;

import org.apache.commons.codec.binary.Hex;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
            ByteArrayOutputStream os = new ByteArrayOutputStream(str.length());
            GZIPOutputStream gos = new GZIPOutputStream(os);
            gos.write(str.getBytes());
            gos.close();
            byte[] compressed = os.toByteArray();
            os.close();
            return new String(Hex.encodeHex(compressed));
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
        return str;
    }

    public static String decompress(String compressedData) throws IOException {
        if (TextUtils.isEmpty(compressedData)) {
            return compressedData;
        }
        try {
            final int BUFFER_SIZE = 32;
            byte[] compressedBytes = Hex.decodeHex(compressedData.toCharArray());
            ByteArrayInputStream is = new ByteArrayInputStream(compressedBytes);
            GZIPInputStream gis = new GZIPInputStream(is, BUFFER_SIZE);
            StringBuilder string = new StringBuilder();
            byte[] data = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = gis.read(data)) != -1) {
                string.append(new String(data, 0, bytesRead));
            }
            gis.close();
            is.close();
            return string.toString();
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
        return compressedData;
    }
}
