package com.example.mywebbuilder.preview;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.mywebbuilder.databinding.ActivityPreviewBinding;
import com.example.mywebbuilder.jsoupUtils.HTMLUtils;
import com.example.mywebbuilder.utils.DirectoryUtil;

import java.io.File;

public class PreviewActivity extends AppCompatActivity {
    ActivityPreviewBinding binding;
    HTMLUtils htmlUtils;
    String newId= "header1", projectPath, projectName;
    boolean isHTTPUrl = false;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPreviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backBtn.setOnClickListener(v -> finish());
        binding.webView.getSettings().setJavaScriptEnabled(true);

        Intent intent= getIntent();
        projectPath = intent.getStringExtra("projectPath");
        projectName = intent.getStringExtra("projectName");
        isHTTPUrl = intent.getBooleanExtra("httpUrl", false);

        binding.siteTitleTv.setText(projectName);

        File file = new File(projectPath);
        if(file.exists() && !isHTTPUrl){
            binding.webView.loadUrl(file.getAbsolutePath());
        }else if(isHTTPUrl){
            binding.webView.loadUrl(projectPath);
        }else{
            Toast.makeText(this, "not exist", Toast.LENGTH_SHORT).show();
        }


//        String filename = "index.html";
//        File rootFile = new File(DirectoryUtil.rootProjects + "/" + "project1/index.html");

        WebSettings webSettings = binding.webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        binding.webView.getSettings().setAllowFileAccess(true);
        binding.webView.getSettings().setAllowFileAccessFromFileURLs(true);

        binding.webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                binding.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                binding.progressBar.setVisibility(View.GONE);
                binding.webView.setVisibility(View.GONE);
                if(isHTTPUrl){
                    binding.noConnLottie.setVisibility(View.VISIBLE);
                }
            }
        });

//        File file = new File(DirectoryUtil.rootAssets + "/" + "headerDrawer/header1.html");
//        File stylePath = new File(DirectoryUtil.rootAssets+ "/" +"headerDrawer/header1.css");
//        File scriptPath = new File(DirectoryUtil.rootAssets+ "/" +"headerDrawer/header1.js");
//        String filePath = file.getAbsolutePath();

//        htmlUtils = new HTMLUtils(rootFile.getAbsolutePath(), this);
//
//        try {
//            htmlUtils.addHTMLFile(filePath, "root", newId);
//            htmlUtils.linkStyle(stylePath.getAbsolutePath(), newId);
//            htmlUtils.linkScript(scriptPath.getAbsolutePath(), newId);
//        } catch (Exception e) {
//            Log.d("TAG", e.toString());
//        }
//
//        if(file.exists()){
//            binding.webView.loadUrl(rootFile.getAbsolutePath());
//        }else{
//            Toast.makeText(this, "not exist", Toast.LENGTH_SHORT).show();
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        try {
//            htmlUtils.removeWithId(newId+"_css");
//            htmlUtils.removeWithId(newId+"_script");
//            htmlUtils.removeWithId(newId);
//
//        }catch (Exception e){
//            Log.d("TAG", e.toString());
//        }
    }
}