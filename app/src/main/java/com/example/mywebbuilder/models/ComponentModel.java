package com.example.mywebbuilder.models;

import java.io.Serializable;

public class ComponentModel implements Serializable {
    String name, elementId, type, htmlUrl, styleUrl, scriptUrl, imageUrl, previewUrl;

    public ComponentModel() {
    }

    public ComponentModel(String name, String elementId, String type, String htmlUrl, String styleUrl, String scriptUrl, String imageUrl, String previewUrl) {
        this.name = name;
        this.elementId = elementId;
        this.type = type;
        this.htmlUrl = htmlUrl;
        this.styleUrl = styleUrl;
        this.scriptUrl = scriptUrl;
        this.imageUrl = imageUrl;
        this.previewUrl = previewUrl;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    public String getStyleUrl() {
        return styleUrl;
    }

    public void setStyleUrl(String styleUrl) {
        this.styleUrl = styleUrl;
    }

    public String getScriptUrl() {
        return scriptUrl;
    }

    public void setScriptUrl(String scriptUrl) {
        this.scriptUrl = scriptUrl;
    }
}
