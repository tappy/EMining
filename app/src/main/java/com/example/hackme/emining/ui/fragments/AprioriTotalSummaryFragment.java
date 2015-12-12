package com.example.hackme.emining.ui.fragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.hackme.emining.Helpers.StringHelper;
import com.example.hackme.emining.R;
import com.example.hackme.emining.entities.GetApioriModelReq;
import com.example.hackme.emining.model.DatabaseManager;
import com.example.hackme.emining.Helpers.WebViewManager;
import com.example.hackme.emining.model.GetApioriModelLoader;
import com.example.hackme.emining.model.ModelLoader;

import org.json.JSONArray;
public class AprioriTotalSummaryFragment extends Fragment {

    private View rootView;
    private WebView apriori_summary_WebView;
    private String dataval = "";
    private GetApioriModelReq req;

    // TODO: Rename and change types and number of parameters
    public static AprioriTotalSummaryFragment newInstance() {
        AprioriTotalSummaryFragment fragment = new AprioriTotalSummaryFragment();
        return fragment;
    }

    public AprioriTotalSummaryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_apriori_total_summary, container, false);
        apriori_summary_WebView = (WebView) rootView.findViewById(R.id.apriori_summary_webView);
        apriori_summary_WebView.setWebViewClient(new WebViewClient());
        loadBody("body");
        loadBody("summary");
        return rootView;
    }

    public void loadBody(String type) {
        req = new GetApioriModelReq();
        req.userId = new DatabaseManager(getContext()).getLoginId();
        req.param = type;
        new GetApioriModelLoader(req, new ModelLoader.DataLoadingListener() {
            @Override
            public void onLoaded(String data) {
                try {
                    JSONArray js = new JSONArray(data);
                    if (req.param.equals("summary")) {
                        dataval += "<!Doctype html><head>" + new WebViewManager(rootView).getCSS() + "</head><body><div class='summary div'>";
                        dataval += "<div class='div m-top-1' >&nbsp&nbsp&nbsp&nbsp จากผลการวิเคราะห์เหมืองข้อมูลได้ใช้วิธีการหากฏความสัมพันธ์อัลกอริทึม Apriori ได้ค่าต่างๆดังนี้</div>";
                        for (int i = 0; i < js.length(); i++) {
                            if (js.getString(i).split(":")[0].trim().equals("Minimum support")) {
                                dataval += "<div class='div draw_node m-top-1' > ค่า Minimum support เท่ากับ " + js.getString(i).split(":")[1].split("\\(")[0].trim() + " </div>";
                            } else if (js.getString(i).split(":")[0].replace("<confidence>", "").trim().equals("Minimum metric")) {
                                dataval += "<div class='div draw_node m-top-1' > ค่า Minimum metric เท่ากับ " + js.getString(i).split(":")[1].trim() + " </div>";
                            } else if (js.getString(i).split(":")[0].trim().equals("Number of cycles performed")) {
                                dataval += "<div class='div draw_node m-top-1' > ค่า Number of cycles performed เท่ากับ " + js.getString(i).split(":")[1].trim() + " </div>";
                            }
                        }
                    } else if (req.param.equals("body")) {
                        String[] listArr = new String[100];
                        dataval += "<div class='div m-top-1' >และมีกฏความสัมพันธ์ทั้ง " + js.length() + " กฏดังต่อไปนี้ </div>";
                        for (int i = 0; i < js.length(); i++) {
                            String[] sVal = js.getString(i).split(":");
                            int persen = (int) (Float.parseFloat(sVal[1].replaceAll("[(-)]", "").trim()) * 100) - 1;
                            if (listArr[persen] == null) {
                                listArr[persen] = "";
                            }
                            listArr[persen] += js.getString(i) + ",";
                            Log.d("persen", "" + (persen + 1));

                        }

                        for (int i = listArr.length - 1; i >= 0; i--) {
                            if (listArr[i] != null) {
                                dataval += "<div class='div apriori-box'><h5 class='text-title'><b> กลุ่มของกฏที่มีค่าความน่าเชื่อถือเท่ากับ " + (i + 1) + "%</b></h5>";
                                String[] v = listArr[i].split(",");
                                for (int j = 0; j < v.length; j++) {
                                    String[] sVal = v[j].split("==>");
                                    dataval += "<div class='div' ><h5 class='text-content'>กฏที่ " + StringHelper.getNumRuled(sVal[0]).replace(".", "");
                                    String[] atb = StringHelper.getPart1(sVal[0]).trim().split(" ");
                                    dataval += " ถ้า ";
                                    for (int k = 0; k < atb.length - 1; k++) {
                                        dataval += atb[k] + " ";
                                    }
                                    dataval += " แล้ว " + sVal[1].trim().split(":")[0].replace("conf", "").trim().split(" ")[0];
                                    dataval += "</h5></div><br/><br/>";
                                }
                                dataval += "</div>";
                            }
                        }
                        dataval += "</div></body></html>";
                        apriori_summary_WebView.loadData(dataval, "text/html; charset=UTF-8", null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(String message) {

            }
        });
    }
}
