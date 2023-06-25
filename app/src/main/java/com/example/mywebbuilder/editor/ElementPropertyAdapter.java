package com.example.mywebbuilder.editor;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mywebbuilder.R;
import com.example.mywebbuilder.utils.ColorConverter;
import com.example.mywebbuilder.utils.ColorPickerDialog;

import java.util.ArrayList;
import java.util.Arrays;


public class ElementPropertyAdapter extends RecyclerView.Adapter<ElementPropertyAdapter.ViewHolder> {
    Context context;
    ArrayList<Pair<String, String>> styleList;
    ArrayList<String> htmlProps = new ArrayList<>(Arrays.asList("innerText", "src", "href"));


    public ElementPropertyAdapter(Context context, ArrayList<Pair<String, String>> styleList) {
        this.context = context;
        this.styleList = styleList;
    }

    @NonNull
    @Override
    public ElementPropertyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.element_property_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ElementPropertyAdapter.ViewHolder holder, int position) {
        Pair<String, String> data = styleList.get(position);
        holder.propertyTv.setText(data.first);
        holder.valueEt.setText(data.second);

        if(data.first.contains("color")){
            holder.valueEt.setKeyListener(null);
            String color = ColorConverter.convertToHex(data.second);
            holder.valueEt.setText(color);
            holder.valueEt.setOnClickListener(v -> createColorDialog(holder.valueEt, data.first, color));
        }
        else if(!htmlProps.contains(data.first)){
            holder.valueEt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    String size = s.toString();
                    if(isValidSize(size)){
                        holder.valueEt.setTextColor(Color.GREEN);
                        ((EditElementActivity)context).setEditedCssProperties(new Pair<>(data.first, size));
                    }else{
                        holder.valueEt.setTextColor(Color.RED);
                    }
                }
            });
        }
        else {
            holder.valueEt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    ((EditElementActivity)context).setEditedHtmlProperties(new Pair<>(data.first, s.toString()));
                }
            });
        }
    }

    private void createColorDialog(EditText valueEt, String property, String color) {
        ColorPickerDialog colorPickerDialog = new ColorPickerDialog(context, ColorConverter.hexToInt(color), color1 -> {
            valueEt.setText(ColorConverter.intToHex(color1));
            ((EditElementActivity)context).setEditedCssProperties(new Pair<>(property, ColorConverter.intToHex(color1)));
        });
        colorPickerDialog.show();
    }

    public boolean isValidSize(String value) {
        value = value.trim();
        return value.matches("\\d+(\\.\\d+)?(px|em|%|rem|vw|vh|vmin|vmax|cm|mm|in|pt|pc)");
    }

    @Override
    public int getItemCount() {
        return styleList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView propertyTv;
        EditText valueEt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            propertyTv= itemView.findViewById(R.id.property_tv);
            valueEt = itemView.findViewById(R.id.value_et);
        }
    }


}
