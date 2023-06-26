package com.example.mywebbuilder.editor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.mywebbuilder.databinding.ActivityEditElementBinding;
import com.example.mywebbuilder.jsoupUtils.ComponentEditor;
import com.example.mywebbuilder.models.ComponentModel;
import com.example.mywebbuilder.models.ElementStyles;
import com.example.mywebbuilder.utils.StorageUtil;
import com.example.mywebbuilder.utils.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class EditElementActivity extends AppCompatActivity {
    ActivityEditElementBinding binding;
    ArrayList<Pair<String, String>> filteredStyleList = new ArrayList<>();
    public String projectName, projectID, projectPath;
    ComponentModel editComponent;
    ElementPropertyAdapter propertyAdapter;
    List<ComponentModel> editorList;
    HashMap<String, HashMap<String, String>> editedCss;
    HashMap<String, HashMap<String, String>> editedHtml;
    int editPosition = 0;
    String currentElementId = null;
    String currentElementTagName = "";
    String currentElementClassName = "";

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
        filteredStyleList = new ArrayList<>();
        editedCss = new HashMap<>();
        editedHtml = new HashMap<>();

        setUpWebView();
        binding.backBtn.setOnClickListener(v -> onBackPressed());
        binding.saveBtn.setOnClickListener(v -> {
            ComponentEditor.applyEditedCss(editedCss, editComponent.getPreviewUrl(), projectPath);
            ComponentEditor.applyEditedHtml(editedHtml, editComponent.getPreviewUrl(), projectPath);
            editedCss.clear();
            editedHtml.clear();
            binding.editWebView.reload();
        });
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setUpWebView() {
        binding.editWebView.getSettings().setJavaScriptEnabled(true);
        binding.editWebView.getSettings().setAllowFileAccess(true);
        binding.editWebView.getSettings().setAllowFileAccessFromFileURLs(true);
        MyJavaScriptInterface jsInterface = new MyJavaScriptInterface();
        binding.editWebView.addJavascriptInterface(jsInterface, "Android");

        binding.editWebView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                insertHighlightStyle();
                view.loadUrl("javascript:(function() {" +
                        "var elements = document.getElementsByTagName('*');" +
                        "for (var i = 0; i < elements.length; i++) {" +
                        "    elements[i].addEventListener('click', function(event) {" +
                        "        event.stopPropagation();" +
                        "        var tagName = this.tagName;" +
                        "        var id = this.id;" +
                        "        var className = this.className;" +
                        "        var computedStyle = window.getComputedStyle(this);" +
                        "        var style = {};" +
                        "        var htmlProp = {};" +
                        "        htmlProp['innerText'] = this.innerText;" +
                        "        htmlProp['href'] = this.href;" +
                        "        htmlProp['src'] = this.src;" +
                        "        for (var j = 0; j < computedStyle.length; j++) {" +
                        "            var property = computedStyle[j];" +
                        "            var value = computedStyle.getPropertyValue(property);" +
                        "            if (value !== '' && value !== 'none') {" +
                        "                style[property] = value;" +
                        "            }" +
                        "        }" +
                        "        var elementInfo = {" +
                        "            'tagName': tagName," +
                        "            'className': className," +
                        "            'id': id," +
                        "            'style': style," +
                        "            'htmlProp': htmlProp" +
                        "        };" +
                        "        Android.onClick(JSON.stringify(elementInfo));" +
                        "    });" +
                        "}" +
                        "})()");
                if (currentElementId != null) highlightSelectedElement(currentElementId);
            }
        });
        binding.editWebView.loadUrl(editComponent.getPreviewUrl());
    }

    private void setUpRecyclerview() {
        binding.propRv.setLayoutManager(new LinearLayoutManager(this));
        propertyAdapter = new ElementPropertyAdapter(this, filteredStyleList);
        binding.propRv.setAdapter(propertyAdapter);
    }

    public void setEditedCssProperties(Pair<String, String > property){
        if(filteredStyleList.contains(property) && editedCss.containsKey(currentElementId) && Objects.requireNonNull(editedCss.get(currentElementId)).containsKey(property.first)){
            Objects.requireNonNull(editedCss.get(currentElementId)).remove(property.first);
        }
        else if(!filteredStyleList.contains(property)){
            if(!editedCss.containsKey(currentElementId)){
                editedCss.put(currentElementId, new HashMap<>());
            }
            Objects.requireNonNull(editedCss.get(currentElementId)).put(property.first, property.second);
        }

        updateElementCssProperty(StringUtil.convertToCamelCase(property.first), property.second);
    }

    public void setEditedHtmlProperties(Pair<String, String > property){
        if(filteredStyleList.contains(property) && editedHtml.containsKey(currentElementId) && Objects.requireNonNull(editedHtml.get(currentElementId)).containsKey(property.first)){
            Objects.requireNonNull(editedHtml.get(currentElementId)).remove(property.first);
        }
        else if(!filteredStyleList.contains(property)){
            if(!editedHtml.containsKey(currentElementId)){
                editedHtml.put(currentElementId, new HashMap<>());
            }
            Objects.requireNonNull(editedHtml.get(currentElementId)).put(property.first, property.second);

        }
        if(property.first.equals("innerText")){
            updateElementInnerText(property.second);
        }
        else updateElementHtmlProperty(property.first, property.second);
    }

    void highlightSelectedElement(String elementId) {
        try {
            String javascriptCode = "var element = document.getElementById('" + elementId + "');" +
                    "if (element) {" +
                    "    element.classList.add('highlighted');" +
                    "}";
            binding.editWebView.loadUrl("javascript:(function() { " + javascriptCode + " })();");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void removeHighlight() {
        try {
            String javascriptCode = "var elements = document.getElementsByClassName('highlighted');" +
                    "while (elements.length) {" +
                    "    elements[0].classList.remove('highlighted');" +
                    "}";
            binding.editWebView.loadUrl("javascript:(function() { " + javascriptCode + " })();");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void insertHighlightStyle() {
        try {
            String javascriptCode = "var style = document.createElement('style');" +
                    "style.innerHTML = '.highlighted { border: 2px solid red; }';" +
                    "document.head.appendChild(style);";

            binding.editWebView.loadUrl("javascript:(function() { " + javascriptCode + " })();");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    void filterStyle(JSONObject elementInfo) {
        filteredStyleList = new ArrayList<>();
        if (elementInfo == null) {
            return;
        }
        try {
            JSONObject styleObject = elementInfo.getJSONObject("style");
            JSONObject htmlProp = elementInfo.getJSONObject("htmlProp");

            String tagName = elementInfo.getString("tagName").toLowerCase(Locale.ROOT);

            if ((tagName.equals("p") || tagName.equals("li") || tagName.equals("a") || tagName.startsWith("h")) && htmlProp.has("innerText")) {
                Pair<String, String> propertyPair = new Pair<>("innerText", htmlProp.getString("innerText"));
                filteredStyleList.add(propertyPair);
            }
            if (tagName.equals("img") && htmlProp.has("src")) {
                Pair<String, String> propertyPair = new Pair<>("src", htmlProp.getString("src"));
                filteredStyleList.add(propertyPair);
            }
            if (tagName.equals("a") || tagName.startsWith("li")) {
                Pair<String, String> propertyPair;
                if (htmlProp.has("href"))
                    propertyPair = new Pair<>("href", htmlProp.getString("href"));
                else propertyPair = new Pair<>("href", "");
                filteredStyleList.add(propertyPair);
            }

            Iterator<String> styleKeys = styleObject.keys();
            ArrayList<String> allowedProperties = ElementStyles.allowedProperties;

            while (styleKeys.hasNext()) {
                String property = styleKeys.next();

                int index = allowedProperties.indexOf(property);
                if (index != -1) {
                    String value;
                    try {
                        value = styleObject.getString(property);
                    } catch (JSONException e) {
                        continue;
                    }
                    Pair<String, String> propertyPair = new Pair<>(property, value);
                    filteredStyleList.add(propertyPair);
                }
            }
            binding.elementTitle.setText("</" + currentElementTagName + "> ");
            setUpRecyclerview();
        } catch (Exception e) {
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateElementCssProperty(String propertyName, String propertyValue) {
        try {
            String javascriptCode = "var element = document.getElementById('" + currentElementId + "');" +
                    "if (element) {" +
                    "    element.style." + propertyName + " = '" + propertyValue + "';" +
                    "}";
            binding.editWebView.loadUrl("javascript:(function() { " + javascriptCode + " })();");
        } catch (Exception e) {
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void updateElementHtmlProperty(String propertyName, String propertyValue) {
        try {
            String javascriptCode = "var element = document.getElementById('" + currentElementId + "');" +
                    "if (element) {" +
                    "    element.setAttribute('" + propertyName + "', '" + propertyValue + "');" +
                    "}";
            binding.editWebView.loadUrl("javascript:(function() { " + javascriptCode + " })();");
        } catch (Exception e) {
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void updateElementInnerText(String innerText) {
        try {
            String javascriptCode = "var element = document.getElementById('" + currentElementId + "');" +
                    "if (element) {" +
                    "    element.textContent = '" + innerText + "';" +
                    "}";
            binding.editWebView.loadUrl("javascript:(function() { " + javascriptCode + " })();");
        } catch (Exception e) {
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }




    public class MyJavaScriptInterface {
        @JavascriptInterface
        public void onClick(String elementInfoJson) {
            try {
                JSONObject elementInfo = new JSONObject(elementInfoJson);
                Activity activity = EditElementActivity.this;
                activity.runOnUiThread(() -> {
                    removeHighlight();
                    try {
                        currentElementId = elementInfo.getString("id");
                        currentElementTagName = elementInfo.getString("tagName").toLowerCase(Locale.ROOT);
                        currentElementClassName = elementInfo.getString("className");
                        highlightSelectedElement(currentElementId);
                    } catch (JSONException e) {
                        currentElementId = null;
                        e.printStackTrace();
                    }
                    if (elementInfo.has("style")) {
                        filterStyle(elementInfo);
                    }
                });
            } catch (Exception e) {
                Toast.makeText(EditElementActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

}