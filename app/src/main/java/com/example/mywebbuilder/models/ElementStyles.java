package com.example.mywebbuilder.models;

import java.util.ArrayList;

public class ElementStyles {
    public static ArrayList<String> allowedProperties;

    static {
        allowedProperties = new ArrayList<>();
        allowedProperties.add("width");
        allowedProperties.add("height");
        allowedProperties.add("background-color");
        allowedProperties.add("color");
        allowedProperties.add("font-size");
        allowedProperties.add("margin-top");
        allowedProperties.add("margin-bottom");
        allowedProperties.add("margin-left");
        allowedProperties.add("margin-right");
        allowedProperties.add("padding-top");
        allowedProperties.add("padding-bottom");
        allowedProperties.add("padding-left");
        allowedProperties.add("padding-right");
        allowedProperties.add("border-radius");
    }


}
