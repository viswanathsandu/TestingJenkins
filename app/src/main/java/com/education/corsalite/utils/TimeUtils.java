package com.education.corsalite.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by vissu on 1/29/16.
 */
public class TimeUtils {

    public static long currentTimeInMillis() {
        return Calendar.getInstance().getTimeInMillis();
    }

    public static long getMinInMillis(int minutes) {
        return minutes*60*1000;
    }

    public static Date getCurrentDate() {
        return getDate(currentTimeInMillis());
    }

    public static String getSecondsInTimeFormat(long seconds) {
        long hours = TimeUnit.SECONDS.toHours(seconds);
        long minutes = TimeUnit.SECONDS.toMinutes(seconds) - TimeUnit.HOURS.toMinutes(hours);
        seconds = TimeUnit.SECONDS.toSeconds(seconds) - TimeUnit.HOURS.toSeconds(hours) - TimeUnit.MINUTES.toSeconds(minutes);
        return String.format("%02d:%02d:%02d",
                hours,
                minutes,
                seconds);
    }

    public static String getDateTime(long millis){
       String dateTime = "";
        if(millis != 0) {
            SimpleDateFormat sdfmt2 = new SimpleDateFormat("MMM dd, yyyy HH:mm");
            dateTime = sdfmt2.format(getDate(millis));
        }
        return dateTime;

    }

    public static long getMillisFromDate(String date) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            return dateFormat.parse(date).getTime();
        } catch (Exception e) {
            L.error(e.getMessage(), e);
            return currentTimeInMillis();
        }
    }

    public static String getDateTimeString(long millis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return dateFormat.format(getDate(millis));
    }

    public static String getDateString(long millis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd/MM/yyyy", Locale.getDefault());
        return dateFormat.format(getDate(millis));
    }

    public static Date getDate(long millis) {
        if(millis > 0) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(millis);
            return cal.getTime();
        } else {
            return Calendar.getInstance().getTime();
        }
    }
}
