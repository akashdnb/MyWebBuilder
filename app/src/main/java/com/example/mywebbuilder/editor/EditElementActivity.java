package com.example.mywebbuilder.editor;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.mywebbuilder.databinding.ActivityEditElementBinding;
import com.example.mywebbuilder.models.ComponentModel;
import com.example.mywebbuilder.utils.StorageUtil;

import org.json.JSONObject;
import java.util.List;

public class EditElementActivity extends AppCompatActivity {
    ActivityEditElementBinding binding;
    public String projectName, projectID, projectPath;
    ComponentModel editComponent;
    List<ComponentModel> editorList;
    int editPosition = 0;

    @SuppressLint({"SetJavaScriptEnabled", "SetTextI18n"})
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
                        "        var elementInfo = {" +
                        "            'tagName': this.tagName," +
                        "            'className': this.className," +
                        "            'innerText': this.innerText," +
                        "            'src': this.src," +
                        "            'href': this.href" +
                        "        };" +
                        "        Android.onClick(JSON.stringify(elementInfo));" +
                        "    });" +
                        "}" +
                        "})()");
            }
        });
        binding.editWebView.loadUrl(editComponent.getPreviewUrl());
    }

    void highlightSelectedElement(JSONObject elementInfo) {
        try {
            StringBuilder javascriptCode = new StringBuilder("var elements = document.getElementsByTagName('*');" +
                    "for (var i = 0; i < elements.length; i++) {" +
                    "    var element = elements[i]; " +
                    "if (");
            String[] selectors = {"tagName", "className", "innerText", "href", "src"};

            for (String selector : selectors) {
                if (elementInfo.get("tagName").equals("DIV")
                        && !selector.equals("className") && !selector.equals("DIV")) {
                    continue;
                }
                if (elementInfo.has(selector)) {
                    String selectorValue = elementInfo.getString(selector);
                    javascriptCode.append("element.").append(selector).append(" === '").append(selectorValue).append("' && ");
                }
            }
            javascriptCode = new StringBuilder(javascriptCode.substring(0, javascriptCode.length() - 3));
            javascriptCode.append(")" + "{ element.classList.add('highlighted');" + "break;" + " }" + "}");

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

    public class MyJavaScriptInterface {
        @JavascriptInterface
        public void onClick(String elementInfoJson) {
            try {
                JSONObject elementInfo = new JSONObject(elementInfoJson);
                Activity activity = EditElementActivity.this;
                activity.runOnUiThread(() -> {
                    removeHighlight();
                    highlightSelectedElement(elementInfo);
                    binding.elementTv.setText(elementInfoJson);
                });
            } catch (Exception e) {
                Toast.makeText(EditElementActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }
}