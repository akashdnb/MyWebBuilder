package com.example.mywebbuilder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mywebbuilder.broadcastReceivers.NetworkChangeListener;
import com.example.mywebbuilder.broadcastReceivers.NetworkChangeReceiver;
import com.example.mywebbuilder.databinding.ActivityDashboardBinding;
import com.example.mywebbuilder.jsoupUtils.HTMLUtils;
import com.example.mywebbuilder.models.ProjectModel;
import com.example.mywebbuilder.utils.DirectoryUtil;
import com.example.mywebbuilder.utils.KeyGenerator;
import com.example.mywebbuilder.utils.StorageUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DashboardActivity extends AppCompatActivity implements NetworkChangeListener {
    ActivityDashboardBinding binding;
    private NetworkChangeReceiver networkChangeReceiver;
    List<ProjectModel> projectsList;
    ProjectsAdapter projectsAdapter;
    ProgressDialog progressDialog;
    boolean exitTimer = false;
    private boolean isNetworkAvailable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        networkChangeReceiver = new NetworkChangeReceiver(this);
        projectsList = new StorageUtil(this).getProjectList();
        if (projectsList == null) projectsList = new ArrayList<>();
        setupActivity();
    }

    protected void setupActivity() {
        setUpRecyclerView();
        binding.fabBtn.setOnClickListener(v -> createDialog());
        setupProgressDialog();
    }

    @SuppressLint("MissingInflatedId")
    private void createDialog() {
        if(!isNetworkAvailable){
            Toast.makeText(this, "No Internet!!", Toast.LENGTH_SHORT).show();
            return;
        }
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
        String sourcePath = "https://akashjsr63.github.io/wb/index.html";
        String targetPath = DirectoryUtil.hiddenRootProjects + "/" + projectName + "/index.html";

        if (!DirectoryUtil.createFile(targetPath)) {
            return;
        }
        progressDialog.show();
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
                ProjectModel projectModel = new ProjectModel(projectName, projectId, targetPath);
                projectsList.add(projectModel);
                new StorageUtil(this).setProjectList(projectsList);
                projectsAdapter.notifyItemInserted(projectsList.size());
                progressDialog.dismiss();
            });
        });
    }

    private void setUpRecyclerView() {
        projectsAdapter = new ProjectsAdapter(this, projectsList);
        binding.sitesRv.setLayoutManager(new LinearLayoutManager(this));
        binding.sitesRv.setAdapter(projectsAdapter);
    }

    private void setupProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creating Project...");
        progressDialog.setCancelable(false);
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

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(networkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkChangeReceiver);
    }

    @Override
    public void onNetworkConnected() {
        if (!isNetworkAvailable) {
            setupActivity();
            Toast.makeText(this, "Back Online!", Toast.LENGTH_SHORT).show();
        }
        isNetworkAvailable = true;
    }

    @Override
    public void onNetworkDisconnected() {
        isNetworkAvailable = false;
        Toast.makeText(this, "No Internet!", Toast.LENGTH_SHORT).show();
    }
}