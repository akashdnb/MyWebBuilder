package com.example.mywebbuilder.editor;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.mywebbuilder.databinding.EditComponentBottomSheetBinding;
import com.example.mywebbuilder.models.ComponentModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class EditComponentBottomSheet extends BottomSheetDialog {
    EditComponentBottomSheetBinding binding;
    ComponentModel editComponent;
    Context context;

    public EditComponentBottomSheet(@NonNull Context context, ComponentModel editComponent) {
        super(context);
        this.context = context;
        this.editComponent = editComponent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = EditComponentBottomSheetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getBehavior().setSkipCollapsed(true);
        getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
        getBehavior().setDraggable(false);

        binding.bsLayout.setMinHeight(Resources.getSystem().getDisplayMetrics().heightPixels);
        binding.backBtn.setOnClickListener(view -> dismiss());
    }
}
