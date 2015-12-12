package com.example.hackme.emining.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.example.hackme.emining.R;
import com.example.hackme.emining.entities.GetApioriModelReq;
import com.example.hackme.emining.model.DatabaseManager;
import com.example.hackme.emining.Helpers.WebViewManager;
import com.example.hackme.emining.model.GetApioriModelLoader;
import com.example.hackme.emining.model.ModelLoader;

import org.json.JSONArray;


public class AprioriSummaryFragment extends Fragment {

    private View rootview;
    private WebView webView;
    private ProgressBar aprioriProcessBar;
    private GetApioriModelReq req;

    public static AprioriSummaryFragment newInstance() {
        AprioriSummaryFragment fragment = new AprioriSummaryFragment();
        return fragment;
    }

    public AprioriSummaryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_apriori_summary_frag, container, false);
        webView = (WebView) rootview.findViewById(R.id.webView);
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);
        aprioriProcessBar = (ProgressBar) rootview.findViewById(R.id.apriori_processBar);
        aprioriProcessBar.setVisibility(View.VISIBLE);
        req = new GetApioriModelReq();
        req.param = "summary";
        req.userid = new DatabaseManager(rootview.getContext()).getLoginId();
        new GetApioriModelLoader(req, new ModelLoader.DataLoadingListener() {
            @Override
            public void onLoaded(final String data) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONArray js = new JSONArray(data);
                            String webData = "<!Doctype html><head>" + new WebViewManager().getCSS() + "</head>" +
                                    "<body><table widht='100%' border=0>";
                            for (int i = 0; i < js.length(); i++) {
                                webData += "<tr>";
                                if (i == 3) webData += "<td><B>" + js.getString(i) + "</B></td>";
                                else webData += "<td>" + js.getString(i) + "</td>";
                                webData += "</tr>";
                            }
                            webData += "</table></body></html>";
                            webView.loadData(webData, "text/html; charset=UTF-8", null);
                            aprioriProcessBar.setVisibility(View.INVISIBLE);
                        } catch (Exception e) {
                            Log.d("webview err", e.toString());
                        }
                    }
                });
            }

            @Override
            public void onFailed(String message) {

            }
        });

        return rootview;
    }


}
