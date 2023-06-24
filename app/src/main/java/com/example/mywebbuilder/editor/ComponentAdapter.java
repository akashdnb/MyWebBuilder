package com.example.mywebbuilder.editor;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mywebbuilder.R;
import com.example.mywebbuilder.models.ComponentModel;
import com.example.mywebbuilder.preview.PreviewActivity;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ComponentAdapter extends RecyclerView.Adapter<ComponentAdapter.ViewHolder> {
    Context context;
    List<ComponentModel> componentsList, originalList;
    DrawerListener drawerListener;

    public ComponentAdapter(Context context, List<ComponentModel> originalListList) {
        this.context = context;
        this.originalList = originalListList;
        componentsList = new ArrayList<>(originalListList);

    }

    @NonNull
    @Override
    public ComponentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.component_rv_item, parent, false);
        return new ComponentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComponentAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.componentsTV.setText(componentsList.get(position).getName());
        setUpDrag(holder.itemView, position);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PreviewActivity.class);
            intent.putExtra("projectPath", componentsList.get(position).getPreviewUrl());
            intent.putExtra("projectName", componentsList.get(position).getName());
            intent.putExtra("httpUrl", true);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return componentsList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filter(String query) {
        componentsList.clear();

        if(query.equalsIgnoreCase("No filter")) {
            componentsList.addAll(originalList);
            notifyDataSetChanged();
            return;
        }

        if (query.trim().isEmpty()) {
            componentsList.addAll(originalList);
        } else {
            query = query.toLowerCase();

            for (ComponentModel item : originalList) {
                if (item.getName().toLowerCase().contains(query)
                        || item.getType().toLowerCase(Locale.ROOT).contains(query)) {
                    componentsList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void setOnDragStartListener(DrawerListener listener) {
        this.drawerListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView componentsTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            componentsTV = itemView.findViewById(R.id.components_tv);
        }
    }

    private void setUpDrag(View itemView, int position) {
        itemView.setOnLongClickListener(v -> {
            if(!((EditorActivity)context).isNetworkAvailable) {
                return false;
            }
            if (drawerListener != null) drawerListener.onDragsStarted();
            String jsonString = new Gson().toJson(componentsList.get(position));
            ClipData.Item item = new ClipData.Item(jsonString);
            ClipData dragData = new ClipData("DragData", new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, item);

            View.DragShadowBuilder dragShadowBuilder = new View.DragShadowBuilder(v);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                v.startDragAndDrop(dragData, dragShadowBuilder, v, 0);
            }

            return true;
        });
    }

}
