package com.example.mywebbuilder.editor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.mywebbuilder.R;
import com.example.mywebbuilder.broadcastReceivers.NetworkChangeListener;
import com.example.mywebbuilder.broadcastReceivers.NetworkChangeReceiver;
import com.example.mywebbuilder.databinding.ActivityEditorBinding;
import com.example.mywebbuilder.jsoupUtils.HTMLUtils;
import com.example.mywebbuilder.models.ComponentModel;
import com.example.mywebbuilder.preview.PreviewActivity;
import com.example.mywebbuilder.utils.DirectoryUtil;
import com.example.mywebbuilder.utils.KeyGenerator;
import com.example.mywebbuilder.utils.StorageUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.sephiroth.android.library.xtooltip.ClosePolicy;
import it.sephiroth.android.library.xtooltip.Tooltip;

public class EditorActivity extends AppCompatActivity implements DrawerListener, NetworkChangeListener {
    ActivityEditorBinding binding;
    private NetworkChangeReceiver networkChangeReceiver;
    public boolean isNetworkAvailable = true;
    public List<ComponentModel> componentModelList, editorList;
    RecyclerView sliderRV;
    EditText searchEt;
    Spinner filterSpinner;
    FirebaseDatabase database;
    DatabaseReference componentsRef;
    DatabaseReference typesRef;
    EditorRVAdapter editorRVAdapter;
    ComponentAdapter componentAdapter;
    public String projectName, projectID, projectPath;
    HTMLUtils htmlUtils;
    public ArrayList<Integer> selectedList;
    private ProgressDialog progressDialog;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        networkChangeReceiver = new NetworkChangeReceiver(this);

        Intent intent = getIntent();
        projectName = intent.getStringExtra("projectName");
        projectID = intent.getStringExtra("projectID");
        projectPath = intent.getStringExtra("projectPath");
        binding.titleTv.setText(projectName);
        setupProgressDialog();

