package com.example.hackme.emining.model;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hackme.emining.Helpers.InternetConnection;
import com.example.hackme.emining.R;
import com.example.hackme.emining.Helpers.WebServiceConfig;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class DataManager extends Fragment {

    private final int FILE_SELECT_CODE = 0;
    private DatabaseManager dbms;
    private ListView datamanagerlistview;
    int fir = 0, las = 0;
    private View rootView;
    private ImageButton uploadFile;
    public ProgressBar progressBar;
    public ProgressDialog progressDialog;
    public ArrayList arrayList;
    private boolean stuUpdate;
    private AlertDialog.Builder listAction;
    private HashMap<String, Object> updateHm;
    private SimpleAdapter simpleAdapter;
    private ImageButton imgbtn;
    private Animation refnim;
    private listAdapter listA;
    private HashMap<String, Object> hm;
    private ArrayList as;
    private AlertDialog dlg;

    public static DataManager newInstance() {
        DataManager data_fragment = new DataManager();
        return data_fragment;
    }

    public DataManager() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_data_manager_page, container, false);
        dbms = new DatabaseManager(getActivity().getBaseContext());
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        imgbtn = (ImageButton) rootView.findViewById(R.id.refreshData);
        refnim = AnimationUtils.loadAnimation(rootView.getContext(), R.anim.rotate_view);
        refnim.setRepeatCount(Animation.INFINITE);
        imgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new loadData().execute(new DatabaseManager(rootView.getContext()).getLoginId());
            }
        });
        uploadFile = (ImageButton) rootView.findViewById(R.id.uploadBtn);
        uploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stuUpdate = false;
                filePickDlg(getString(R.string.select_file_upload));
            }
        });
        AlertDialog.Builder aBuilder = showDialog(getString(R.string.network), getString(R.string.check_network), getString(R.string.closeBtn));
        if (!new InternetConnection().isNetworkAvailable(rootView.getContext())) {
            aBuilder.show();
        } else {
            if (dbms.existUser()) {
                new loadData().execute(dbms.getLoginId());
            }
        }
        super.onResume();
        return rootView;
    }

    private void simpleDialog(String title, String message, Drawable icon) {
        new AlertDialog.Builder(rootView.getContext())
                .setTitle(title)
                .setMessage(message)
                .setIcon(icon)
                .setPositiveButton(getString(R.string.closeBtn), null)
                .setCancelable(true)
                .show();
    }

    private void filePickDlg(String titleText) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, titleText), 0);
    }

    private AlertDialog.Builder showDialog(String... set) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(rootView.getContext());
        alertDialog.setTitle(set[0]);
        alertDialog.setMessage(set[1]);
        alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
        alertDialog.setNegativeButton(set[2], null);
        alertDialog.setCancelable(true);
        return alertDialog;
    }

    public void loadListView(final String data) {
        datamanagerlistview = (ListView) rootView.findViewById(R.id.datamanagerlistView);
        try {
            if (!data.equals("false")) {
                JSONArray jsonArray = new JSONArray(data);
                JSONObject jsonObject;
                arrayList = new ArrayList();
                HashMap<String, Object> hm;
                for (int i = 0; i < jsonArray.length(); i++) {
                    hm = new HashMap<>();
                    jsonObject = jsonArray.getJSONObject(i);
                    hm.put("table_name", jsonObject.getString("table_name"));
                    hm.put("num_row", jsonObject.getString("num_row"));
                    arrayList.add(hm);
                }

                String[] lfrom = new String[]{
                        "table_name",
                        "num_row"
                };

                int[] idview = new int[]{
                        R.id.list_table_name,
                        R.id.fileCount
                };

                int listFrag = R.layout.listview_tab;
                simpleAdapter = new SimpleAdapter(rootView.getContext(), arrayList, listFrag, lfrom, idview);
                datamanagerlistview.setAdapter(simpleAdapter);
                datamanagerlistview.setOnScrollListener(new AbsListView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {

                    }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                        if (view.getId() == datamanagerlistview.getId()) {

                            fir = datamanagerlistview.getFirstVisiblePosition();
                            if (fir < las) {
                                //Log.d("scroll", "scroll up");
                                if (!uploadFile.isShown()) {
                                    uploadFile.setVisibility(rootView.VISIBLE);
                                    Animation animation = AnimationUtils.loadAnimation(rootView.getContext(), R.anim.showing);
                                    uploadFile.startAnimation(animation);
                                }
                            } else if (fir > las) {
                                if (uploadFile.isShown()) {
                                    uploadFile.setVisibility(rootView.INVISIBLE);
                                    Animation animation = AnimationUtils.loadAnimation(rootView.getContext(), R.anim.hiddent);
                                    uploadFile.startAnimation(animation);
                                }
                            }
                            las = view.getFirstVisiblePosition();
                        }
                    }
                });

                datamanagerlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        updateHm = (HashMap) parent.getItemAtPosition(position);
                        final LayoutInflater layoutInflater = LayoutInflater.from(rootView.getContext());
                        final View view1 = layoutInflater.inflate(R.layout.data_list_action_resource, parent, false);
                        final ListView listView = (ListView) view1.findViewById(R.id.data_action_listView);
                        final ProgressBar data_list_progress = (ProgressBar) view1.findViewById(R.id.data_list_progress);
                        data_list_progress.setVisibility(View.INVISIBLE);
                        listAction = new AlertDialog.Builder(rootView.getContext());
                        listAction.setTitle(updateHm.get("table_name").toString());
                        listAction.setCancelable(true);
                        listAction.setView(view1);
                        listAction.setNeutralButton(getString(R.string.closeBtn), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        listAction.setNegativeButton("เพิ่มข้อมูล", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                stuUpdate = true;
                                filePickDlg(getString(R.string.select_file_update));
                            }
                        });
                        dlg = listAction.create();
                        dlg.show();
                        new AsyncTask<String, Void, String>() {

                            @Override
                            protected void onPreExecute() {
                                data_list_progress.setVisibility(View.VISIBLE);
                            }

                            @Override
                            protected String doInBackground(String... params) {
                                StringBuilder stringBuilder = new StringBuilder();
                                try {
                                    HttpClient client = new DefaultHttpClient();
                                    HttpPost httpPost = new HttpPost(new WebServiceConfig().getHost("loadFileName.php"));
                                    List<NameValuePair> params1 = new ArrayList<>();
                                    params1.add(new BasicNameValuePair("userID", params[0]));
                                    params1.add(new BasicNameValuePair("tableName", params[1]));
                                    httpPost.setEntity(new UrlEncodedFormEntity(params1));
                                    HttpResponse response = client.execute(httpPost);
                                    if (response.getStatusLine().getStatusCode() == 200) {
                                        InputStream inputStream = response.getEntity().getContent();
                                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                                        String line;
                                        while ((line = bufferedReader.readLine()) != null) {
                                            stringBuilder.append(line);
                                        }
                                    }
                                    return stringBuilder.toString();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    return null;
                                }
                            }

                            @Override
                            protected void onPostExecute(String s) {
                                data_list_progress.setVisibility(View.INVISIBLE);
                                Log.d("loadFIlwname", s);
                                try {
                                    JSONArray js = new JSONArray(s);
                                    JSONObject jsonObject1;
                                    as = new ArrayList();
                                    HashMap<String, Object> hp;
                                    for (int i = 0; i < js.length(); i++) {
                                        jsonObject1 = js.getJSONObject(i);
                                        hp = new HashMap<>();
                                        hp.put("file_name", jsonObject1.getString("file_name"));
                                        hp.put("id_upload", jsonObject1.getString("id_upload"));
                                        as.add(hp);
                                    }
                                    int id = R.layout.file_name_list;
                                    listA = new listAdapter(rootView.getContext(), id, as);
                                    listView.setAdapter(listA);

                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }.execute(new DatabaseManager(rootView.getContext()).getLoginId(), updateHm.get("table_name").toString());
                    }
                });

            } else if (data.trim().equals("false")) {
                Toast.makeText(rootView.getContext(), "Don't have file uploaded", Toast.LENGTH_LONG).show();
                arrayList.clear();
                simpleAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(rootView.getContext(), "Download list problem!", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public class listAdapter extends ArrayAdapter<HashMap<String, Object>> {

        public listAdapter(Context context, int resource) {
            super(context, resource);
        }

        public listAdapter(Context context, int resource, ArrayList items) {
            super(context, resource, items);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater li = LayoutInflater.from(getContext());
                v = li.inflate(R.layout.file_name_list, parent, false);
            }
            hm = getItem(position);
            TextView tv = (TextView) v.findViewById(R.id.textView20);
            tv.setText(hm.get("file_name").toString());
            ImageButton del = (ImageButton) v.findViewById(R.id.del_file);
            del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hm = getItem(position);
                    final AlertDialog.Builder aBuilder = new AlertDialog.Builder(rootView.getContext());
                    aBuilder.setTitle(getString(R.string.delete_file));
                    aBuilder.setMessage("คุณต้องการลบไฟล์ " + hm.get("file_name").toString() + " ใช่หรือไม่");
                    aBuilder.setCancelable(true);
                    aBuilder.setIcon(android.R.drawable.ic_dialog_alert);
                    aBuilder.setNegativeButton(getString(R.string.okBtn), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, int which) {

                            new AsyncTask<String, Void, String>() {
                                @Override
                                protected void onPreExecute() {
                                    super.onPreExecute();
                                }

                                @Override
                                protected String doInBackground(String... params) {
                                    StringBuilder builder = new StringBuilder();
                                    try {
                                        HttpClient client = new DefaultHttpClient();
                                        HttpPost httpPost = new HttpPost(new WebServiceConfig().getHost("deleteUploadList.php"));
                                        List<NameValuePair> params1 = new ArrayList<>();
                                        params1.add(new BasicNameValuePair("id_upload", params[0]));
                                        params1.add(new BasicNameValuePair("uploadTable", params[1]));
                                        params1.add(new BasicNameValuePair("fileName", params[2]));
                                        params1.add(new BasicNameValuePair("userid", params[3]));
                                        httpPost.setEntity(new UrlEncodedFormEntity(params1));
                                        HttpResponse response = client.execute(httpPost);
                                        if (response.getStatusLine().getStatusCode() == 200) {
                                            InputStream inputStream = response.getEntity().getContent();
                                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                                            String line;
                                            while ((line = bufferedReader.readLine()) != null) {
                                                builder.append(line);
                                            }
                                        }
                                        Log.d("respon", builder.toString());
                                        return builder.toString();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        return null;
                                    }
                                }

                                @Override
                                protected void onPostExecute(String s) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(s);
                                        if (jsonObject.getInt("status") == 1) {
                                            as.remove(position);
                                            listA.notifyDataSetChanged();
                                            new loadData().execute(new DatabaseManager(rootView.getContext()).getLoginId());
                                            if (jsonObject.getInt("drop") == 1) {//Toast.makeText(view1.getContext(), "drop=" + jsonObject.getInt("drop"), Toast.LENGTH_SHORT).show();
                                                dlg.dismiss();
                                            }
                                        } else {
                                            showDialog(getString(R.string.missing), getString(R.string.del_failed), getString(R.string.closeBtn));
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }.execute(hm.get("id_upload").toString(), updateHm.get("table_name").toString(), hm.get("file_name").toString(), new DatabaseManager(rootView.getContext()).getLoginId().toString());
                        }
                    });
                    aBuilder.setPositiveButton(getString(R.string.cancelBtn), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    aBuilder.show();
                }
            });
            return v;
        }
    }


    public class loadData extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            imgbtn.startAnimation(refnim);
        }

        @Override
        protected String doInBackground(String... params) {
            StringBuilder stringBuilder = new StringBuilder();
            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(new WebServiceConfig().getHost("loadListTable.php"));
                List<NameValuePair> params1 = new ArrayList<>();
                params1.add(new BasicNameValuePair("userID", params[0]));
                httpPost.setEntity(new UrlEncodedFormEntity(params1));
                HttpResponse response = client.execute(httpPost);
                if (response.getStatusLine().getStatusCode() == 200) {
                    InputStream inputStream = response.getEntity().getContent();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    return stringBuilder.toString();
                } else {
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String str) {
            progressBar.setVisibility(View.INVISIBLE);
            loadListView(str);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            switch (requestCode) {
                case FILE_SELECT_CODE:
                    if (resultCode == getActivity().RESULT_OK) {
                        Uri uri = data.getData();
                        File file = new File(getRealPathFromURI(uri));
                        String[] ftype = file.getName().split("\\.");
                        String typ = ftype[ftype.length - 1];

                        if (typ.equals("csv") || typ.equals("arff")) {
                            if (stuUpdate) {
                                new newUpdateFile().execute(file.getPath(), new WebServiceConfig().getHost("updateTable.php"), updateHm.get("table_name").toString());
                            } else {
                                new myNewUploadFile().execute(file.getPath(), new WebServiceConfig().getHost("uploadFile.php"));
                            }

                        } else {
                            AlertDialog.Builder a = new AlertDialog.Builder(rootView.getContext());
                            a.setTitle(getString(R.string.alert));
                            a.setIcon(android.R.drawable.ic_dialog_alert);
                            a.setMessage(getString(R.string.fileType_notSupport));
                            a.setCancelable(true);
                            a.setPositiveButton(getString(R.string.closeBtn), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (stuUpdate) {
                                        filePickDlg(getString(R.string.select_file_update));
                                    } else {
                                        filePickDlg(getString(R.string.select_file_upload));
                                    }
                                }
                            });
                            a.show();
                        }
                    }
            }
        } catch (Exception e) {
            //Toast.makeText(rootView.getContext(), getString(R.string.upload_failed) + e.toString(), Toast.LENGTH_SHORT).show();
            simpleDialog(getString(R.string.alert), getString(R.string.upload_failed) + "\n กรุณาเลือกไหล์ด้วยด้วยแอพจัดการไฟล์", getResources().getDrawable(android.R.drawable.ic_dialog_alert));
            Log.e("Error upload File", e.toString());
        }
    }


    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getActivity().getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
        }
        return result;
    }

    public class myNewUploadFile extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(rootView.getContext(), getString(R.string.upload), getString(R.string.upload_dialog), false, true);
            progressDialog.setIcon(android.R.drawable.stat_sys_upload);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                StringBuilder builder = new StringBuilder();
                String fileName = params[0];
                String serverURL = params[1];
                File file = new File(fileName);
                FileBody fb = new FileBody(file);
                StringBody user = new StringBody(dbms.getLoginId());
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(serverURL);

                MultipartEntity reqEntity = new MultipartEntity();
                reqEntity.addPart("filUpload", fb);
                reqEntity.addPart("userID", user);
                post.setEntity(reqEntity);
                HttpResponse response = client.execute(post);
                BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                String body;
                while ((body = rd.readLine()) != null) {
                    builder.append(body.toString());
                }
                return builder.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            progressDialog.setMessage(values[0]);
            if (values[1] == "1") {
                progressDialog.setIcon(android.R.drawable.stat_sys_upload_done);
            } else if (values[1] == "2") {
                progressDialog.setIcon(android.R.drawable.ic_delete);
            }
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                Log.d("respon uploas", s + "");
                progressDialog.dismiss();
                JSONObject jso = new JSONObject(s);
                if (jso.getInt("StatusID") == 1) {
                    new loadData().execute(dbms.getLoginId());
                } else {
                    new loadData().execute(dbms.getLoginId());
                    simpleDialog(getString(R.string.alert), getString(R.string.upload_failed), getResources().getDrawable(android.R.drawable.ic_dialog_alert));
                    Log.e("Error!", jso.getString("Error"));
                }
                Log.d("responMessage", s);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class newUpdateFile extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(rootView.getContext(), getString(R.string.update), getString(R.string.upload_dialog), false, true);
            progressDialog.setIcon(android.R.drawable.stat_sys_upload);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                StringBuilder builder = new StringBuilder();
                String fileName = params[0];
                String serverURL = params[1];
                String tableName = params[2];
                File file = new File(fileName);
                FileBody fb = new FileBody(file);
                StringBody user = new StringBody(dbms.getLoginId());
                StringBody tableNameBody = new StringBody(tableName);
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(serverURL);
                MultipartEntity reqEntity = new MultipartEntity();
                reqEntity.addPart("filUpload", fb);
                reqEntity.addPart("userID", user);
                reqEntity.addPart("tableName", tableNameBody);
                post.setEntity(reqEntity);
                HttpResponse response = client.execute(post);
                BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                String body;
                while ((body = rd.readLine()) != null) {
                    builder.append(body.toString());
                }
                return builder.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            progressDialog.setMessage(values[0]);
            if (values[1] == "1") {
                progressDialog.setIcon(android.R.drawable.stat_sys_upload_done);
            } else if (values[1] == "2") {
                progressDialog.setIcon(android.R.drawable.ic_delete);
            }
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            try {
                new loadData().execute(dbms.getLoginId());
            } catch (Exception e) {
                simpleDialog(getString(R.string.alert), getString(R.string.update_failed), getResources().getDrawable(android.R.drawable.ic_dialog_alert));
                e.printStackTrace();
            }
        }
    }

}
