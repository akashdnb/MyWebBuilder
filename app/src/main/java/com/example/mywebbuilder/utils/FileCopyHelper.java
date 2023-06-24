package com.example.mywebbuilder.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileCopyHelper {
    public static boolean copyFile(Context context, String sourceFilePath, String destinationFilePath) {
        try {
            File sourceFile = new File(sourceFilePath);
            File destinationFile = new File(destinationFilePath);

            // Create the destination directory if it doesn't exist
            File destinationDirectory = destinationFile.getParentFile();
            if (!destinationDirectory.exists()) {
                destinationDirectory.mkdirs();
            }

            // Create InputStream and OutputStream
            InputStream inputStream = new FileInputStream(sourceFile);
            OutputStream outputStream = new FileOutputStream(destinationFile);

            // Buffer size for reading and writing data
            byte[] buffer = new byte[4096];
            int bytesRead;

            // Copy the file
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            // Close the streams
            inputStream.close();
            outputStream.close();

            return true; // File copied successfully
        } catch (Exception e) {
            Log.d("TAG", e.toString());
            Toast.makeText(context, "copy "+e, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return false; // Error occurred while copying the file
        }
    }
}
