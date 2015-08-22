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
import android.widget.ProgressBar;
import android.widget.Toast;

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


public class apriori_summary_frag extends Fragment {

    private View rootview;
    private WebView webView;
    private ProgressBar aprioriProcessBar;
    public static apriori_summary_frag newInstance() {
        apriori_summary_frag fragment = new apriori_summary_frag();
        return fragment;
    }

    public apriori_summary_frag() {
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
        webView=(WebView)rootview.findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        aprioriProcessBar=(ProgressBar)rootview.findViewById(R.id.apriori_processBar);
        aprioriProcessBar.setVisibility(View.INVISIBLE);

        new loadSummary().execute(new database_manager(rootview.getContext()).getLoginId(),"summary");
        return rootview;
    }

    private class loadSummary extends AsyncTask<String, Void, String>{
        @Override
        protected void onPreExecute() {
        aprioriProcessBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                StringBuilder builder = new StringBuilder();
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(new webServiceConfig().getHost("getAprioryModel.php"));
                List<NameValuePair> list = new ArrayList<NameValuePair>();
                list.add(new BasicNameValuePair("userid", params[0]));
                list.add(new BasicNameValuePair("param", params[1]));
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
                String ret = builder.toString();
                return ret;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String aVoid) {
            //Toast.makeText(rootview.getContext(), aVoid, Toast.LENGTH_SHORT).show();
            try{
                JSONArray js=new JSONArray(aVoid);
                String webData = "<!Doctype html><head>" +new webView_manager(rootview).getCSS()+"</head>" +
                        "<body><table widht='100%' border=0>";
                for(int i=0;i<js.length();i++){
                    webData+="<tr>";
                    if(i==3)webData+="<td><B>"+js.getString(i)+"</B></td>";
                    else webData+="<td>"+js.getString(i)+"</td>";
                    webData+="</tr>";
                }
                webData+="</table></body></html>";
                webView.loadData(webData,"text/html; charset=UTF-8",null);
                aprioriProcessBar.setVisibility(View.INVISIBLE);
            }catch (Exception e){
             Log.d("webview err",e.toString());
            }
        }
    }




}
