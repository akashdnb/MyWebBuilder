package com.example.mywebbuilder.preview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.mywebbuilder.R;
import com.example.mywebbuilder.databinding.ActivityPreviewBinding;

import java.io.File;

public class PreviewActivity extends AppCompatActivity {
    ActivityPreviewBinding binding;
    String projectPath, projectName;
    boolean isHTTPUrl = false;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPreviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setUpMenu();

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
                if(isHTTPUrl){
                    binding.webView.setVisibility(View.GONE);
                    binding.noConnLottie.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setUpMenu() {
        binding.menuMore.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(PreviewActivity.this, v);
            popupMenu.inflate(R.menu.preview_menu);

            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.open_in_browser:
                        try {
                            File file = new File(projectPath);
                            Uri fileUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".fileprovider", file);

                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(fileUri, "text/html");
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            startActivity(intent);
                        }catch (Exception e){
                            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    case R.id.save_file:
                        Toast.makeText(PreviewActivity.this, "saved", Toast.LENGTH_SHORT).show();
                        return true;
                }
                return false;
            });
            popupMenu.show();
        });

    }

}