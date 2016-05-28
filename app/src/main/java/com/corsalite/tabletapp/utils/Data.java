package com.corsalite.tabletapp.utils;

import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.StyleSpan;

/**
 * Created by vissu on 10/10/15.
 */
public class Data {

    public static int getInt(String str) {
        if (!TextUtils.isEmpty(str)) {
            try {
                int value = Integer.parseInt(str);
                return value;
            } catch (NumberFormatException e) {
                L.error("Number Format exception", e);
            }
        }
        return 0;
    }

    public static int getInt(String str, int defaultValue) {
        if (!TextUtils.isEmpty(str)) {
            try {
                int value = Integer.parseInt(str);
                return value;
            } catch (NumberFormatException e) {
                L.error("Number Format exception", e);
            }
        }
        return defaultValue;
    }

    public static long getLong(String str) {
        if (!TextUtils.isEmpty(str)) {
            try {
                long value = Long.parseLong(str);
                return value;
            } catch (NumberFormatException e) {
                L.error("Number Format exception", e);
            }
        }
        return 0l;
    }

    public static int getDoubleInInt(String str) {
        if (!TextUtils.isEmpty(str)) {
            try {
                double value = Double.parseDouble(str);
                return (int) value;
            } catch (NumberFormatException e) {
                L.error("Number Format exception", e);
            }
        }
        return 0;
    }

    public static double getDoubleWithTwoDecimals(String str) {
        if (!TextUtils.isEmpty(str)) {
            try {
                double value = Double.parseDouble(str);
                return Math.round(value * 100.0) / 100.0;
            } catch (NumberFormatException e) {
                L.error("Number Format exception", e);
            }
        }
        return 0l;
    }

    public static SpannableString getBoldString(String text) {
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), 0, text.length() - 1, 0);
        return spannableString;
    }
}
