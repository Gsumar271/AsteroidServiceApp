package com.eugenesumaryev.myapipractice02;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by eugenesumaryev on 12/7/17.
 */

public class AsteroidWebFragment extends Fragment {

    private WebView mWebView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.asteroid_web_layout, container, false);

        mWebView = (WebView) view.findViewById(R.id.activity_webview);

        mWebView.loadUrl("https://en.wikipedia.org/wiki/Asteroid");

        // Force links and redirects to open in the WebView instead of in a browser
        mWebView.setWebViewClient(new WebViewClient());

        return view;
    }
}
