package com.example.mywebbuilder.models;

public class EditorRVModel {
    String id, url;

    public EditorRVModel() {
    }

    public EditorRVModel(String id, String url) {
        this.id = id;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
