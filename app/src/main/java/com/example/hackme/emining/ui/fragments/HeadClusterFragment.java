package com.example.hackme.emining.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.example.hackme.emining.R;
import com.example.hackme.emining.entities.GetClusterModelReq;
import com.example.hackme.emining.model.DatabaseManager;
import com.example.hackme.emining.Helpers.WebViewManager;
import com.example.hackme.emining.model.GetClusterModelLoader;
import com.example.hackme.emining.model.ModelLoader;

import org.json.JSONArray;

import java.util.ArrayList;

public class HeadClusterFragment extends Fragment {
    private View rootView;
    private WebView webView;
    private String webData;
    private ProgressBar progressBar;
    private GetClusterModelReq req, req2;

    public static HeadClusterFragment newInstance() {
        return new HeadClusterFragment();
    }

    public HeadClusterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_head_cluster, container, false);
        progressBar = (ProgressBar) rootView.findViewById(R.id.process);
        progressBar.setVisibility(View.VISIBLE);
        webView = (WebView) rootView.findViewById(R.id.cluster_head_web);
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);
        req = new GetClusterModelReq();
        req.userId = new DatabaseManager(rootView.getContext()).getLoginId();
        req.param = "head";
        loadClusterContent(req);
        return rootView;
    }

    public void loadClusterContent(final GetClusterModelReq req) {

        new GetClusterModelLoader(req, new ModelLoader.DataLoadingListener() {
            @Override
            public void onLoaded(final String data) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONArray js = new JSONArray(data);
                            if (req.param.equals("head")) {
                                webData = "<!Doctype html><head>" + new WebViewManager().getCSS() + "</head>" +
                                        "<body><table widht='100%' border=0>";
                                for (int i = 0; i < js.length(); i++) {
                                    webData += "<tr>";
                                    webData += "<td>" + js.getString(i) + "</td>";
                                    webData += "</tr>";
                                }
                                req2 = new GetClusterModelReq();
                                req2.userId = new DatabaseManager(rootView.getContext()).getLoginId();
                                req2.param = "footer";
                                loadClusterContent(req2);
                            } else {
                                progressBar.setVisibility(View.INVISIBLE);
                                webData += "<tr>";
                                webData += "<table widht='100%' border=0>";
                                for (int i = 0; i < js.length(); i++) {
                                    if (i == 0) {
                                        webData += "<thead><tr>";
                                        webData += "<th align='center' colspan='3' >" + js.getString(i) + "</th>";
                                        webData += "</tr>" +
                                                "<tr>" +
                                                "<th>กลุ่ม</th>" +
                                                "<th>จำนวนข้อมูล</th>" +
                                                "<th>เปอร์เซ็นต์</th>" +
                                                "</tr></thead><tbody>";
                                    } else {
                                        webData += "<tr>";
                                        ArrayList<String> marr = getClusterHeadTable(js.getString(i));
                                        webData += "<td align='center' >" + marr.get(0) + "</td>";
                                        webData += "<td align='center' >" + marr.get(1) + "</td>";
                                        webData += "<td align='center' >" + marr.get(2).replaceAll("[(-)]+", "") + "</td>";
                                        webData += "</tr>";
                                    }
                                }
                                webData += "</tbody></tr>";
                                webData += "</table>";
                                webData += "</table></body></html>";
                                webView.loadData(webData, "text/html; charset=UTF-8", null);
                            }
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
    }

    private ArrayList<String> getClusterHeadTable(String val) {

        ArrayList<String> arr = new ArrayList<>();
        String vx[] = val.split(" ");
        for (String aVx : vx) {
            if (!aVx.equals("")) {
                arr.add(aVx);
            }
        }

        Log.d("Size Arr", arr.size() + "");
        if (arr.size() >= 4) {
            String temp = arr.get(2);
            arr.add(2, (temp + arr.get(3)));
        }
        Log.d("Size Arr", arr.size() + "");
        return arr;
    }

}
