package com.example.hackme.emining.ui.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.example.hackme.emining.R;
import com.example.hackme.emining.ui.activities.TreeModelView;
import com.example.hackme.emining.Helpers.WebServiceConfig;
import com.example.hackme.emining.model.DatabaseManager;
import com.example.hackme.emining.model.LoadDropDownData;
import com.example.hackme.emining.ui.activities.AprioriModelView;
import com.example.hackme.emining.ui.activities.ClusterModelView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ViewModelFragment extends Fragment {

    public View rooview;
    public Spinner spinner, spinner2, spinner3;
    public ProgressBar progressBardropdown, progressBardropdown2, progressBardropdown3;
    private ProgressDialog progressDialog;
    public ArrayList arrayList;
    public HashMap<String, Object> hashMap;
    public JSONArray jsonArray;
    public JSONObject jsonObject;
    public SpinnerAdapter spinnerAdapter;
    private EditText numCluster;
    private EditText iteria, seed;
    private Switch switcher;
    private String missing;
    private EditText apri0, apri1, apri2, apri3, apri4, apri5, apri6;
    private EditText tree_2_confidentFactor, tree_4_minNumObj, tree_5_numFolds, tree_8_treeSeed;
    private Switch tree_1_binarySplit, tree_6_reduceErrorPuning, tree_9_subTree, tree_10_unPruned, tree_11_useLaplace;
    private TabHost tabHost;

    public static ViewModelFragment newInstance() {
        return new ViewModelFragment();
    }

    public ViewModelFragment() {
        // Required empty public constructor
    }

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible) {
            new loadDropDownTable().execute(new DatabaseManager(rooview.getContext()).getLoginId());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        rooview = inflater.inflate(R.layout.fragment_analysys_model_page, container, false);

        tabHost = (TabHost) rooview.findViewById(R.id.tabHost);
        tabHost.setup();
        tabHost.addTab(tabHost.newTabSpec("Clustering").setIndicator("การจัดกลุ่ม").setContent(R.id.tab1));
        tabHost.addTab(tabHost.newTabSpec("Classification").setIndicator("การจำแนกข้อมูล").setContent(R.id.tab2));
        tabHost.addTab(tabHost.newTabSpec("Association").setIndicator("กฎความสัมพันธ์").setContent(R.id.tab3));
        tabHost.setCurrentTab(0);

        TabWidget widget = tabHost.getTabWidget();
        for (int i = 0; i < widget.getChildCount(); i++) {
            View v = widget.getChildAt(i);
            TextView tv = (TextView) v.findViewById(android.R.id.title);
            tv.setTextSize(14);
            tv.setSingleLine(true);
            tv.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            if (tv == null) {
                continue;
            }
            v.setBackgroundResource(R.drawable.tabhost_bg);
            tv.setTextColor(getResources().getColor(R.color.text_color));
        }

        Switch sw = (Switch) rooview.findViewById(R.id.replaceMN);
        sw.setSelected(true);
        sw.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        sw.setMarqueeRepeatLimit(100);

        iteria = (EditText) rooview.findViewById(R.id.iteration);
        seed = (EditText) rooview.findViewById(R.id.tree_8_treeSeed);
        switcher = (Switch) rooview.findViewById(R.id.replaceMN);

        apri0 = (EditText) rooview.findViewById(R.id.classIndex);
        apri1 = (EditText) rooview.findViewById(R.id.delta);
        apri2 = (EditText) rooview.findViewById(R.id.lowerBmin);
        apri3 = (EditText) rooview.findViewById(R.id.minMet);
        apri4 = (EditText) rooview.findViewById(R.id.numRu);
        apri5 = (EditText) rooview.findViewById(R.id.significanceL);
        apri6 = (EditText) rooview.findViewById(R.id.upperBmin);

        tree_1_binarySplit = (Switch) rooview.findViewById(R.id.tree_1_binarySplit);
        tree_2_confidentFactor = (EditText) rooview.findViewById(R.id.tree_2_confidentFactor);
        tree_4_minNumObj = (EditText) rooview.findViewById(R.id.tree_4_minNumObj);
        tree_5_numFolds = (EditText) rooview.findViewById(R.id.tree_5_numFolds);
        tree_6_reduceErrorPuning = (Switch) rooview.findViewById(R.id.tree_6_reduceErrorPuning);
        tree_8_treeSeed = (EditText) rooview.findViewById(R.id.tree_8_treeSeed);
        tree_9_subTree = (Switch) rooview.findViewById(R.id.tree_9_subTree);
        tree_9_subTree.setChecked(true);
        tree_10_unPruned = (Switch) rooview.findViewById(R.id.tree_10_unPruned);
        tree_11_useLaplace = (Switch) rooview.findViewById(R.id.tree_11_useLaplace);

        tree_6_reduceErrorPuning.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) tree_10_unPruned.setChecked(false);
            }
        });

        tree_10_unPruned.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) tree_6_reduceErrorPuning.setChecked(false);
            }
        });

        numCluster = (EditText) rooview.findViewById(R.id.numCluster);

        spinner = (Spinner) rooview.findViewById(R.id.spinner);
        spinner2 = (Spinner) rooview.findViewById(R.id.spinner2);
        spinner3 = (Spinner) rooview.findViewById(R.id.spinner3);

        jsonArray = new JSONArray();
        progressBardropdown = (ProgressBar) rooview.findViewById(R.id.progressBardropdown);
        progressBardropdown2 = (ProgressBar) rooview.findViewById(R.id.progressBar2);
        progressBardropdown3 = (ProgressBar) rooview.findViewById(R.id.progressBar3);

        ImageButton clusterAnaBtn = (ImageButton) rooview.findViewById(R.id.imageButton2);
        clusterAnaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    HashMap hm;
                    int algorithm = tabHost.getCurrentTab();
                    switch (algorithm) {
                        case 0:
                            hm = (HashMap) spinner.getSelectedItem();
                            if (!hm.get("table").toString().equals("ไม่มีข้อมูล")) {
                                Log.d("select", hm.get("table").toString());
                                if (switcher.isChecked()) {
                                    missing = "";
                                } else {
                                    missing = "-M";
                                }
                                new loadAnalysys().execute(
                                        hm.get("table").toString(),
                                        String.valueOf(algorithm),
                                        new DatabaseManager(rooview.getContext()).getLoginId(),
                                        numCluster.getText().toString(),
                                        iteria.getText().toString(), seed.getText().toString(),
                                        missing);
                            }
                            break;
                        case 1:
                            hm = (HashMap) spinner2.getSelectedItem();
                            if (!hm.get("table").toString().equals("ไม่มีข้อมูล")) {
                                new loadAnalysys().execute(
                                        hm.get("table").toString(),
                                        String.valueOf(algorithm),
                                        new DatabaseManager(rooview.getContext()).getLoginId(),
                                        getSwitch(tree_1_binarySplit, " -B "),
                                        getConfidentFactor(" -C " + tree_2_confidentFactor.getText().toString()),
                                        " -M " + tree_4_minNumObj.getText().toString(),
                                        getNumFold(" -N " + tree_5_numFolds.getText().toString()),
                                        getSwitch(tree_6_reduceErrorPuning, " -R "),
                                        getSeed(" -Q " + tree_8_treeSeed.getText().toString()),
                                        getSubtree(),
                                        getSwitch(tree_10_unPruned, " -U "),
                                        getSwitch(tree_11_useLaplace, " -A "));
                            }
                            break;
                        case 2:
                            hm = (HashMap) spinner3.getSelectedItem();
                            if (!hm.get("table").toString().equals("ไม่มีข้อมูล")) {
                                new loadAnalysys().execute(
                                        hm.get("table").toString(),
                                        String.valueOf(algorithm),
                                        new DatabaseManager(rooview.getContext()).getLoginId(),
                                        apri0.getText().toString(),
                                        apri1.getText().toString(),
                                        apri2.getText().toString(),
                                        apri3.getText().toString(),
                                        apri4.getText().toString(),
                                        apri5.getText().toString(),
                                        apri6.getText().toString());
                            }
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        new loadDropDownTable().execute(new DatabaseManager(rooview.getContext()).getLoginId());
        return rooview;
    }

    private String getSwitch(Switch sw, String rtext) {
        if (sw.isChecked()) {
            return rtext;
        } else {
            return "";
        }
    }

    private String inverseGetSwitch(Switch sw, String rtext) {
        if (sw.isChecked()) {
            return "";
        } else {
            return rtext;
        }
    }

    private String getNumFold(String numfold) {
        if (tree_6_reduceErrorPuning.isChecked()) {
            return numfold;
        } else {
            return "";
        }
    }

    private String getSubtree() {
        if (!inverseGetSwitch(tree_9_subTree, " -S ").equals("")) {
            if (tree_10_unPruned.isChecked()) return "";
            else return " -S ";
        }
        return "";
    }

    private String getSeed(String seed) {
        if (tree_6_reduceErrorPuning.isChecked()) {
            return seed;
        } else {
            return "";
        }
    }

    private String getConfidentFactor(String cfdf) {
        if (tree_6_reduceErrorPuning.isChecked() || tree_10_unPruned.isChecked()) {
            return "";
        } else {
            return cfdf;
        }
    }

    public void numberPickerDialog(int min, int max, String defVal) {
        final String[] listnum = new String[max];
        for (int i = 0; i < listnum.length; i++) {
            listnum[i] = String.valueOf((i + 1) + min);
        }
        final NumberPicker num = new NumberPicker(rooview.getContext());
        num.setMinValue(1);
        num.setMaxValue(listnum.length);
        num.setDisplayedValues(listnum);
        num.setValue(Integer.parseInt(defVal));

        AlertDialog.Builder dialog = new AlertDialog.Builder(rooview.getContext());
        dialog.setTitle("Number picker");
        dialog.setView(num);
        dialog.setPositiveButton(getString(R.string.okBtn), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                numCluster.setText(String.valueOf(num.getValue()));
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    class loadAnalysys extends AsyncTask<String, Void, String[]> {

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(rooview.getContext(), getString(R.string.processing), getString(R.string.please_wait), false, true);
        }

        @Override
        protected String[] doInBackground(String... params) {
            StringBuilder builder = new StringBuilder();
            try {
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                HttpPost post = new HttpPost(new WebServiceConfig().getHost("analysysModel.php"));
                HttpClient client = new DefaultHttpClient();
                List<NameValuePair> list = new ArrayList<>();

                list.add(new BasicNameValuePair("tableName", params[0]));
                list.add(new BasicNameValuePair("algorithm", params[1]));
                list.add(new BasicNameValuePair("userid", params[2]));

                switch (params[1]) {
                    case "0":
                        list.add(new BasicNameValuePair("class_count", params[3]));
                        list.add(new BasicNameValuePair("max_iteria", params[4]));
                        list.add(new BasicNameValuePair("seed", params[5]));
                        list.add(new BasicNameValuePair("missing_value", params[6]));
                        break;
                    case "1":
                        list.add(new BasicNameValuePair("binarySplit", params[3]));
                        list.add(new BasicNameValuePair("confidentFactor", params[4]));
                        list.add(new BasicNameValuePair("minNumObj", params[5]));
                        list.add(new BasicNameValuePair("numFolds", params[6]));
                        list.add(new BasicNameValuePair("reduceErrorPuning", params[7]));
                        list.add(new BasicNameValuePair("treeSeed", params[8]));
                        list.add(new BasicNameValuePair("subTree", params[9]));
                        list.add(new BasicNameValuePair("unPruned", params[10]));
                        list.add(new BasicNameValuePair("useLaplace", params[11]));

                        break;
                    case "2":
                        list.add(new BasicNameValuePair("classindex", params[3]));
                        list.add(new BasicNameValuePair("delta", params[4]));
                        list.add(new BasicNameValuePair("lowerBoundMinSupport", params[5]));
                        list.add(new BasicNameValuePair("minMetric", params[6]));
                        list.add(new BasicNameValuePair("numRules", params[7]));
                        list.add(new BasicNameValuePair("significanceLevel", params[8]));
                        list.add(new BasicNameValuePair("upperBoundMinSupport", params[9]));
                        break;
                }

                post.setEntity(new UrlEncodedFormEntity(list));
                HttpResponse response = client.execute(post);
                if (response.getStatusLine().getStatusCode() == 200) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        builder.append(line);
                    }
                } else {
                    builder.append("0");
                }
                String[] rval = new String[2];
                rval[0] = params[1];
                rval[1] = builder.toString();
                return rval;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] s) {
            progressDialog.dismiss();
            try {
                JSONObject jsonObject1 = new JSONObject(s[1]);
                if (jsonObject1.getInt("model") == 1) {
                    if (jsonObject1.getInt("algorithm") == 0) {
                        Intent cluster = new Intent(rooview.getContext(), ClusterModelView.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("valueModel","");
                        bundle.putInt("class_count", Integer.parseInt(numCluster.getText().toString()));
                        cluster.putExtras(bundle);
                        startActivity(cluster);

                    } else if (jsonObject1.getInt("algorithm") == 1) {

                        Intent cluster = new Intent(rooview.getContext(), TreeModelView.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("valueModel","");
                        cluster.putExtras(bundle);
                        startActivity(cluster);

                    } else if (jsonObject1.getInt("algorithm") == 2) {
                        Intent cluster = new Intent(rooview.getContext(), AprioriModelView.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("valueModel","");
                        cluster.putExtras(bundle);
                        startActivity(cluster);

                    }
                } else if (jsonObject1.getInt("model") == 2) {
                    AlertDialog.Builder al = new AlertDialog.Builder(rooview.getContext());
                    al.setTitle("Error!");
                    al.setIcon(android.R.drawable.ic_dialog_alert);
                    switch (s[0]) {
                        case "0":
                            al.setMessage(getString(R.string.file_not_sup) + " Simple KMeans");
                            break;
                        case "1":
                            al.setMessage(getString(R.string.file_not_sup) + " J48");
                            break;
                        case "2":
                            al.setMessage(getString(R.string.file_not_sup) + " Apriori");
                            break;
                    }
                    al.setPositiveButton(getString(R.string.closeBtn), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    al.setCancelable(true);
                    Dialog d = al.create();
                    d.show();
                } else {
                    AlertDialog.Builder al = new AlertDialog.Builder(rooview.getContext());
                    al.setTitle("Error!");
                    al.setIcon(android.R.drawable.ic_dialog_alert);
                    al.setMessage(getString(R.string.ana_err));
                    al.setPositiveButton(getString(R.string.closeBtn), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    al.setCancelable(true);
                    Dialog d = al.create();
                    d.show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class loadDropDownTable extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            progressBardropdown.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                return new LoadDropDownData().loading(new DatabaseManager(getActivity().getApplicationContext()).getLoginId());
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            progressBardropdown.setVisibility(View.INVISIBLE);
            creatSpiner(s);
        }
    }

    public void creatSpiner(String s) {
        try {
            Log.d("Load", "Load table list");
            arrayList = new ArrayList();
            jsonArray = new JSONArray(s);
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                hashMap = new HashMap<>();
                if (jsonObject.getString("table_name") == "0") {
                    hashMap.put("table", "ไม่มีข้อมูล");
                } else {
                    hashMap.put("table", jsonObject.getString("table_name"));
                }
                arrayList.add(hashMap);
            }

            String[] datafrom = new String[]{
                    "table",
            };
            int[] valueTo = new int[]{
                    R.id.dropdown_textView
            };
            int idList = R.layout.dropdown_list;
            spinnerAdapter = new SimpleAdapter(rooview.getContext(), arrayList, idList, datafrom, valueTo);
            spinner.setAdapter(spinnerAdapter);
            spinner2.setAdapter(spinnerAdapter);
            spinner3.setAdapter(spinnerAdapter);

            spinner.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    new loadDropDownTable().execute(new DatabaseManager(rooview.getContext()).getLoginId());
                    return false;
                }
            });

            spinner2.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    new loadDropDownTable().execute(new DatabaseManager(rooview.getContext()).getLoginId());
                    return false;
                }
            });

            spinner3.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    new loadDropDownTable().execute(new DatabaseManager(rooview.getContext()).getLoginId());
                    return false;
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
