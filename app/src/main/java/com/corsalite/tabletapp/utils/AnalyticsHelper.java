package com.corsalite.tabletapp.utils;

import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Aastha on 25/10/15.
 */
public class AnalyticsHelper {

    static ArrayList<Integer> graphColors;

    public static ArrayList<String> parseDate(ArrayList<String> dateList,boolean sort){
        ArrayList<String> dateListNew = new ArrayList<>();

        if(sort) {
            Collections.sort(dateList, new Comparator<String>() {
                DateFormat f = new SimpleDateFormat("yyyyMM");

                @Override
                public int compare(String o1, String o2) {
                    try {
                        return f.parse(o1).compareTo(f.parse(o2));
                    } catch (ParseException e) {
                        throw new IllegalArgumentException(e);
                    }
                }
            });
        }

        for(String date :dateList){
            if(date == null){
                dateListNew.add("");
                continue;
            }
            String month = formatDateMonth(Integer.parseInt(date.substring(4, 6)));
            String newDate = month+","+date.substring(0,4);
            dateListNew.add(newDate);
        }
        return dateListNew;
    }

    private static String formatDateMonth(int monthNum){
        switch (monthNum){
            case 1: return "Jan";
            case 2:return "Feb";
            case 3:return "Mar";
            case 4:return "Apr";
            case 5:return "May";
            case 6:return "Jun";
            case 7:return "Jul";
            case 8: return "Aug";
            case 9 :return "Sep";
            case 10:return "Oct";
            case 11:return "Nov";
            case 12:return "Dec";
        }
        return null;

    }

    public static ArrayList<Integer> getColors(){
        if(graphColors == null || graphColors.size() < 0) {
            graphColors = new ArrayList<>();
            for (int c : ColorTemplate.JOYFUL_COLORS)
                graphColors.add(c);

            for (int c : ColorTemplate.COLORFUL_COLORS)
                graphColors.add(c);

            for (int c : ColorTemplate.PASTEL_COLORS)
                graphColors.add(c);
        }

        return graphColors;
    }

    public static String truncateString(String decimal){
        if(decimal !=null) {
            return String.format("%.2f", Float.parseFloat(decimal));
        }
            return null;
    }
}
