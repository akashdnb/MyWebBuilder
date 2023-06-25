package com.example.mywebbuilder.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;

import com.example.mywebbuilder.R;
import com.example.mywebbuilder.databinding.DialogColorPickerBinding;

import java.util.Locale;

public class ColorPickerDialog extends Dialog implements View.OnClickListener {

    private int selectedColor;
    private OnColorSelectedListener onColorSelectedListener;
    DialogColorPickerBinding binding;

    public ColorPickerDialog(Context context, int initialColor, OnColorSelectedListener listener) {
        super(context);
        this.selectedColor = initialColor;
        this.onColorSelectedListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = DialogColorPickerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.width = (int) (getScreenWidth() * 0.9);
        getWindow().setAttributes(layoutParams);

        binding.colorPreview.setBackgroundColor(selectedColor);

        binding.redSeekBar.setProgress(Color.red(selectedColor));
        binding.greenSeekBar.setProgress(Color.green(selectedColor));
        binding.blueSeekBar.setProgress(Color.blue(selectedColor));

        binding.redSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        binding.greenSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        binding.blueSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);

        binding.cancelButton.setOnClickListener(this);
        binding.okButton.setOnClickListener(this);

        binding.hexColorEditText.setText(convertToHex(selectedColor));
        binding.hexColorEditText.addTextChangedListener(hexColorTextWatcher);
    }

    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            int red = binding.redSeekBar.getProgress();
            int green = binding.greenSeekBar.getProgress();
            int blue = binding.blueSeekBar.getProgress();
            selectedColor = Color.rgb(red, green, blue);
            binding.colorPreview.setBackgroundColor(selectedColor);
            binding.hexColorEditText.setText(convertToHex(selectedColor));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };

    private TextWatcher hexColorTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            try {
                String hexColor = s.toString().toUpperCase(Locale.ROOT);
                if (!hexColor.isEmpty()) {
                    if (hexColor.startsWith("#")) {
                        // Hex color code
                        int color = Color.parseColor(hexColor);
                        selectedColor = color;
                        binding.colorPreview.setBackgroundColor(color);
                        binding.redSeekBar.setProgress(Color.red(color));
                        binding.greenSeekBar.setProgress(Color.green(color));
                        binding.blueSeekBar.setProgress(Color.blue(color));
                    } else {
                        // Octa color code
                        int red = Integer.parseInt(hexColor.substring(1, 3), 8);
                        int green = Integer.parseInt(hexColor.substring(3, 5), 8);
                        int blue = Integer.parseInt(hexColor.substring(5, 7), 8);
                        int alpha = hexColor.length() > 7 ? Integer.parseInt(hexColor.substring(7), 8) : 255;
                        selectedColor = Color.argb(alpha, red, green, blue);
                        binding.colorPreview.setBackgroundColor(selectedColor);
                        binding.redSeekBar.setProgress(red);
                        binding.greenSeekBar.setProgress(green);
                        binding.blueSeekBar.setProgress(blue);
                    }
                } else {
                    selectedColor = Color.BLACK;
                    binding.colorPreview.setBackgroundColor(selectedColor);
                    binding.redSeekBar.setProgress(Color.red(selectedColor));
                    binding.greenSeekBar.setProgress(Color.green(selectedColor));
                    binding.blueSeekBar.setProgress(Color.blue(selectedColor));
                }
            } catch (IllegalArgumentException e) {
                // Invalid color hex code
            }
        }
    };



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancelButton:
                dismiss();
                break;
            case R.id.okButton:
                if (onColorSelectedListener != null) {
                    onColorSelectedListener.onColorSelected(selectedColor);
                }
                dismiss();
                break;
        }
    }

    public interface OnColorSelectedListener {
        void onColorSelected(int color);
    }

    private String convertToHex(int color) {
        return String.format("#%06X", (0xFFFFFF & color));
    }

    private int getScreenWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
            return displayMetrics.widthPixels;
        }
        return 0;
    }
}
