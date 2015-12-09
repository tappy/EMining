package com.example.hackme.emining;


import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

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

public class Summary extends Fragment {

    private View rootView;
    private WebView webView;
    private String data = "";
    private ProgressBar cluster_process;

    public static Summary newInstance() {
        Summary fragment = new Summary();
        return fragment;
    }

    public Summary() {
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
        cluster_process=(ProgressBar)rootView.findViewById(R.id.cluster_process);
        cluster_process.setVisibility(View.INVISIBLE);
        webView = (WebView) rootView.findViewById(R.id.cluster_summary);
        webView.setWebViewClient(new WebViewClient());
        getSummayModel("head");
        getSummayModel("footer");
        return rootView;
    }

    private void getSummayModel(final String param) {

        new AsyncTask<String, Void, String[]>() {

            @Override
            protected void onPreExecute() {
                cluster_process.setVisibility(View.VISIBLE);
            }

            @Override
            protected String[] doInBackground(String... params) {
                try {
                    StringBuilder builder = new StringBuilder();
                    HttpClient client = new DefaultHttpClient();
                    HttpPost post = new HttpPost(new WebServiceConfig().getHost("getClusterModel.php"));
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
                    String[] str=new String[2];
                    str[0]=builder.toString();
                    str[1]=params[0];
                    return str;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String[] s) {
                try {
                    JSONArray jsonArray = new JSONArray(s[0]);
                    if(s[1]=="head") {
                        data += "<!Doctype html><head>" + new WebViewManager(rootView).getCSS() + "</head><body><div class='summary div'>";
                      int iteration = Integer.parseInt(jsonArray.getString(0).split(":")[1].trim());
                        data += "<div class='div m-top-1' >&nbsp&nbsp&nbsp&nbspจากผลการวิเคราะห์ข้อมูลด้วยวิธีการจัดกลุ่มโดยใช้อัลกอริทึม Simple KMeans ได้ค่าต่างๆตังนี้</div>" +
                                "<div class='div draw_node m-top-1' > ค่า Number of iterations เท่ากับ";
                        data += " " + iteration + " </div>";
                        data += "<div class='div draw_node m-top-1' > ค่า Within cluster sum of squared errors เท่ากับ";
                        data += " " + Float.parseFloat(jsonArray.getString(1).split(":")[1].trim()) + " </div>";
                    }else if(s[1]=="footer"){
                        data+="<div class='div m-top-1' > ซึ่งในการวิเคราะห์ได้ทำการจัดกลุ่มของข้อมูลได้เป็น "+(jsonArray.length()-1)+" กลุ่มดังนี้</div>";
                        for(int i=1;i<jsonArray.length();i++){
                            String val=jsonArray.getString(i).replaceAll("[(-)]+", " ");
                                   val=val.replaceAll(" +", " ");
                            String[] v=val.trim().split(" ");
                            data+="<div class='div draw_node m-top-1' >  กลุ่มที่ "+(i-1)+" มีจำนวนข้อมูล "+v[1]+" เรคคอร์ด คิดเป็น "+v[2]+"</div>";
                        }
                        data+="</div></body></html>";
                        webView.loadData(data,"text/html; charset=UTF-8",null);
                        cluster_process.setVisibility(View.INVISIBLE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.execute(param, new DatabaseManager(rootView.getContext()).getLoginId());
    }
}
