package com.example.hackme.emining.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.hackme.emining.R;
import com.example.hackme.emining.entities.GetClusterModelReq;
import com.example.hackme.emining.model.DatabaseManager;
import com.example.hackme.emining.model.GetClusterModelLoader;
import com.example.hackme.emining.model.ModelLoader;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

public class ClusterBodyFragment extends Fragment {
    private View rootView;
    private ListView listView;
    private static String COL = "COL";
    private static String PARAM = "PARAM";
    private String param;
    private int col;

    public static ClusterBodyFragment newInstance(String param, int col) {
        ClusterBodyFragment fragment = new ClusterBodyFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PARAM, param);
        bundle.putInt(COL, col);
        fragment.setArguments(bundle);
        return fragment;
    }

    public ClusterBodyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            param = getArguments().getString(PARAM);
            col = getArguments().getInt(COL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_cluster_body, container, false);

        GetClusterModelReq req = new GetClusterModelReq();
        req.userId = new DatabaseManager(rootView.getContext()).getLoginId();
        req.param = param;
        getClustermodel(req);
        return rootView;
    }

    public void getClustermodel(GetClusterModelReq req) {
        new GetClusterModelLoader(req, new ModelLoader.DataLoadingListener() {
            @Override
            public void onLoaded(String data) {
                try {
                    if (param.equals("body")) {

                        listView = (ListView) rootView.findViewById(R.id.listView);
                        JSONArray js = new JSONArray(data);

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

            @Override
            public void onFailed(String message) {

            }
        });
    }

}
