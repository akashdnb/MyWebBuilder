package com.example.mywebbuilder.editor;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mywebbuilder.databinding.ActivityEditElementBinding;
import com.example.mywebbuilder.models.ComponentModel;
import com.example.mywebbuilder.utils.StorageUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditElementActivity extends AppCompatActivity {
    ActivityEditElementBinding binding;
    public String projectName, projectID, projectPath;
    ComponentModel editComponent;
    List<ComponentModel> editorList;
    int editPosition = 0;
    private List<Element> sectionElements;
    private Map<String, List<String>> elementCssProperties;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditElementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        projectName = intent.getStringExtra("projectName");
        projectID = intent.getStringExtra("projectID");
        projectPath = intent.getStringExtra("projectPath");
        editPosition = intent.getIntExtra("editPosition", 0);
        editorList = new StorageUtil(this).getHierarchy(projectName);
        editComponent = editorList.get(editPosition);
        binding.titleTv.setText("Edit " + editComponent.getName());

        setupActivity();
        binding.saveBtn.setOnClickListener(v -> applyCssToElements());
    }

    private void setupActivity() {
        elementCssProperties = new HashMap<>();
        Log.d("tag", "yes");

        // Define allowed values for 'color' property
        List<String> colorValues = new ArrayList<>();
        colorValues.add("red");
        colorValues.add("blue");
        colorValues.add("green");
        elementCssProperties.put("color", colorValues);

        // Define allowed values for 'font-size' property
        List<String> fontSizeValues = new ArrayList<>();
        fontSizeValues.add("12px");
        fontSizeValues.add("16px");
        fontSizeValues.add("20px");
        elementCssProperties.put("font-size", fontSizeValues);

        List<String> p = new ArrayList<>();
        p.add("color");
        p.add("font-size");
        elementCssProperties.put("p", p);

//        Log.d("tag", String.valueOf(elementCssProperties));

        String websiteUrl = editComponent.getHtmlUrl();
        String desiredSectionId = editComponent.getElementId();
        try {
            File file = new File(projectPath);
            Document doc = Jsoup.parse(file, "UTF-8", "");
//            Document doc = Jsoup.connect(websiteUrl).get();
            Element section = doc.getElementById(desiredSectionId);
            assert section != null;
            sectionElements = section.getAllElements();

//            Log.d("tag", String.valueOf(section));
            Log.d("tag", String.valueOf(sectionElements.size()));

            // Generate form elements for each extracted element
            for (int i = 0; i < sectionElements.size(); i++) {
                Element element = sectionElements.get(i);
                String tagName = element.tagName();
                List<String> cssProperties = elementCssProperties.get(tagName.trim());
//                Log.d("tag", String.valueOf(elementCssProperties));

                if (cssProperties != null) {
//                    Log.d("tag", tagName + " "+cssProperties);
//                    TextView textView = new TextView(this);
//                    textView.setText(tagName);
//                    binding.formContainer.addView(textView);

                    for (String cssProperty : cssProperties) {
//                        Log.d("tag", cssProperty);
                        TextView propertyNameTextView = new TextView(this);
                        propertyNameTextView.setText(cssProperty);
                        binding.formContainer.addView(propertyNameTextView);

                        Spinner propertyValueSpinner = new Spinner(this);
                        List<String> allowedValues = elementCssProperties.get(cssProperty);

                        // Create an ArrayAdapter for the dropdown list
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, allowedValues);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        propertyValueSpinner.setAdapter(adapter);

                        // Set the initial value from the element's attribute
                        String initialValue = element.attr(cssProperty);
                        int selectedIndex = allowedValues.indexOf(initialValue);
                        propertyValueSpinner.setSelection(selectedIndex);

                        // Generate a unique identifier for the form element
                        String elementId = tagName + "_" + i + "_" + cssProperty;
                        propertyValueSpinner.setId(View.generateViewId());
                        propertyValueSpinner.setTag(elementId);

                        binding.formContainer.addView(propertyValueSpinner);
                    }
                }
            }
        } catch (Exception e) {
            Log.d("tag", e.toString());
            e.printStackTrace();
        }
    }

    private void applyCssToElements() {
        Map<String, Map<String, String>> updatedCssProperties = new HashMap<>();

        for (int i = 0; i < binding.formContainer.getChildCount(); i += 2) {
            View child = binding.formContainer.getChildAt(i + 1);
//            Log.d("tag", String.valueOf(child)+i);
            if (child instanceof Spinner) {
                Spinner spinner = (Spinner) child;
                String cssProperty = spinner.getSelectedItem().toString();

//                Log.d("tag", cssProperty);

                View propertyNameView = binding.formContainer.getChildAt(i);
                if (propertyNameView instanceof TextView) {
                    TextView propertyNameTextView = (TextView) propertyNameView;
                    String elementId = spinner.getTag().toString();
                    String[] idParts = elementId.split("_");
                    String tagName = idParts[0];

                    Map<String, String> cssProperties = updatedCssProperties.get(tagName);
                    if (cssProperties == null) {
                        cssProperties = new HashMap<>();
                        updatedCssProperties.put(tagName, cssProperties);
                    }

                    propertyNameView = binding.formContainer.getChildAt(i + 1);
                    if (propertyNameView instanceof TextView) {
                        propertyNameTextView = (TextView) propertyNameView;
                        String propertyName = propertyNameTextView.getText().toString();
                        cssProperties.put(propertyName, cssProperty);
                    }
                }
            }
        }

        // Apply the updated CSS properties to the corresponding elements
        for (Element element : sectionElements) {
            String tagName = element.tagName();
            Map<String, String> cssProperties = updatedCssProperties.get(tagName);

            if (cssProperties != null) {
                for (Map.Entry<String, String> entry : cssProperties.entrySet()) {
                    String propertyName = entry.getKey();
                    String propertyValue = entry.getValue();
                    element.attr(propertyName, propertyValue);
                }
            }

        }
//        Log.d("tag", h)
        Log.d("TAG", String.valueOf(updatedCssProperties));


        // Update the WebView to reflect the changes

    }
}