        if (isNetworkAvailable) setupActivity();
    }

    protected void setupActivity() {
        selectedList = new ArrayList<>();
        htmlUtils = new HTMLUtils(projectPath, this);

        editorList = new StorageUtil(this).getHierarchy(projectName);
        if (editorList == null) editorList = new ArrayList<>();

        database = FirebaseDatabase.getInstance();
        componentsRef = database.getReference("components");
        typesRef = database.getReference("componentTypes");
        componentModelList = new ArrayList<>();

        updateComponentList();
        setUpEditorRV();
        addDragListener();
        handleClicks();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void handleClicks() {
        binding.frameLayout.findViewById(R.id.close_btn).setOnClickListener(v -> onBackPressed());
        binding.burgerIcon.setOnClickListener(v -> {
            binding.drawerLayout.openDrawer(Gravity.LEFT);
            setUpToolTip();
        });
        binding.previewBtn.setOnClickListener(v -> {
            Intent intent = new Intent(EditorActivity.this, PreviewActivity.class);
            intent.putExtra("projectPath", projectPath);
            intent.putExtra("projectName", projectName);
            startActivity(intent);
        });
        binding.selectBackBtn.setOnClickListener(v -> {
            binding.selectionBar.setVisibility(View.GONE);
            selectedList.clear();
            editorRVAdapter.isCheckBoxVisible = false;
            editorRVAdapter.isSelectedAll = false;
            editorRVAdapter.notifyDataSetChanged();
            binding.selectedNo.setText("0");
        });
        binding.selectAll.setOnClickListener(v -> {
            selectedList.clear();
            editorRVAdapter.isSelectedAll = !editorRVAdapter.isSelectedAll;
            if (editorRVAdapter.isSelectedAll) {
                for (int i = 0; i < editorList.size(); i++) selectedList.add(i);
            }
            editorRVAdapter.notifyDataSetChanged();
            binding.selectedNo.setText(String.valueOf(editorRVAdapter.getItemCount()));
        });
        binding.deleteSelected.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(EditorActivity.this);
            builder.setTitle("Delete Components !!");
            builder.setPositiveButton("Delete", (dialog, which) -> {
                deleteComponents();
            }).setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            builder.create().show();
        });
        binding.editSelected.setOnClickListener(v -> {
            Intent intent = new Intent(EditorActivity.this, EditElementActivity.class);
            intent.putExtra("projectPath", projectPath);
            intent.putExtra("projectName", projectName);
            intent.putExtra("projectID", projectID);
            intent.putExtra("editPosition", selectedList.get(0));
            try {
                startActivity(intent);
            }catch (Exception e){
                Log.d("TAG", e.toString());
            }

            binding.selectBackBtn.performClick();
        });
        searchEt = binding.frameLayout.findViewById(R.id.search_et);
        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                componentAdapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void updateSpinner() {
        filterSpinner = binding.frameLayout.findViewById(R.id.filter_spinner);
        List<String> typeList = new ArrayList<>(Arrays.asList("No filter", "Nav Bar", "Text Area", "Card", "Image Area", "Button", "Footer"));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, typeList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        filterSpinner.setAdapter(adapter);
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                componentAdapter.filter(typeList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                componentAdapter.filter(typeList.get(0));
            }
        });
    }

    void deleteComponents() {
        Collections.sort(selectedList);
        List<ComponentModel> deleteList = new ArrayList<>();
        for (Integer position : selectedList) {
            deleteList.add(editorList.get(position));
        }
        for (ComponentModel deleteItem : deleteList) {
            try {
                htmlUtils.removeWithId(deleteItem.getElementId());
            } catch (Exception e) {
                Toast.makeText(this, "" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            DirectoryUtil.deleteFile(deleteItem.getPreviewUrl());
            editorList.remove(deleteItem);
        }
        new StorageUtil(this).setHierarchy(projectName, editorList);
        binding.selectBackBtn.performClick();
    }

    private void setUpEditorRV() {
        editorRVAdapter = new EditorRVAdapter(this, editorList);
        editorRVAdapter.setOnDragStartListener(this);
        binding.mainRv.setLayoutManager(new LinearLayoutManager(this));
        binding.mainRv.setAdapter(editorRVAdapter);
    }

    private void setUpComponentsRV() {
        componentAdapter = new ComponentAdapter(this, componentModelList);
        sliderRV = binding.frameLayout.findViewById(R.id.slider_rv);
        sliderRV.setLayoutManager(new LinearLayoutManager(this));
        sliderRV.setAdapter(componentAdapter);
        if (editorList.size() == 0) {
            if (!binding.drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                binding.drawerLayout.openDrawer(Gravity.LEFT);
                setUpToolTip();
                setProgressBarGone();
            }
        }
        updateSpinner();
    }

    private void setupProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Adding Component...");
        progressDialog.setCancelable(false);
    }

    public void setProgressBarGone() {
        binding.progressBar.setVisibility(View.GONE);
    }

    private void updateComponentList() {
        componentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                componentModelList.clear();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    ComponentModel component = childSnapshot.getValue(ComponentModel.class);
                    componentModelList.add(component);
                }
                setUpComponentsRV();
//                setUpEditorRV();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void addComponentToRecyclerView(ComponentModel component, int position) {
        progressDialog.show();
        String componentID = KeyGenerator.generateKey();
        component.setElementId(componentID);
        String root = "root";
        if (position > 0) root = editorList.get(position - 1).getElementId();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        String finalRoot = root;
        String componentPath = DirectoryUtil.rootProjectsData+"/"+projectName+"/"+component.getName()+"-"+componentID;
        executor.execute(() -> {
            try {
                htmlUtils.LinkHTMLFile(component.getHtmlUrl(), finalRoot, componentID, componentPath+".html");
                htmlUtils.linkStyle(component.getStyleUrl(), componentID, componentPath+".css");
                htmlUtils.linkScript(component.getScriptUrl(), componentID, componentPath+".js");
            } catch (Exception e) {
                progressDialog.dismiss();
                Toast.makeText(this, "err " + e, Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            handler.post(() -> {
                component.setPreviewUrl(componentPath+".html");
                editorList.add(position, component);
                editorRVAdapter.notifyItemInserted(position);
                new StorageUtil(this).setHierarchy(projectName, editorList);
                progressDialog.dismiss();
            });
        });


    }

    @SuppressLint("SetTextI18n")
    public void selectItem(int position, int increment) {
        try {
            binding.selectionBar.setVisibility(View.VISIBLE);
            int selected = selectedList.size();
            binding.selectedNo.setText(selected + "");
            if (selected > 1) binding.editSelected.setVisibility(View.GONE);
            else binding.editSelected.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            Toast.makeText(this, "" + e, Toast.LENGTH_SHORT).show();
        }
    }


    private void addComponents() {
        String id = KeyGenerator.generateKey();
        ComponentModel componentModel = new ComponentModel("Image Area 1", id, "Image", "", "", "", "https://firebasestorage.googleapis.com/v0/b/mywebbuilder-b03af.appspot.com/o/header1.png?alt=media&token=36a4ebac-c653-4542-a20d-04555bc6a138", "");
        componentsRef.child("Image Area 1").setValue(componentModel);
        typesRef.child("Image Area").setValue("Image Area");
    }

    private void setUpToolTip() {
        Tooltip tooltip = new Tooltip.Builder(this)
                .anchor(binding.frameLayout, binding.frameLayout.getWidth(), 0, false)
                .text("Long press to drag\n\n" +
                        "Click to preview")
                .typeface(Typeface.DEFAULT)
                .maxWidth(400)
                .closePolicy(ClosePolicy.Companion.getTOUCH_ANYWHERE_NO_CONSUME())
                .arrow(true)
                .showDuration(1500)
                .fadeDuration(200)
                .overlay(true)
                .create();
        tooltip.show(binding.frameLayout, Tooltip.Gravity.RIGHT, true);
    }


    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            binding.drawerLayout.closeDrawer(Gravity.LEFT);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onDragsStarted() {
        if (binding.drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            binding.drawerLayout.closeDrawer(Gravity.LEFT);
        }
    }

    private void addDragListener() {
        binding.navLayout.setOnDragListener(new View.OnDragListener() {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) binding.mainRv.getLayoutParams();

            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        onDragsStarted();
                        return true;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        binding.addLine.setVisibility(View.VISIBLE);
                        layoutParams.topMargin = getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._68sdp);
                        binding.mainRv.setLayoutParams(layoutParams);
                        return true;
                    case DragEvent.ACTION_DRAG_EXITED:
                        binding.addLine.setVisibility(View.GONE);
                        layoutParams.topMargin = getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._48sdp);
                        binding.mainRv.setLayoutParams(layoutParams);
                        return true;
                    case DragEvent.ACTION_DROP:
                        binding.addLine.setVisibility(View.GONE);
                        layoutParams.topMargin = getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._48sdp);
                        binding.mainRv.setLayoutParams(layoutParams);
                        ClipData.Item item = event.getClipData().getItemAt(0);
                        String jsonString = item.getText().toString();
                        ComponentModel component = new Gson().fromJson(jsonString, ComponentModel.class);
                        addComponentToRecyclerView(component, 0);
                        return true;
                }
                return false;
            }
        });
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
        binding.mainRv.setVisibility(View.VISIBLE);
        binding.noConnLottie.setVisibility(View.GONE);
    }

    @Override
    public void onNetworkDisconnected() {
        isNetworkAvailable = false;
//        binding.mainRv.setVisibility(View.GONE);
//        binding.noConnLottie.setVisibility(View.VISIBLE);
    }
}