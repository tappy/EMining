package com.example.hackme.emining;


import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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
import java.util.HashMap;
import java.util.List;


public class apriori_body_frag extends Fragment {

    private View rootview;
    private ListView listView;
    private SimpleAdapter adapter;
    private ArrayList arrayList;

    public static apriori_body_frag newInstance() {
        apriori_body_frag fragment = new apriori_body_frag();
        return fragment;
    }

    public apriori_body_frag() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootview=inflater.inflate(R.layout.fragment_apriori_body_frag, container, false);

        listView=(ListView)rootview.findViewById(R.id.apriori_body_listview);

        new loadSummary().execute(new database_manager(rootview.getContext()).getLoginId(),"body");

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

        private String getNumRuled(String val){
            return val.trim().split(" ")[0];
        }

        private String getPart1(String val){
            String[] mval=val.trim().split(" ");
            String line="";
            for(int i=1;i<mval.length;i++){
             line+=mval[i];
                if(i<(mval.length-2)) line+=" , ";
                else line+="  ";
            }
            return line;
        }

        @Override
        protected void onPostExecute(String aVoid) {
            //Toast.makeText(rootview.getContext(), aVoid, Toast.LENGTH_SHORT).show();
            try {
                JSONArray js = new JSONArray(aVoid);
                arrayList=new ArrayList();
                HashMap<String,Object> hm;
                for(int i=0;i<js.length();i++){
                    hm=new HashMap<>();
                    String[] sVal=js.getString(i).split("==>");
                    hm.put("numRuled","กฏที่ "+getNumRuled(sVal[0]).replace(".",""));
                    hm.put("val1",getPart1(sVal[0]).trim()+" ==> "+sVal[1].trim());
                    arrayList.add(hm);
                }
                String[] from=new String[]{
                        "numRuled",
                        "val1"
                };
                int[] to=new int[]{
                        R.id.numRuled,
                        R.id.part1
                };
                int layout=R.layout.apriori_body_layout_list;
                adapter=new SimpleAdapter(rootview.getContext(),arrayList,layout,from,to);
                listView.setAdapter(adapter);

            } catch (Exception e) {
                Log.d("Create listview err", e.toString());
            }
        }

    }
}
