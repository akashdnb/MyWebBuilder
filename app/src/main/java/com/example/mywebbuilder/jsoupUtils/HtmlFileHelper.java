package com.example.mywebbuilder.jsoupUtils;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class HtmlFileHelper {

    public static void extractStylesFromHtml(String filePath, String targetHtmlPath, String targetCssPath) {
        try {
            // Load the HTML file using Jsoup
            File input = new File(filePath);
            Document document = Jsoup.parse(input, "UTF-8");

            File htmlFile = new File(targetHtmlPath);
            File cssFile = new File(targetCssPath);
            if(!htmlFile.exists()) htmlFile.exists();
            if(!cssFile.exists()) cssFile.exists();

            // Extract style code from <style> tags
            Elements styleElements = document.getElementsByTag("style");
            StringBuilder styleCodeBuilder = new StringBuilder();
            for (Element styleElement : styleElements) {
                String styleCode = styleElement.html();
                styleCodeBuilder.append(styleCode).append("\n");
            }
            styleElements.remove();

            // Save the extracted styles to the output file
            FileWriter fileWriter = new FileWriter(targetCssPath);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(styleCodeBuilder.toString());
            bufferedWriter.close();

            Element linkElement = document.createElement("link");
            linkElement.attr("rel", "stylesheet");
            linkElement.attr("href", "style.css");

            Element headElement = document.head();
            headElement.appendChild(linkElement);

            fileWriter = new FileWriter(targetHtmlPath);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(document.toString());
            bufferedWriter.close();

        } catch (Exception e) {
            Log.d("bug", e.toString());
            e.printStackTrace();
        }
    }


}
