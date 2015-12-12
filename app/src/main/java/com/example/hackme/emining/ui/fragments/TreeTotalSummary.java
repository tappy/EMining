package com.example.hackme.emining.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.hackme.emining.R;
import com.example.hackme.emining.Helpers.WebViewManager;
import com.example.hackme.emining.entities.SummayLoaderReq;
import com.example.hackme.emining.entities.TreeModelReq;
import com.example.hackme.emining.model.DatabaseManager;
import com.example.hackme.emining.model.GetTreeModelLoader;
import com.example.hackme.emining.model.ModelLoader;
import com.example.hackme.emining.model.SummaryLoader;

import org.json.JSONArray;

import java.util.ArrayList;

public class TreeTotalSummary extends Fragment {

    private View rootView;
    private WebView webView;
    private String webData = "";

    public static TreeTotalSummary newInstance() {
        return new TreeTotalSummary();
    }

    public TreeTotalSummary() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_tree_total_summary, container, false);
        webView = (WebView) rootView.findViewById(R.id.tree_total_summary);
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);
        TreeModelReq req = new TreeModelReq();
        req.userid = new DatabaseManager(getActivity()).getLoginId();
        req.param = "summary";
        loadSummary(req);
        return rootView;
    }

    public void loadSummary(TreeModelReq req) {
        new GetTreeModelLoader(req, new ModelLoader.DataLoadingListener() {
            @Override
            public void onLoaded(final String data) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONArray js = new JSONArray(data);
                            webData += "<!Doctype html><head>" + new WebViewManager().getCSS() + "</head><body><div class='summary div'>";
                            webData += "<div class='div m-top-1' >&nbsp&nbsp&nbsp&nbspจากผลการวิเคราะห์ข้อมูลด้วยต้นไม้ตัดสินใจอัลกอริทึม J48 ได้ค่าต่างๆดังนี้</div>";
                            for (int i = 0; i < js.length(); i++) {
                                String val = js.getString(i).replace("\n", "");
                                if (val.equals("=== Stratified cross-validation ===")) {
                                    ArrayList marr = getListData(js, getLine(js, "=== Stratified cross-validation ===", 1), getLine(js, "=== Confusion Matrix ===", 2) - 1);
                                    webData += "<div class='m-top-1'>&nbsp</div><div class='div bg_r-base m-top-1 content_center' >ข้อมูล Stratified cross-validation </div>";
                                    webData += getErrorOnTrainingDataPer(marr);
                                } else if (val.equals("=== Error on training data ===")) {

                                }
                            }
                            webData += "</div></body></html>";
                            webView.loadData(webData, "text/html; charset=UTF-8", null);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onFailed(String message) {

            }
        });
    }

    private String getErrorOnTrainingDataPer(ArrayList<String> list) {
        String wdata = "";
        for (int i = 1; i <= 2; i++) {
            String[] gData = getPart(list.get(i).trim(), 3);
            if (i == 1) {
                wdata += "<div class='div draw_node m-top-1' >ได้ค่า Correctly Classified Instances เท่ากับ " + gData[2].replace(" ", "").trim() + " </div>";
            } else if (i == 2) {
                wdata += "<div class='div draw_node m-top-1' >ค่า Incorrectly Classified Instances เท่ากับ " + gData[2].replace(" ", "").trim() + " </div>";
            }
        }
        wdata += getErrorOnTrainingDataBody(list);
        return wdata;
    }

    private String getErrorOnTrainingDataBody(ArrayList<String> list) {
        String wdata = "";
        for (int i = 3; i < list.size(); i++) {
            String[] gData = getPart(list.get(i).trim(), 2);
            wdata += "<div class='div draw_node m-top-1' > ค่า " + gData[0] + " เท่ากับ " + gData[1] + "</div>";
        }
        return wdata;
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

}
