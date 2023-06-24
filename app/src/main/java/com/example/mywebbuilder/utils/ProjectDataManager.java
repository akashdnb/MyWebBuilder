package com.example.mywebbuilder.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.mywebbuilder.models.ComponentModel;

import java.io.File;
import java.util.concurrent.Executors;

public class ProjectDataManager {
    Context context;
    ComponentModel component;
    String projectID, projectName= "project1", componentID, filePath;

    public ProjectDataManager(Context context) {
        this.context = context;
    }

    public String saveComponent(ComponentModel component, String projectID, String componentID){
        this.component = component;
        this.projectID = projectID;
        this.componentID = componentID;
        filePath = DirectoryUtil.rootProjectsData + "/" + projectName;

        try{
            FileDownloader.downloadFile(component.getHtmlUrl(), new File(filePath), componentID+".html");
            if(!component.getScriptUrl().isEmpty())
                FileDownloader.downloadFile(component.getHtmlUrl(), new File(filePath), componentID+".js");
            if(!component.getStyleUrl().isEmpty())
                FileDownloader.downloadFile(component.getHtmlUrl(), new File(filePath), componentID+".css");
        }catch (Exception e){
            Toast.makeText(context, "manager "+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            Log.d("TAG", e.toString());
        }

        return projectID;
    }
}
