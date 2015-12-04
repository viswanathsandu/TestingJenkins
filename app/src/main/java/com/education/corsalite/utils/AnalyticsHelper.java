package com.education.corsalite.utils;

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
            case 1: return "January";
            case 2:return "February";
            case 3:return "March";
            case 4:return "April";
            case 5:return "May";
            case 6:return "June";
            case 7:return "July";
            case 8: return "August";
            case 9 :return "September";
            case 10:return "October";
            case 11:return "November";
            case 12:return "December";
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
