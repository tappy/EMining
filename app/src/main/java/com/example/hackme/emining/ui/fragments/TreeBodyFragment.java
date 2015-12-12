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

public class TreeBodyFragment extends Fragment {

    private WebView webView;
    public static String line = "";
    private WebViewManager web_m;

    public static TreeBodyFragment newInstance() {
        return new TreeBodyFragment();
    }

    public TreeBodyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_tree_body_frag, container, false);
        webView = (WebView) rootview.findViewById(R.id.tree_body_frag);
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);
        web_m = new WebViewManager();
        TreeModelReq req = new TreeModelReq();
        req.userid = new DatabaseManager(rootview.getContext()).getLoginId();
        req.param = "body";
        loadSummary(req);
        return rootview;
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
                            line += web_m.htmlHead(web_m.getCSS());
                            ArrayList<String> as = getTreeVal(js);
                            for (int j = 0; j < as.size(); j++) {
                                String[] str = as.get(j).split(",");
                                line += "<div class='div draw_node m-top-1'>" +
                                        "<div class='inline bg_r-trans text_bold'>" +
                                        "กฏที่ " + (j + 1) + " " +
                                        "</div><br/>";
                                for (int i = 0; i < str.length; i++) {
                                    if (!str[i].equals("")) {
                                        if (i < str.length - 1) {
                                            line += "<div class='inline bg_r-primary'> " + str[i] + " </div>";
                                            line += "->";
                                        } else if (i < str.length) {
                                            String[] nstr = str[i].split(":");
                                            line += "<div class='inline bg_r-primary'> " + nstr[0] + " </div> ";
                                            line += " <b>:</b> <div class='inline bg_r-alert'> " + nstr[1] + " </div> ";
                                        }
                                    }
                                }
                                line += "</div>";
                            }
                            line += web_m.htmlFooter();
                            webView.loadData(line, "text/html; charset='utf-8' ", null);
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

    private ArrayList<String> getTreeVal(JSONArray js) {
        ArrayList<String> as = new ArrayList<>();
        int tSize = getTreeSize(js);
        String[] nodeCount = new String[tSize];
        try {
            for (int node = 0; node < js.length(); node++) {
                String str = "";
                String[] mnode = js.getString(node).split("\\|");
                Log.d("node", mnode[mnode.length - 1]);
                nodeCount[mnode.length - 1] = mnode[mnode.length - 1];
                for (int i = mnode.length; i < tSize; i++) {
                    nodeCount[i] = null;
                }
                if (mnode[mnode.length - 1].split(":").length > 1) {
                    for (String v : nodeCount) {
                        if (v != null) {
                            str += v + ",";
                        }
                    }
                    as.add(str);
                }
            }
            return as;
        } catch (Exception ex) {
            ex.printStackTrace();
            return as;
        }
    }

    private int getTreeSize(JSONArray js) {
        int count = 0;
        try {
            for (int i = 0; i < js.length(); i++) {
                String[] str = js.getString(i).split("\\|");
                if (str.length > count) count = str.length;
            }
            Log.d("node count", count + "");
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return count;
        }
    }
}
