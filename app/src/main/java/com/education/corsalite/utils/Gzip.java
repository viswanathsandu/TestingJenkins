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

    public static byte[] compressBytes(String data) throws IOException {
        byte[] compressed = null;
        try {
            compressed = data.getBytes();
            ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length());
            GZIPOutputStream gzip = new GZIPOutputStream(bos);
            gzip.write(data.getBytes());
            gzip.close();
            compressed = bos.toByteArray();
            bos.close();
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
        return compressed;
    }

    public static String decompressBytes(byte[] compressed) throws IOException {
        StringBuilder sb = new StringBuilder();
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(compressed);
            GZIPInputStream gis = new GZIPInputStream(bis);
            BufferedReader br = new BufferedReader(new InputStreamReader(gis, "UTF-8"));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            gis.close();
            bis.close();
        } catch (Exception e) {
            L.error(e.getMessage(), e);
        }
        return sb.toString();
    }


    public static String compress(String str) throws IOException {
        if (str == null || str.length() == 0) {
            return str;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(out);
        gzip.write(str.getBytes());
        gzip.close();
        String outStr = out.toString("ISO-8859-1");
        return outStr;
    }

    public static String decompress(String str) throws IOException {
        if (str == null || str.length() == 0) {
            return str;
        }
        GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(str.getBytes("ISO-8859-1")));
        BufferedReader bf = new BufferedReader(new InputStreamReader(gis, "ISO-8859-1"));
        String outStr = "";
        String line;
        while ((line = bf.readLine()) != null) {
            outStr += line;
        }
        return outStr;
    }
}
