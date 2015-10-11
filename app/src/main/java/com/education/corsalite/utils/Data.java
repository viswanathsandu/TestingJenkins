package com.education.corsalite.utils;

import android.content.Intent;
import android.text.TextUtils;

/**
 * Created by vissu on 10/10/15.
 */
public class Data {

    public static int getInt(String str) {
        if(!TextUtils.isEmpty(str)) {
            try {
                int value = Integer.parseInt(str);
                return value;
            } catch (NumberFormatException e) {
                L.error("Number Format exception", e);
            }
        }
        return 0;
    }

    public static long getLong(String str) {
        if(!TextUtils.isEmpty(str)) {
            try {
                long value = Long.parseLong(str);
                return value;
            } catch (NumberFormatException e) {
                L.error("Number Format exception", e);
            }
        }
        return 0l;
    }

    public double getdoubleWithTwoDecimals(String str) {
        if(!TextUtils.isEmpty(str)) {
            try {
                double value = Double.parseDouble(str);
                return Math.round(value * 100.0)/100.0;
            } catch (NumberFormatException e) {
                L.error("Number Format exception", e);
            }
        }
        return 0l;
    }
}
