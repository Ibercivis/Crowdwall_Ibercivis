package com.example.webviewwsm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class MainActivity extends AppCompatActivity {

    WebView visor;
    String url = "http://lxbifi21.bifi.unizar.es/windy";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        visor = (WebView) findViewById(R.id.webview);

        final WebSettings ajustesVisorWeb = visor.getSettings();
        ajustesVisorWeb.setJavaScriptEnabled(true);

        visor.loadUrl(url);
    }
}
