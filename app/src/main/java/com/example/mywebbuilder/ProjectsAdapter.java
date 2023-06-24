package com.example.mywebbuilder;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mywebbuilder.editor.EditorActivity;
import com.example.mywebbuilder.models.ProjectModel;
import com.example.mywebbuilder.preview.PreviewActivity;
import com.example.mywebbuilder.utils.DirectoryUtil;
import com.example.mywebbuilder.utils.StorageUtil;

import java.io.File;
import java.util.List;
import java.util.Objects;

public class ProjectsAdapter extends RecyclerView.Adapter<ProjectsAdapter.ViewHolder> {

    Context context;
    List<ProjectModel> projectsList;

    public ProjectsAdapter(Context context, List<ProjectModel> projectsList) {
        this.context = context;
        this.projectsList = projectsList;
    }

    @NonNull
    @Override
    public ProjectsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.project_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectsAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.projectTV.setText(projectsList.get(position).getProjectName());
        String projectID = projectsList.get(position).getProjectID();
        String projectName = projectsList.get(position).getProjectName();
        String projectPath = projectsList.get(position).getFilePath();

        holder.editBtn.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditorActivity.class);
            intent.putExtra("projectName", projectName);
            intent.putExtra("projectID", projectID);
            intent.putExtra("projectPath", projectPath);
            context.startActivity(intent);
        });
        holder.previewBtn.setOnClickListener(v -> {
            Intent intent = new Intent(context, PreviewActivity.class);
            intent.putExtra("projectName", projectName);
            intent.putExtra("projectPath", projectPath);
            context.startActivity(intent);
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete Project !!");
                builder.setMessage("Are you sure you want to delete "+ projectName+"?");
                builder.setPositiveButton("Delete", (dialog, which) -> {
                    deleteProject(position);
                }).setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
                builder.create().show();
                return false;
            }
        });
    }

    private void deleteProject(int position) {
        StorageUtil storageUtil = new StorageUtil(context);
        File file = new File(projectsList.get(position).getFilePath());
        File projectDir = file.getParentFile();

        if(file.delete()){
            assert projectDir != null;
            projectDir.delete();
            projectsList.remove(position);
            storageUtil.setProjectList(projectsList);
            notifyItemRemoved(position);
            Toast.makeText(context, "Project deleted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return projectsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView projectTV;
        Button previewBtn, editBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            projectTV = itemView.findViewById(R.id.project_name_tv);
            previewBtn = itemView.findViewById(R.id.preview_btn);
            editBtn= itemView.findViewById(R.id.edit_btn);
        }
    }
}
