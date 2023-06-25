package com.example.mywebbuilder.jsoupUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;

public class ComponentEditor {

    public static void applyEditedCss(HashMap<String, String> editedCss, String elementId, String filePath, String projectPath) {
        try {
            File input = new File(filePath);
            Document doc = Jsoup.parse(input, "UTF-8");

            Element element = doc.getElementById(elementId);
            if (element != null) {
                for (String property : editedCss.keySet()) {
                    String value = editedCss.get(property);
                    if (element.hasAttr("style")) {
                        String existingStyle = element.attr("style");
                        String updatedStyle = mergeStyles(existingStyle, property, value);
                        element.attr("style", updatedStyle);
                    } else {
                        element.attr("style", property + ": " + value);
                    }
                }
            }

            File projectFile = new File(projectPath);
            Document projectDoc = Jsoup.parse(projectFile, "UTF-8");

            File output = new File(filePath);
            FileWriter writer = new FileWriter(output);
            writer.write(doc.outerHtml());
            writer.close();

            Element projectElement = projectDoc.getElementById(elementId);
            if (projectElement != null){
                projectElement.replaceWith(element);
            }

            output = new File(String.valueOf(projectFile));
            writer = new FileWriter(output);
            writer.write(projectDoc.outerHtml());
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void applyEditedHtml(HashMap<String, String> editedHtml, String elementId, String filePath, String projectPath) {
        try {
            File input = new File(filePath);
            Document doc = Jsoup.parse(input, "UTF-8");

            Element element = doc.getElementById(elementId);
            if (element != null) {
                for (String attribute : editedHtml.keySet()) {
                    String value = editedHtml.get(attribute);
                    if (attribute.equals("innerText")) {
                        element.text(value);
                    } else {
                        element.attr(attribute, value);
                    }
                }
            }

            File projectFile = new File(projectPath);
            Document projectDoc = Jsoup.parse(projectFile, "UTF-8");

            File output = new File(filePath);
            FileWriter writer = new FileWriter(output);
            writer.write(doc.outerHtml());
            writer.close();

            Element projectElement = projectDoc.getElementById(elementId);
            if (projectElement != null){
                projectElement.replaceWith(element);
            }

            output = new File(String.valueOf(projectFile));
            writer = new FileWriter(output);
            writer.write(projectDoc.outerHtml());
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String mergeStyles(String existingStyle, String property, String value) {
        // Split the existing style into individual properties
        String[] styleProperties = existingStyle.split(";");

        // Check if the property is already present
        boolean isPropertyPresent = false;
        for (int i = 0; i < styleProperties.length; i++) {
            String styleProperty = styleProperties[i].trim();
            if (styleProperty.startsWith(property + ":")) {
                // Property already exists, overwrite the value
                styleProperties[i] = property + ": " + value;
                isPropertyPresent = true;
                break;
            }
        }

        // If the property is not present, add it to the existing style
        if (!isPropertyPresent) {
            existingStyle += "; " + property + ": " + value;
        }

        // Reconstruct the updated style string
        StringBuilder updatedStyle = new StringBuilder();
        for (String styleProperty : styleProperties) {
            updatedStyle.append(styleProperty).append("; ");
        }

        return updatedStyle.toString().trim();
    }
}
