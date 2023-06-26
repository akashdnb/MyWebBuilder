package com.example.mywebbuilder.utils;

import android.os.Build;
import android.os.Environment;

import java.io.File;

public class DirectoryUtil {

    public static File rootDirMyWebBuilder= rootDir();

    public static File rootAssets= rootAssets();

    public static File rootProjects= rootProjects();

    public static File hiddenRootProjects = HiddenRootProjects();

    public static File rootProjectsData= rootProjectsData();

    public static void createFleFolder(){
        if(!rootDirMyWebBuilder.exists())
            rootDirMyWebBuilder.mkdirs();
        if(!rootProjects.exists())
            rootProjects.mkdirs();
        if(!hiddenRootProjects.exists())
            hiddenRootProjects.mkdirs();
        if(!rootProjectsData.exists())
            rootProjectsData.mkdirs();
    }

    public static boolean createFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) return true;
        File parentDir = file.getParentFile();

        if (parentDir != null && !parentDir.exists()) {
            boolean created = parentDir.mkdirs();
            if (!created) {
                return false;
            }
        }
        try {
            return file.createNewFile();
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);

        if (file.exists()) {
            boolean deleted = file.delete();
            return deleted;
        } else {
            return false;
        }
    }


    public static boolean deleteFolder(String folderPath) {
        File folder = new File(folderPath);
        if (!folder.exists()) {
            return true;
        }
        if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) deleteFolder(file.getAbsolutePath());
                    else file.delete();
                }
            }
        }
        return folder.delete();
    }


    private static File rootDir(){
        File dir;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        {
            dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/" + "MyWebBuilder");
        }
        else
        {
            dir = new File(Environment.getExternalStorageDirectory() + "/" + "MyWebBuilder");
        }
        return dir;
    }

    private static File rootAssets(){
        File dir;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        {
            dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/" + "MyWebBuilder/.assets");
        }
        else
        {
            dir = new File(Environment.getExternalStorageDirectory() + "/" + "MyWebBuilder/.assets");
        }
        return dir;
    }

    private static File rootProjects(){
        File dir;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        {
            dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/" + "MyWebBuilder/projects");
        }
        else
        {
            dir = new File(Environment.getExternalStorageDirectory() + "/" + "MyWebBuilder/projects");
        }
        return dir;
    }

    private static File HiddenRootProjects(){
        File dir;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        {
            dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/" + "MyWebBuilder/.rawProjects");
        }
        else
        {
            dir = new File(Environment.getExternalStorageDirectory() + "/" + "MyWebBuilder/.rawProjects");
        }
        return dir;
    }

    private static File rootProjectsData(){
        File dir;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        {
            dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/" + "MyWebBuilder/.projectsData");
        }
        else
        {
            dir = new File(Environment.getExternalStorageDirectory() + "/" + "MyWebBuilder/.projectsData");
        }
        return dir;
    }
}
