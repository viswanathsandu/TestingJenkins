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
            Date date = getDate(millis);
            dateTime = sdfmt2.format(date);
        }
        return dateTime;

    }

    public static String getDateString(long millis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = getDate(millis);
        return dateFormat.format(date);
    }

    public static Date getDate(long millis) {
        if(millis > 0) {
            return new Date(millis);
        } else {
            return new Date();
        }
    }
}
