package de.shiewk.blockhistory.util;

import org.jetbrains.annotations.Range;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TimeUtil {

    public static String formatTimestamp(long time){
        Date date = new Date(time);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        TimeZone zone = calendar.getTimeZone();
        int second = calendar.get(Calendar.SECOND);
        int minute = calendar.get(Calendar.MINUTE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int year = calendar.get(Calendar.YEAR);

        String day = numberWithSuffix(calendar.get(Calendar.DAY_OF_MONTH));
        String month = monthName(calendar.get(Calendar.MONTH));
        return "%s %s %s, %s:%s:%s %s".formatted(
                month,
                day,
                year,
                hour < 10 ? "0" + hour : hour,
                minute < 10 ? "0" + minute : minute,
                second < 10 ? "0" + second : second,
                zone.getDisplayName(false, TimeZone.SHORT)
        );
    }
    private static String numberWithSuffix(int i){
        return i + numberSuffix(i);
    }

    private static String numberSuffix(int i){
        if ((i % 10) == 1 && i != 11){
            return "st";
        } else if ((i % 10) == 2 && i != 12){
            return "nd";
        } else if ((i % 10) == 3 && i != 13){
            return "rd";
        }
        return "th";
    }

    public static String monthName(@Range(from = 0, to = 11) int m){
        switch (m){
            case 0 -> {
                return "January";
            }
            case 1 -> {
                return "February";
            }
            case 2 -> {
                return "March";
            }
            case 3 -> {
                return "April";
            }
            case 4 -> {
                return "May";
            }
            case 5 -> {
                return "June";
            }
            case 6 -> {
                return "July";
            }
            case 7 -> {
                return "August";
            }
            case 8 -> {
                return "September";
            }
            case 9 -> {
                return "October";
            }
            case 10 -> {
                return "November";
            }
            case 11 -> {
                return "December";
            }
        }
        return "Unknown Month";
    }
}
