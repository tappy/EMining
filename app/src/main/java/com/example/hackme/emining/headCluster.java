package com.example.hackme.emining;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

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

public class headCluster extends Fragment {
    private View rootView;
    private WebView cluster_head_web;
    private String webData;

    public static headCluster newInstance() {
        headCluster fragment = new headCluster();
        return fragment;
    }

    public headCluster() {
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
        cluster_head_web = (WebView) rootView.findViewById(R.id.cluster_head_web);
        cluster_head_web.setWebViewClient(new WebViewClient());
        WebSettings webSettings = cluster_head_web.getSettings();
        webSettings.setJavaScriptEnabled(true);

        loadClusterContent("head");
        loadClusterContent("footer");
        return rootView;
    }

    public void loadClusterContent(String loadParam) {
        new AsyncTask<String, Void, String[]>() {
            @Override
            protected void onPreExecute() {

            }

            @Override
            protected String[] doInBackground(String... params) {
                try {
                    StringBuilder builder = new StringBuilder();
                    HttpClient client = new DefaultHttpClient();
                    HttpPost post = new HttpPost(new webServiceConfig().getHost("getClusterModel.php"));
                    List<NameValuePair> list = new ArrayList<NameValuePair>();
                    list.add(new BasicNameValuePair("getParam", params[0]));
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
                    Log.d("res", builder.toString());
                    String[] ret = {builder.toString(), params[0]};
                    return ret;
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String[] s) {

                //Toast.makeText(getBaseContext(), s[0], Toast.LENGTH_SHORT).show();

                try {
                    JSONArray js = new JSONArray(s[0]);
                    if (s[1] == "head") {
                        webData = "<!Doctype html><head>"+new webView_manager(rootView).getCSS()+"</head>" +
                                "<body><table widht='100%' border=0>";
                        for (int i = 0; i < js.length(); i++) {
                            webData += "<tr>";
                            webData += "<td>" + js.getString(i) + "</td>";
                            webData += "</tr>";
                        }
                    } else {
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
                                ArrayList<String> marr=getClusterHeadTable(js.getString(i).toString());
                                webData += "<td align='center' >" + marr.get(0) + "</td>";
                                webData += "<td align='center' >" + marr.get(1) + "</td>";
                                webData += "<td align='center' >" + marr.get(2).replaceAll("[(-)]+","") + "</td>";
                                webData += "</tr>";
                            }
                        }
                        webData += "</tbody></tr>";
                        webData += "</table>";
                        webData += "</table></body></html>";
                        cluster_head_web.loadData(webData, "text/html; charset=UTF-8", null);
                    }
                } catch (Exception e) {
                    Log.d("webview err", e.toString());
                }
            }

        }.execute(loadParam, new database_manager(rootView.getContext()).getLoginId());

    }

    private ArrayList getClusterHeadTable(String val) {

        ArrayList<String> arr=new ArrayList<>();
        String vx[] = val.split(" ");
        for (int i = 0; i < vx.length; i++) {
            if (!vx[i].equals("")) {
            arr.add(vx[i]);
            }
        }

        Log.d("Size Arr",arr.size()+"");
        if(arr.size()>=4){
            String temp=arr.get(2);
          arr.add(2,(temp+arr.get(3)));
        }
        Log.d("Size Arr",arr.size()+"");
        return arr;
    }

}
