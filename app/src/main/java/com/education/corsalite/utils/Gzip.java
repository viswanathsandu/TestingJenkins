package com.education.corsalite.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by vissu on 7/13/16.
 */

public class Gzip {

    public static String compress(String str) throws IOException {
        if (str == null || str.length() == 0) {
            return str;
        }
        long initTime = TimeUtils.currentTimeInMillis();
        L.debug("Before Compression\t" + str.length() +"\t"+ initTime);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(out);
        gzip.write(str.getBytes("UTF-8"));
        gzip.close();
        String outStr = out.toString("ISO-8859-1");
        L.debug("After Compression\t" + outStr.length() +"\t"+ (TimeUtils.currentTimeInMillis() - initTime));
        return outStr;
    }

    public static String decompress(String str) throws IOException {
        if (str == null || str.length() == 0) {
            return str;
        }
        long initTime = TimeUtils.currentTimeInMillis();
        L.debug("Before Decompression\t" + str.length() +"\t"+ initTime);
        GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(str.getBytes("ISO-8859-1")));
        BufferedReader bf = new BufferedReader(new InputStreamReader(gis, "UTF-8"));
        String outStr = "";
        String line;
        while ((line = bf.readLine()) != null) {
            outStr += line;
        }
        L.debug("After Decompression\t" + outStr.length() +"\t"+ (TimeUtils.currentTimeInMillis() - initTime));
        return outStr;
    }
}
