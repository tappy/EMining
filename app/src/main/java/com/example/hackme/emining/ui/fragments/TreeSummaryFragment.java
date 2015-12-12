package com.example.hackme.emining.ui.fragments;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.example.hackme.emining.R;
import com.example.hackme.emining.Helpers.WebViewManager;
import com.example.hackme.emining.entities.SummayLoaderReq;
import com.example.hackme.emining.model.DatabaseManager;
import com.example.hackme.emining.model.ModelLoader;
import com.example.hackme.emining.model.SummaryLoader;

import org.json.JSONArray;

import java.util.ArrayList;


public class TreeSummaryFragment extends Fragment {

    private View rootview;
    private WebView webView;
    private String webData;
    private ProgressDialog tree_progressDialog;

    public static TreeSummaryFragment newInstance() {
        TreeSummaryFragment fragment = new TreeSummaryFragment();
        return fragment;
    }

    public TreeSummaryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_tree_summary_frag, container, false);
        webView = (WebView) rootview.findViewById(R.id.tree_summary_web_wiew);
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);
        SummayLoaderReq req = new SummayLoaderReq();
        req.userid = new DatabaseManager(rootview.getContext()).getLoginId();
        req.param = "summary";
        loadSummary(req);
        return rootview;
    }

    public void loadSummary(SummayLoaderReq req) {
        new SummaryLoader(req, new ModelLoader.DataLoadingListener() {
            @Override
            public void onLoaded(String data) {
                try {
                    JSONArray js = new JSONArray(data);
                    webData = "<!Doctype html><head>" + new WebViewManager().getCSS() + "</head>" +
                            "<body><table widht='100%' border=0>";
                    webData += getFirstSummary(js);
                    int seeCount = 0;
                    for (int i = 0; i < js.length(); i++) {
                        if (js.getString(i).trim().equals("=== Error on training data ===")) {

                        } else if (js.getString(i).trim().equals("=== Confusion Matrix ===") && seeCount == 0) {
                            seeCount++;
                        } else if (js.getString(i).trim().equals("=== Stratified cross-validation ===")) {

                            webData += "<tr><table widht='100%' border=0 style='background-color:#009688;'>";
                            ArrayList marr = getListData(js, getLine(js, "=== Stratified cross-validation ===", 1), getLine(js, "=== Confusion Matrix ===", 2) - 1);
                            webData += "<thead>" +
                                    "<tr>" +
                                    getErrorOnTrainingDataTitle(marr, "#009688") +
                                    "</tr>" +
                                    "</thead>";
                            webData += "<tbody>" +
                                    getErrorOnTrainingDataPer(marr) +
                                    getErrorOnTrainingDataBody(marr) +
                                    "</tbody>";
                            webData += "</table><tr>";

                        } else if (js.getString(i).trim().equals("=== Confusion Matrix ===") && seeCount > 0) {
                            seeCount++;
                            webData += "<tr><table widht='100%' border=0 style='background-color:#009688;'>";
                            ArrayList marr = getListData(js, getLine(js, "=== Confusion Matrix ===", 2), js.length() - 1);
                            webData += getConfusionMatrix(marr, "#009688");
                            webData += "</table><tr>";

                        }
                    }
                    webData += "</table></body></html>";
                    webView.loadData(webData, "text/html; charset=UTF-8", null);
                    tree_progressDialog.dismiss();
                } catch (Exception e) {
                    Log.d("webview err", e.toString());
                }
            }

            @Override
            public void onFailed(String message) {

            }
        });
    }


    private String getFirstSummary(JSONArray js) {
        String line = "";
        try {
            for (int i = 0; i < js.length(); i++) {
                if (!js.getString(i).trim().equals("=== Error on training data ===")) {
                    line += "<tr><td>" + js.getString(i).trim() + "</td></tr>";
                } else {
                    break;
                }
            }
            return line;
        } catch (Exception e) {
            return "";
        }
    }

    private String getErrorOnTrainingDataBody(ArrayList<String> list) {
        String wdata = "";
        for (int i = 3; i < list.size(); i++) {
            wdata += "<tr>";
            String[] gData = getPart(list.get(i).trim(), 2);
            for (int j = 0; j < gData.length; j++) {
                if (j == 0) wdata += "<td>" + gData[j] + "</td>";
                else wdata += "<td colspan='2'>" + gData[j] + "</td>";
            }
            wdata += "</tr>";

        }
        return wdata;
    }


    private String getErrorOnTrainingDataPer(ArrayList<String> list) {
        String wdata = "";
        for (int i = 1; i <= 2; i++) {
            wdata += "<tr>";
            String[] gData = getPart(list.get(i).trim(), 3);
            for (String aGData : gData) {
                wdata += "<td>" + aGData + "</td>";
            }
            wdata += "</tr>";
        }
        return wdata;
    }

    private String getErrorOnTrainingDataTitle(ArrayList<String> list, String color) {
        return "<th align='center' colspan='3' style='background-color:" + color + "; color:#FFFFFF;'>" + list.get(0).trim() + "</th>";
    }


    private String getConfusionMatrix(ArrayList<String> list, String color) {
        String wdata = "";
        try {
            wdata += "<thead>";
            for (int i = 0; i < list.size(); i++) {
                if (i == 0) {
                    int count = getColsCount(list);
                    wdata += "<tr>";
                    wdata += "<th align='center' colspan='" + String.valueOf(count) + "' style='background-color:" + color + "; color:#FFFFFF;'>" + list.get(i).trim() + "</th>";
                    wdata += "</tr>";
                } else if (i == 1) {
                    String[] l = list.get(i).split("<--")[0].trim().split(" ");
                    wdata += "<tr>";
                    for (String v : l) {
                        if (!v.isEmpty()) {
                            wdata += "<th>" + v.trim() + "</th>";
                        }
                    }
                    wdata += "</tr></thead><tbody>";
                } else {
                    String[] valSp = list.get(i).split("\\|")[0].trim().split(" ");
                    wdata += "<tr>";
                    for (String v : valSp) {
                        if (!v.trim().isEmpty()) {
                            wdata += "<td align='center'>" + v + "</td>";
                        }
                    }
                    wdata += "</tr>";
                }
            }
            for (int i = 1; i < list.size(); i++) {
                if (i == 1) {
                    wdata += "<tr>";
                    wdata += "<th colspan='" + getColsCount(list) + "' align='center'>Classified as</th>";
                    wdata += "</tr>";
                } else {
                    String valSp = list.get(i).split("\\|")[1];
                    wdata += "<tr>";
                    wdata += "<td colspan='" + getColsCount(list) + "'>" + valSp.trim() + "</td>";
                    wdata += "</tr>";
                }
            }
            wdata += "</tbody>";
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return wdata;
    }

    private int getColsCount(ArrayList list) {
        int count = 0;
        String[] mk = list.get(1).toString().split("<--")[0].trim().split(" ");
        for (String aMk : mk) {
            if (!aMk.isEmpty()) {
                count++;
            }
        }
        return count;
    }

    private String[] getPart(String val, int count) {
        int stu = 0;
        String[] ss = val.trim().split(" ");
        String[] gData = new String[count];
        for (int i = 0; i < ss.length; i++) {
            if (!ss[i].isEmpty()) {
                if (gData[stu] == null) {
                    gData[stu] = "";
                }
                gData[stu] += ss[i] + " ";
                if (stu < count - 1) {
                    if (ss[i + 1].trim().isEmpty()) {
                        stu++;
                    }
                }
            }
        }
        return gData;
    }

    private ArrayList getListData(JSONArray line, int f, int e) {
        try {
            ArrayList<String> list = new ArrayList<>();
            for (int j = f; j <= e; j++) {
                if (!line.getString(j).trim().equals("")) {
                    list.add(line.getString(j));
                }
            }
            return list;
        } catch (Exception ex) {
            return null;
        }

    }

    private int getLine(JSONArray js, String f, int see) {
        int seeing = 0;
        int k = 0;
        try {
            for (int i = 0; i < js.length(); i++) {
                if (js.get(i).toString().trim().equals(f.trim())) {
                    k = i;
                    seeing++;
                    if (seeing == see) {
                        break;
                    }
                }
            }
            return k;
        } catch (Exception ex) {
            return 0;
        }
    }
}
