package com.dream.six.utils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CommonDateUtils {

    private CommonDateUtils() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static String getCurrentYear() {
        LocalDateTime now = LocalDateTime.now();
        return now.format(DateTimeFormatter.ofPattern("yyyy"));
    }

    public static String getCurrentMonth() {
        LocalDateTime now = LocalDateTime.now();
        return now.format(DateTimeFormatter.ofPattern("MMMM"));
    }

    public static String getCurrentDate() {
        LocalDateTime now = LocalDateTime.now();
        return now.format(DateTimeFormatter.ofPattern("dd"));
    }
    public static String getCurrentHour() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH"));
    }

    public static String getCurrentMinute() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("mm"));
    }

    public static String getCurrentSecond() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("ss"));
    }


    public static String getTimestampString(Timestamp timestamp) {
        LocalDateTime dateTime = timestamp.toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");//time format "2020-01-01 00:00:00"
        return dateTime.format(formatter);
    }


}
