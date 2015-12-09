package com.example.hackme.emining.ui.fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.hackme.emining.R;
import com.example.hackme.emining.model.DatabaseManager;
import com.example.hackme.emining.Helpers.WebServiceConfig;

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

public class ClusterBodyFragment extends Fragment {
    private View rootView;
    private ListView listView;

    public static ClusterBodyFragment newInstance(String param, int col) {
        ClusterBodyFragment fragment = new ClusterBodyFragment();
        Bundle bundle = new Bundle();
        bundle.putString("param", param);
        bundle.putInt("col", col);
        fragment.setArguments(bundle);
        return fragment;
    }

    public ClusterBodyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_cluster_body, container, false);
        if (getArguments() != null) {
            Log.d("getArg", getArguments().getString("param"));
            loadClusterContent(getArguments().getString("param"), getArguments().getInt("col"));
        } else {
            Log.d("getArgNull", "ArgNull");
        }
        return rootView;
    }

    public void loadClusterContent(final String loadParam, final int col) {
        new AsyncTask<String, Void, String[]>() {
            ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                progressDialog = ProgressDialog.show(rootView.getContext(), "Loading", "Loading content...", false, true);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setIcon(android.R.drawable.stat_sys_download);
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
                    Log.d("res", builder.toString());
                    String[] ret = {builder.toString(), params[0]};
                    return ret;
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String[] s) {
                progressDialog.dismiss();
                try {
                    if (s[1].equals("body")) {

                        listView = (ListView) rootView.findViewById(R.id.listView);
                        JSONArray js = new JSONArray(s[0]);

                        HashMap<String, Object> hm;
                        ArrayList arrayList = new ArrayList();

                        for (int i = 0; i < js.length(); i++) {
                            JSONArray js2 = new JSONArray(js.getString(i));
                            hm = new HashMap<>();
                            hm.put("v1", js2.getString(0));
                            hm.put("v2", js2.getString(col));
                            arrayList.add(hm);

                        }
                        String[] from = new String[]{"v1", "v2"};
                        int[] to = new int[]{R.id.textView3, R.id.textView4};
                        int id = R.layout.cluster_list;
                        SimpleAdapter simpleAdapter = new SimpleAdapter(rootView.getContext(), arrayList, id, from, to);
                        listView.setAdapter(simpleAdapter);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        }.execute(loadParam, new DatabaseManager(rootView.getContext()).getLoginId());
    }
}
