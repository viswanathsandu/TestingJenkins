package com.education.corsalite.utils;

import java.util.concurrent.TimeUnit;

/**
 * Created by vissu on 1/29/16.
 */
public class TimeUtils {

    public static String getSecondsInTimeFormat(long seconds) {
        long hours = TimeUnit.SECONDS.toHours(seconds);
        long minutes = TimeUnit.SECONDS.toMinutes(seconds) - TimeUnit.HOURS.toMinutes(hours);
        seconds = TimeUnit.SECONDS.toSeconds(seconds) - TimeUnit.HOURS.toSeconds(hours) - TimeUnit.MINUTES.toSeconds(minutes);
        return String.format("%02d:%02d:%02d",
                hours,
                minutes,
                seconds);
    }
}
