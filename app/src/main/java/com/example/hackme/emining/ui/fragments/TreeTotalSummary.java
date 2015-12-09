package com.example.hackme.emining.ui.fragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.hackme.emining.R;
import com.example.hackme.emining.Helpers.WebServiceConfig;
import com.example.hackme.emining.Helpers.WebViewManager;
import com.example.hackme.emining.model.DatabaseManager;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TreeTotalSummary extends Fragment {

    private View rootView;
    private WebView webView;
    private String data="";

    public static TreeTotalSummary newInstance() {
        TreeTotalSummary fragment = new TreeTotalSummary();
        return fragment;
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
        webView=(WebView)rootView.findViewById(R.id.tree_total_summary);
        webView.setWebViewClient(new WebViewClient());
        getSummayModel("summary");
        return rootView;
    }

    private void getSummayModel(String mparam) {

        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {
                    StringBuilder builder = new StringBuilder();
                    HttpClient client = new DefaultHttpClient();
                    HttpPost post = new HttpPost(new WebServiceConfig().getHost("getTreeModel.php"));
                    List<NameValuePair> list = new ArrayList<NameValuePair>();
                    list.add(new BasicNameValuePair("param", params[0]));
                    list.add(new BasicNameValuePair("userid", params[1]));
                    post.setEntity(new UrlEncodedFormEntity(list));
                    HttpResponse response = client.execute(post);
                    int code = response.getStatusLine().getStatusCode();
                    if (code == 200) {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            builder.append(line);
                        }
                    }
                    return builder.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String s) {
                try {
                    JSONArray js=new JSONArray(s);
                    data += "<!Doctype html><head>" + new WebViewManager(rootView).getCSS() + "</head><body><div class='summary div'>";
                    data+="<div class='div m-top-1' >&nbsp&nbsp&nbsp&nbspจากผลการวิเคราะห์ข้อมูลด้วยต้นไม้ตัดสินใจอัลกอริทึม J48 ได้ค่าต่างๆดังนี้</div>";
                    for(int i=0;i<js.length();i++){
                       String val=js.getString(i).toString().replace("\n","");
                        if(val.equals("=== Stratified cross-validation ===")){
                            ArrayList marr = getListData(js, getLine(js, "=== Stratified cross-validation ===", 1), getLine(js, "=== Confusion Matrix ===", 2) - 1);
                            data+="<div class='m-top-1'>&nbsp</div><div class='div bg_r-base m-top-1 content_center' >ข้อมูล Stratified cross-validation </div>";
                            data+=getErrorOnTrainingDataPer(marr);
                        }else if(val.equals("=== Error on training data ===")){
                           // ArrayList marr = getListData(js, getLine(js, "=== Error on training data ===", 1), getLine(js, "=== Confusion Matrix ===", 1) - 1);
                           // data+="<div class='div bg_r-base m-top-1 content_center' >ข้อมูล Error on training data </div>";
                           // data+=getErrorOnTrainingDataPer(marr);
                        }
                    }
                    data+="</div></body></html>";
                    webView.loadData(data,"text/html; charset=UTF-8",null);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }.execute(mparam, new DatabaseManager(rootView.getContext()).getLoginId());
    }

    private String getErrorOnTrainingDataPer(ArrayList<String> list) {
        String wdata = "";
        for (int i = 1; i <= 2; i++) {
            String[] gData = getPart(list.get(i).toString().trim(), 3);
            if(i==1)
            {
                wdata+="<div class='div draw_node m-top-1' >ได้ค่า Correctly Classified Instances เท่ากับ "+gData[2].replace(" ","").trim()+" </div>";
            }
            else if(i==2){
                wdata+="<div class='div draw_node m-top-1' >ค่า Incorrectly Classified Instances เท่ากับ "+gData[2].replace(" ","").trim()+" </div>";
            }
        }
        wdata+=getErrorOnTrainingDataBody(list);
        return wdata;
    }

    private String getErrorOnTrainingDataBody(ArrayList<String> list) {
        String wdata = "";
        for (int i = 3; i < list.size(); i++) {
            String[] gData = getPart(list.get(i).toString().trim(), 2);
               wdata+= "<div class='div draw_node m-top-1' > ค่า "+gData[0]+" เท่ากับ "+gData[1]+"</div>";
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
