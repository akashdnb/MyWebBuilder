package com.example.mywebbuilder.utils;

import android.graphics.Color;

public class ColorConverter {

    public static String convertToHex(String colorString) {
        if (colorString.startsWith("rgba(")) {
            return convertRgbaToHex(colorString);
        } else if (colorString.startsWith("rgb(")) {
            return convertRgbToHex(colorString);
        } else {
            return "";
        }
    }

    public static int hexToInt(String hexColor){
        int colorInt = Color.BLACK;
        try {
            colorInt = Color.parseColor(hexColor);
        }catch (Exception e){
            return  colorInt;
        }
        return colorInt;
    }

    public static String intToHex(int color) {
        return String.format("#%06X", (0xFFFFFF & color));
    }


    private static String convertRgbaToHex(String rgbaColor) {
        String[] values = rgbaColor
                .replace("rgba(", "")
                .replace(")", "")
                .split(",");

        if (values.length == 4) {
            int red = Integer.parseInt(values[0].trim());
            int green = Integer.parseInt(values[1].trim());
            int blue = Integer.parseInt(values[2].trim());
            int alpha = Integer.parseInt(values[3].trim());

            String hexColor = String.format("#%02X%02X%02X%02X", red, green, blue, alpha);
            return hexColor;
        } else {
            throw new IllegalArgumentException("Invalid RGBA color format");
        }
    }

    private static String convertRgbToHex(String rgbColor) {
        String[] values = rgbColor
                .replace("rgb(", "")
                .replace(")", "")
                .split(",");

        if (values.length == 3) {
            int red = Integer.parseInt(values[0].trim());
            int green = Integer.parseInt(values[1].trim());
            int blue = Integer.parseInt(values[2].trim());

            String hexColor = String.format("#%02X%02X%02X", red, green, blue);
            return hexColor;
        } else {
            throw new IllegalArgumentException("Invalid RGB color format");
        }
    }
}
