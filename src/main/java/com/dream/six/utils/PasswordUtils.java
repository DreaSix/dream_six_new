package com.dream.six.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Random;

public class PasswordUtils {

    private static final Random random = new Random();

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private static final String[] PREFIXES = {"golden", "silver", "bronze", "platinum", "diamond"};

    private PasswordUtils() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static String generateDummyPassword() {
        return PREFIXES[random.nextInt(PREFIXES.length)] + getRandomNumber();
    }

    private static String getRandomNumber() {
        return String.valueOf(1000 + random.nextInt(9000));
    }

    public static String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }

    public static boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

}
