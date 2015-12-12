package com.example.hackme.emining.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.hackme.emining.Helpers.StringHelper;
import com.example.hackme.emining.R;
import com.example.hackme.emining.entities.GetApioriModelReq;
import com.example.hackme.emining.model.DatabaseManager;
import com.example.hackme.emining.model.GetApioriModelLoader;
import com.example.hackme.emining.model.ModelLoader;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

public class AprioriBodyFragment extends Fragment {

    private View rootview;
    private ListView listView;
    private SimpleAdapter adapter;
    private ArrayList arrayList;

    public static AprioriBodyFragment newInstance() {
        AprioriBodyFragment fragment = new AprioriBodyFragment();
        return fragment;
    }

    public AprioriBodyFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootview = inflater.inflate(R.layout.fragment_apriori_body_frag, container, false);

        listView = (ListView) rootview.findViewById(R.id.apriori_body_listview);
        GetApioriModelReq req = new GetApioriModelReq();
        req.userId = new DatabaseManager(rootview.getContext()).getLoginId();
        req.param = "body";
        new GetApioriModelLoader(req, new ModelLoader.DataLoadingListener() {
            @Override
            public void onLoaded(final String data) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONArray js = new JSONArray(data);
                            arrayList = new ArrayList();
                            HashMap<String, Object> hm;
                            for (int i = 0; i < js.length(); i++) {
                                hm = new HashMap<>();
                                String[] sVal = js.getString(i).split("==>");
                                hm.put("numRuled", "กฏที่ " + StringHelper.getNumRuled(sVal[0]).replace(".", ""));
                                hm.put("val1", StringHelper.getPart1(sVal[0]).trim() + " ==> " + sVal[1].trim());
                                arrayList.add(hm);
                            }
                            String[] from = new String[]{
                                    "numRuled",
                                    "val1"
                            };
                            int[] to = new int[]{
                                    R.id.numRuled,
                                    R.id.part1
                            };
                            int layout = R.layout.apriori_body_layout_list;
                            adapter = new SimpleAdapter(rootview.getContext(), arrayList, layout, from, to);
                            listView.setAdapter(adapter);

                        } catch (Exception e) {
                            Log.d("Create listview err", e.toString());
                        }
                    }
                });
            }

            @Override
            public void onFailed(String message) {

            }
        });

        return rootview;
    }

}
