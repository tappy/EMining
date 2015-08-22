package com.example.hackme.emining;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

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


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link tree_body_frag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class tree_body_frag extends Fragment {

    private View rootview;
    private WebView webView;
    public static String line = "";
    private webView_manager web_m;

    public static tree_body_frag newInstance() {
        tree_body_frag fragment = new tree_body_frag();
        return fragment;
    }

    public tree_body_frag() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_tree_body_frag, container, false);
        webView = (WebView) rootview.findViewById(R.id.tree_body_frag);
        webView.setWebChromeClient(new WebChromeClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        web_m = new webView_manager(rootview);
        new loadSummary().execute(new database_manager(rootview.getContext()).getLoginId(), "body");
        return rootview;
    }

    private class loadSummary extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            //processing
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                StringBuilder builder = new StringBuilder();
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(new webServiceConfig().getHost("getTreeModel.php"));
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
            try {
                JSONArray js = new JSONArray(aVoid);
                line += web_m.htmlHead(web_m.getCSS());
                ArrayList<String> as=getTreeVal(js);
                for(int j=0;j<as.size();j++){
                    String[] str=as.get(j).split(",");
                    line+="<div class='div draw_node m-top-1'>" +
                            "<div class='inline bg_r-trans text_bold'>" +
                            "กฏที่ "+(j+1)+" " +
                            "</div><br/>";
                    for(int i=0;i<str.length;i++){
                        if(str[i]!="") {
                            if(i<str.length-1){
                                line += "<div class='inline bg_r-primary'> " + str[i] + " </div>";
                                line += "->";
                            }else if(i<str.length){
                                String[] nstr=str[i].split(":");
                                line += "<div class='inline bg_r-primary'> " + nstr[0]+" </div> ";
                                line += " <b>:</b> <div class='inline bg_r-alert'> " + nstr[1]+" </div> ";
                            }
                        }
                    }
                    line+="</div>";
                }
                line += web_m.htmlFooter();
                webView.loadData(line, "text/html; charset='utf-8' ", null);
            } catch (Exception e) {
                Log.d("webview err", e.toString());
            }
        }

        private ArrayList<String> getTreeVal(JSONArray js) {
            ArrayList<String> as = new ArrayList();
            int tSize = getTreeSize(js);
            String[] nodeCount = new String[tSize];
            try {
                for (int node = 0; node < js.length(); node++) {
                    String str = "";
                    String[] mnode = js.getString(node).split("\\|");
                    Log.d("node",mnode[mnode.length - 1]);
                    nodeCount[mnode.length - 1] = mnode[mnode.length - 1];
                    for(int i=mnode.length;i<tSize;i++){
                       nodeCount[i]=null;
                    }
                    if (mnode[mnode.length - 1].split(":").length > 1) {
                        for (String v : nodeCount) {
                            if(v!=null) {
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
}
