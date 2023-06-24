package com.example.mywebbuilder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mywebbuilder.databinding.ActivityDashboardBinding;
import com.example.mywebbuilder.jsoupUtils.HTMLUtils;
import com.example.mywebbuilder.models.ProjectModel;
import com.example.mywebbuilder.utils.DirectoryUtil;
import com.example.mywebbuilder.utils.KeyGenerator;
import com.example.mywebbuilder.utils.StorageUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DashboardActivity extends AppCompatActivity{
    ActivityDashboardBinding binding;
    List<ProjectModel> projectsList;
    ProjectsAdapter projectsAdapter;
    boolean exitTimer = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        projectsList = new StorageUtil(this).getProjectList();
        if (projectsList == null) projectsList = new ArrayList<>();
        setupActivity();
    }

    protected void setupActivity(){
        setUpRecyclerView();
        binding.fabBtn.setOnClickListener(v -> createDialog());
    }

    @SuppressLint("MissingInflatedId")
    private void createDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.project_name_dialog, null);
        dialogBuilder.setView(dialogView);
        EditText editProjectName = dialogView.findViewById(R.id.project_name_et);

        dialogBuilder.setPositiveButton("Create Project", (dialog, which) -> {
            String projectName = editProjectName.getText().toString();
            createNewProject(projectName);
        });

        dialogBuilder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    private void createNewProject(String projectName) {
        if (projectsList.contains(projectName)) {
            Toast.makeText(this, "project name already exists!!", Toast.LENGTH_SHORT).show();
            return;
        }
        String projectId = KeyGenerator.generateKey();
        String sourcePath = "https://akashnitjsr.000webhostapp.com/wb/index.html";
        String targetPath = DirectoryUtil.rootProjects + "/" + projectName + "/index.html";
        File file = new File(targetPath);

        File destinationDirectory = file.getParentFile();
        if (!destinationDirectory.exists()) {
            destinationDirectory.mkdirs();
        }
        try {
            boolean isFileCreated = file.createNewFile();
            if (!isFileCreated) {
                Toast.makeText(this, "Something went wrong!!", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            try {
                new HTMLUtils(targetPath, DashboardActivity.this)
                        .initiateHtml(sourcePath, projectName);
            } catch (Exception e) {
                Toast.makeText(this, "err " + e, Toast.LENGTH_SHORT).show();
                return;
            }
            handler.post(() -> {
                ProjectModel projectModel = new ProjectModel(projectName, projectId, file.getAbsolutePath());
                projectsList.add(projectModel);
                new StorageUtil(this).setProjectList(projectsList);
                projectsAdapter.notifyItemInserted(projectsList.size());
            });
        });

    }

    private void setUpRecyclerView() {
        projectsAdapter = new ProjectsAdapter(this, projectsList);
        binding.sitesRv.setLayoutManager(new LinearLayoutManager(this));
        binding.sitesRv.setAdapter(projectsAdapter);
    }

    @Override
    public void onBackPressed() {
        if (exitTimer) {
            super.onBackPressed();
        }
        exitTimer = true;
        Toast.makeText(this, "Press back again to exit!", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(() -> exitTimer = false, 2000);
    }
}