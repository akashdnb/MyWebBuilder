package com.example.mywebbuilder.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.mywebbuilder.models.ComponentModel;
import com.example.mywebbuilder.models.ProjectModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class StorageUtil {
    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;
    private final Context context;
    private final String PROJECTS_KEY = "frsfgtrf33juhygtrkjhg456fr45u9";

    public StorageUtil(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        editor = prefs.edit();
    }


    public void setHierarchy(String name, List<ComponentModel> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(name, json);
        editor.apply();
    }

    public ArrayList<ComponentModel> getHierarchy(String name) {
        Gson gson = new Gson();
        String json = prefs.getString(name, "");
        Type type = new TypeToken<ArrayList<ComponentModel>>() {}.getType();
        ArrayList<ComponentModel> list = gson.fromJson(json, type);
        return list;
    }

    public void deleteProject(String projectName){
        editor.remove(projectName);
    }

    public void setProjectID(String projectName, String projectID){
        editor.putString(projectName, projectID);
        editor.apply();
    }

    public String getProjectID(String projectName){
        String projectID = prefs.getString(projectName, "null");
        return  projectID;
    }

    public void setProjectList(List<ProjectModel> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(PROJECTS_KEY, json);
        editor.apply();
    }

    public ArrayList<ProjectModel> getProjectList() {
        Gson gson = new Gson();
        String json = prefs.getString(PROJECTS_KEY, "");
        Type type = new TypeToken<ArrayList<ProjectModel>>() {}.getType();
        ArrayList<ProjectModel> list = gson.fromJson(json, type);
        return list;
    }


}
