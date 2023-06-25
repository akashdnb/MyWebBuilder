package com.example.mywebbuilder.jsoupUtils;

import android.content.Context;

import com.example.mywebbuilder.utils.DirectoryUtil;
import com.example.mywebbuilder.utils.KeyGenerator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

import java.io.File;
import java.io.FileWriter;
import java.util.Objects;

public class HTMLUtils {
    String rootPath;
    Context context;

    public HTMLUtils(String filePath, Context context) {
        this.rootPath = filePath;
        this.context = context;
    }

    public void initiateHtml(String sourceUrl, String projectName) throws Exception {
        Document source = Jsoup.connect(sourceUrl).get();

        Element titleElement = source.selectFirst("title");

        if (titleElement == null) return;
        titleElement.text(projectName);

        Element body = source.getElementById("root");
        if (body == null) return;
        body.text("");

        File output = new File(rootPath);
        FileWriter writer = new FileWriter(output);
        writer.write(source.outerHtml());
        writer.close();
    }

    public void LinkHTMLFile(String fileUrl, String rootID, String newID, String componentPath) throws Exception {
        File file = new File(rootPath);
        Document root = Jsoup.parse(file, "UTF-8", "");

        Element div = root.getElementById(rootID);
        if (div == null) return;

        Document element = Jsoup.connect(fileUrl).get();
        Element outermostElement = getOutermostElement(element);
        if (outermostElement != null) {
            outermostElement.attr("id", newID);
            for (Element child : outermostElement.children()) {
                assignIdsToElements(child);
            }
        }

        DirectoryUtil.createFile(componentPath);
        File componentOutput = new File(componentPath);
        FileWriter writer = new FileWriter(componentOutput);
        writer.write(element.outerHtml());
        writer.close();

        if (rootID.equalsIgnoreCase("root")) {
            Element firstChild = div.children().first();
            if (firstChild == null) {
                div.appendChild(Objects.requireNonNull(element.body().children().first()));
            } else {
                firstChild.before(Objects.requireNonNull(element.body().children().first()));
            }
        } else {
            div.after(Objects.requireNonNull(element.body().children().first()));
        }


        File output = new File(rootPath);
        writer = new FileWriter(output);
        writer.write(root.outerHtml());
        writer.close();

    }

    public void addHTMLFile(String filePath, String rootID, String newID) throws Exception {
        File file = new File(rootPath);
        Document root = Jsoup.parse(file, "UTF-8", "");

        File elementFile = new File(filePath);
        Document element = Jsoup.parse(elementFile, "UTF-8");
        Element outermostElement = getOutermostElement(element);
        if (outermostElement != null) {
            outermostElement.attr("id", newID);
        }

        Element div = root.getElementById(rootID);

        if (div == null) return;
        if (rootID.equalsIgnoreCase("root")) {
            div.prependChild(element);
        } else {
            div.after(element.html());
        }

        File output = new File(rootPath);
        FileWriter writer = new FileWriter(output);
        writer.write(root.outerHtml());
        writer.close();

    }


    public void linkStyle(String stylePath, String id, String componentPath) throws Exception {
        if (stylePath.isEmpty()) return;
        File file = new File(rootPath);
        Document doc = Jsoup.parse(file, "UTF-8", "");

        Element cssLink = new Element(Tag.valueOf("link"), "").attr("rel", "stylesheet").attr("href", stylePath).attr("id", id + "_css");

        Element div = doc.getElementById(id);
        if (div == null) return;
        div.before(cssLink);

        File output = new File(rootPath);
        FileWriter writer = new FileWriter(output);
        writer.write(doc.outerHtml());
        writer.close();

    }

    public void linkScript(String scriptPath, String id, String componentPath) throws Exception {
        if (scriptPath.isEmpty()) return;
        File file = new File(rootPath);
        Document doc = Jsoup.parse(file, "UTF-8", "");

        Element script = doc.createElement("script");
        script.attr("src", scriptPath).attr("id", id + "_script");

        Element div = doc.getElementById(id);
        if (div == null) return;

        div.after(script);

        File output = new File(rootPath);
        FileWriter writer = new FileWriter(output);
        writer.write(doc.outerHtml());
        writer.close();

    }

    public void removeWithId(String id) throws Exception {
        File input = new File(rootPath);
        Document doc = Jsoup.parse(input, "UTF-8");

        Element removeElm = doc.getElementById(id);
        Element removeCss = doc.getElementById(id + "_css");
        Element removeScript = doc.getElementById(id + "_script");

        if (removeElm != null) {
            removeElm.remove();
        }
        if (removeCss != null) {
            removeCss.remove();
        }
        if (removeScript != null) {
            removeScript.remove();
        }

        File output = new File(rootPath);
        FileWriter writer = new FileWriter(output);
        writer.write(doc.outerHtml());
        writer.close();
    }

    private static Element getOutermostElement(Document doc) {
        Element outermostElement = null;
        Element bodyElement = doc.body();

        if (bodyElement.children().size() == 1) {
            outermostElement = bodyElement.child(0);
        }

        return outermostElement;
    }


    public static void assignIdsToElements(Element element) {
        element.attr("id", KeyGenerator.generateKey());

        for (Element child : element.children()) {
            assignIdsToElements(child);
        }
    }
}
