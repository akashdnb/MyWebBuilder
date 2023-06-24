package com.example.mywebbuilder.utils;

import java.util.Random;

public class KeyGenerator {
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int KEY_LENGTH = 10;
    private static final Random random = new Random();

    public static String generateKey() {
        StringBuilder keyBuilder = new StringBuilder(KEY_LENGTH);
        for (int i = 0; i < KEY_LENGTH; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            keyBuilder.append(randomChar);
        }
        return keyBuilder.toString();
    }
}