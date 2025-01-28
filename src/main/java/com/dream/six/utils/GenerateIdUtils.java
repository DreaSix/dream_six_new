package com.dream.six.utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class GenerateIdUtils {

    public static String generateId(long count) {
        char prefix = (char) ('A' + ((count - 1) / 99) % 26); // A to Z loop
        long suffix = ((count - 1) % 99) + 1; // 01 to 99 loop
        return String.format("%c%02d", prefix, suffix);
    }
    public static String generateTrackId() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MMdd-HHmmssSSS");
        String format = LocalDateTime.now().format(formatter);
        return format +"-"+ RandomStringUtils.randomAlphanumeric(5);
    }


    public static String generateBatchId() {
        // Get the current timestamp in the format yyyyMMddHHmmss
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return LocalDateTime.now().format(formatter);
    }
}
