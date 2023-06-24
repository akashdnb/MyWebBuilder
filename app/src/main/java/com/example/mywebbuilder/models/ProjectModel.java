package com.example.mywebbuilder.models;

public class ProjectModel {
    String projectName, projectID, filePath;

    public ProjectModel() {
    }

    public ProjectModel(String projectName, String projectID, String filePath) {
        this.projectName = projectName;
        this.projectID = projectID;
        this.filePath = filePath;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectID() {
        return projectID;
    }

    public void setProjectID(String projectID) {
        this.projectID = projectID;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
