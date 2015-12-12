package com.example.hackme.emining.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.example.hackme.emining.R;
import com.example.hackme.emining.Helpers.WebViewManager;
import com.example.hackme.emining.entities.GetClusterModelReq;
import com.example.hackme.emining.model.DatabaseManager;
import com.example.hackme.emining.model.GetClusterModelLoader;
import com.example.hackme.emining.model.ModelLoader;

import org.json.JSONArray;

public class SummaryFragment extends Fragment {

    private View rootView;
    private WebView webView;
    private String webData = "";
    private ProgressBar progressBar;

    public static SummaryFragment newInstance() {
        return new SummaryFragment();
    }

    public SummaryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_summary, container, false);
        progressBar = (ProgressBar) rootView.findViewById(R.id.cluster_process);
        progressBar.setVisibility(View.VISIBLE);
        webView = (WebView) rootView.findViewById(R.id.cluster_summary);
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);
        GetClusterModelReq req = new GetClusterModelReq();
        req.userId = new DatabaseManager(rootView.getContext()).getLoginId();
        req.param = "head";
        getSummaryModel(req);
        return rootView;
    }

    private void getSummaryModel(final GetClusterModelReq req) {

        new GetClusterModelLoader(req, new ModelLoader.DataLoadingListener() {
            @Override
            public void onLoaded(final String data) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONArray jsonArray = new JSONArray(data);
                                    if (req.param.equals("head")) {
                                        webData += "<!Doctype html><head>" + new WebViewManager().getCSS() + "</head><body><div class='summary div'>";
                                        int iteration = Integer.parseInt(jsonArray.getString(0).split(":")[1].trim());
                                        webData += "<div class='div m-top-1' >&nbsp&nbsp&nbsp&nbspจากผลการวิเคราะห์ข้อมูลด้วยวิธีการจัดกลุ่มโดยใช้อัลกอริทึม Simple KMeans ได้ค่าต่างๆตังนี้</div>" +
                                                "<div class='div draw_node m-top-1' > ค่า Number of iterations เท่ากับ";
                                        webData += " " + iteration + " </div>";
                                        webData += "<div class='div draw_node m-top-1' > ค่า Within cluster sum of squared errors เท่ากับ";
                                        webData += " " + Float.parseFloat(jsonArray.getString(1).split(":")[1].trim()) + " </div>";
                                        GetClusterModelReq req2 = new GetClusterModelReq();
                                        req2.userId = new DatabaseManager(rootView.getContext()).getLoginId();
                                        req2.param = "footer";
                                        getSummaryModel(req2);
                                    } else if (req.param.equals("footer")) {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        webData += "<div class='div m-top-1' > ซึ่งในการวิเคราะห์ได้ทำการจัดกลุ่มของข้อมูลได้เป็น " + (jsonArray.length() - 1) + " กลุ่มดังนี้</div>";
                                        for (int i = 1; i < jsonArray.length(); i++) {
                                            String[] v = jsonArray.getString(i).replaceAll("[(-)]+", " ").replaceAll(" +", " ").trim().split(" ");
                                            webData += "<div class='div draw_node m-top-1' >  กลุ่มที่ " + (i - 1) + " มีจำนวนข้อมูล " + v[1] + " เรคคอร์ด คิดเป็น " + v[2] + "</div>";
                                        }
                                        webData += "</div></body></html>";
                                        webView.loadData(webData, "text/html; charset=UTF-8", null);
                                    }
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        });
                    }
                });
            }

            @Override
            public void onFailed(String message) {

            }
        });
    }
}
