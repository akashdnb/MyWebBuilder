package com.example.mywebbuilder.jsoupUtils;

import com.example.mywebbuilder.utils.KeyGenerator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;

public class ComponentEditor {

    public static void applyEditedCss(HashMap<String, String> editedCss, String elementId, String filePath, String projectPath) {
        if (editedCss.isEmpty()) return;
        try {
            File input = new File(filePath);
            Document doc = Jsoup.parse(input, "UTF-8");

            Element element = doc.getElementById(elementId);
            if (element != null) {
                String existingStyle = element.attr("style");
                StringBuilder updatedStyleBuilder = new StringBuilder(existingStyle);

                for (String property : editedCss.keySet()) {
                    String value = editedCss.get(property);
                    String cssProperty = property.trim() + ": " + value.trim();

                    if (existingStyle.contains(property + ":")) {
                        updatedStyleBuilder = new StringBuilder(updatedStyleBuilder.toString().replaceAll(property + ":.*?;", cssProperty + ";"));
                    } else {
                        updatedStyleBuilder.append(cssProperty).append("; ");
                    }
                }

                String updatedStyle = updatedStyleBuilder.toString().trim();
                element.attr("style", updatedStyle);
            }

            File projectFile = new File(projectPath);
            Document projectDoc = Jsoup.parse(projectFile, "UTF-8");

            File output = new File(filePath);
            FileWriter writer = new FileWriter(output);
            writer.write(doc.outerHtml());
            writer.close();

            Element projectElement = projectDoc.getElementById(elementId);
            if (projectElement != null) {
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
        if (editedHtml.isEmpty()) return;
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
            if (projectElement != null) {
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

    public static void applyEditedCss(HashMap<String, HashMap<String, String>> editedCss, String filePath, String projectPath) {
        if (editedCss.isEmpty()) {
            return;
        }
        try {
            File input = new File(filePath);
            Document doc = Jsoup.parse(input, "UTF-8");

            File projectFile = new File(projectPath);
            Document projectDoc = Jsoup.parse(projectFile, "UTF-8");

            for (String elementId : editedCss.keySet()) {
                HashMap<String, String> cssProperties = editedCss.get(elementId);

                Element element = doc.getElementById(elementId);
                if (element != null) {
                    String existingStyle = element.attr("style");
                    StringBuilder updatedStyleBuilder = new StringBuilder(existingStyle);

                    for (String property : cssProperties.keySet()) {
                        String value = cssProperties.get(property);
                        String cssProperty = property.trim() + ": " + value.trim();

                        if (existingStyle.contains(property + ":")) {
                            updatedStyleBuilder = new StringBuilder(updatedStyleBuilder.toString().replaceAll(property + ":.*?;", cssProperty + ";"));
                        } else {
                            updatedStyleBuilder.append(cssProperty).append("; ");
                        }
                    }

                    String updatedStyle = updatedStyleBuilder.toString().trim();
                    element.attr("style", updatedStyle);
                }

                Element projectElement = projectDoc.getElementById(elementId);
                if (projectElement != null) {
                    String existingStyle = projectElement.attr("style");
                    StringBuilder updatedStyleBuilder = new StringBuilder(existingStyle);

                    for (String property : cssProperties.keySet()) {
                        String value = cssProperties.get(property);
                        String cssProperty = property.trim() + ": " + value.trim();

                        if (existingStyle.contains(property + ":")) {
                            updatedStyleBuilder = new StringBuilder(updatedStyleBuilder.toString().replaceAll(property + ":.*?;", cssProperty + ";"));
                        } else {
                            updatedStyleBuilder.append(cssProperty).append("; ");
                        }
                    }

                    String updatedStyle = updatedStyleBuilder.toString().trim();
                    projectElement.attr("style", updatedStyle);
                }
            }

            FileWriter writer = new FileWriter(filePath);
            writer.write(doc.outerHtml());
            writer.close();

            writer = new FileWriter(projectPath);
            writer.write(projectDoc.outerHtml());
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void applyEditedHtml(HashMap<String, HashMap<String, String>> editedCss, String filePath, String projectPath) {
        if (editedCss.isEmpty()) {
            return;
        }
        try {
            File input = new File(filePath);
            Document doc = Jsoup.parse(input, "UTF-8");

            File projectFile = new File(projectPath);
            Document projectDoc = Jsoup.parse(projectFile, "UTF-8");

            for (String elementId : editedCss.keySet()) {
                HashMap<String, String> cssProperties = editedCss.get(elementId);

                Element element = doc.getElementById(elementId);
                if (element != null && cssProperties != null) {
                    for (String attribute : cssProperties.keySet()) {
                        String value = cssProperties.get(attribute);
                        if (attribute.equals("innerText") && value != null) {
                            element.text(value);
                        } else if (value != null) {
                            element.attr(attribute, value);
                        }
                    }
                }

                Element projectElement = projectDoc.getElementById(elementId);
                if (projectElement != null && cssProperties != null) {
                    for (String attribute : cssProperties.keySet()) {
                        String value = cssProperties.get(attribute);
                        if (attribute.equals("innerText") && value != null) {
                            projectElement.text(value);
                        } else if (value != null) {
                            projectElement.attr(attribute, value);
                        }
                    }
                }
            }

            FileWriter writer = new FileWriter(filePath);
            writer.write(doc.outerHtml());
            writer.close();

            writer = new FileWriter(projectPath);
            writer.write(projectDoc.outerHtml());
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void duplicateElement(String elementId, String filePath, String projectFilePath) {
        try {
            File file = new File(filePath);
            Document doc = Jsoup.parse(file, "UTF-8");

            File projectFile = new File(projectFilePath);
            Document projectDoc = Jsoup.parse(projectFile, "UTF-8");

            Element originalElement = doc.getElementById(elementId);
            if (originalElement != null) {
                Element duplicateElement = originalElement.clone();
                String duplicateId = KeyGenerator.generateKey();
                duplicateElement.attr("id", duplicateId);

                generateUniqueIdsForChildElements(duplicateElement);
                Element projectDuplicateElement = duplicateElement.clone();

                originalElement.after(duplicateElement);

                FileWriter writer = new FileWriter(file);
                writer.write(doc.outerHtml());
                writer.close();

                Element projectOriginalElement = projectDoc.getElementById(elementId);
                if (projectOriginalElement != null) {
                    projectOriginalElement.after(projectDuplicateElement);
                }

                FileWriter projectWriter = new FileWriter(projectFile);
                projectWriter.write(projectDoc.outerHtml());
                projectWriter.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void generateUniqueIdsForChildElements(Element element) {
        Elements children = element.children();
        for (Element child : children) {
            String uniqueId = KeyGenerator.generateKey();
            child.attr("id", uniqueId);
            generateUniqueIdsForChildElements(child); // Recursively generate unique IDs for child elements
        }
    }

    public static void deleteElement(String elementId, String filePath, String projectFilePath) {
        try {
            File file = new File(filePath);
            Document doc = Jsoup.parse(file, "UTF-8");

            File projectFile = new File(projectFilePath);
            Document projectDoc = Jsoup.parse(projectFile, "UTF-8");

            Element element = doc.getElementById(elementId);
            if (element != null) {
                element.remove();

                FileWriter writer = new FileWriter(file);
                writer.write(doc.outerHtml());
                writer.close();

                Element projectElement = projectDoc.getElementById(elementId);
                if (projectElement != null) {
                    projectElement.remove();

                    FileWriter projectWriter = new FileWriter(projectFile);
                    projectWriter.write(projectDoc.outerHtml());
                    projectWriter.close();
                }
            }
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

        StringBuilder updatedStyle = new StringBuilder();
        for (int i = 0; i < styleProperties.length; i++) {
            updatedStyle.append(styleProperties[i].trim());
            if (i < styleProperties.length - 1) {
                updatedStyle.append("; ");
            }
        }

        return updatedStyle.toString().trim();
    }
}
