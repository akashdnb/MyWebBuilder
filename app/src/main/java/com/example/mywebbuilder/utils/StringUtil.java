package com.example.mywebbuilder.utils;

public class StringUtil {
    public static String convertToCamelCase(String property) {
        StringBuilder camelCase = new StringBuilder();
        boolean capitalizeNext = false;

        for (int i = 0; i < property.length(); i++) {
            char c = property.charAt(i);

            if (c == '-') {
                capitalizeNext = true;
            } else {
                if (capitalizeNext) {
                    camelCase.append(Character.toUpperCase(c));
                    capitalizeNext = false;
                } else {
                    camelCase.append(Character.toLowerCase(c));
                }
            }
        }

        return camelCase.toString();
    }

}
