package com.example.hackme.emining.ui.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import com.example.hackme.emining.entities.DeleteTableReq;
import com.example.hackme.emining.entities.LoadFilesNameReq;
import com.example.hackme.emining.entities.TableLoaderReq;
import com.example.hackme.emining.model.AddDataLoader;
import com.example.hackme.emining.model.DatabaseManager;
import com.example.hackme.emining.model.DeleteTableLoader;
import com.example.hackme.emining.model.LoadFilesName;
import com.example.hackme.emining.model.ModelLoader;
import com.example.hackme.emining.model.TablesLoader;
import com.example.hackme.emining.model.UpdateDataLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;


public class DataManagerFragment extends Fragment {

    private final int FILE_SELECT_CODE = 0;
    private DatabaseManager dbms;
    private ListView datamanagerlistview;
    int fir = 0, las = 0;
    private View rootView;
    private ImageButton uploadFile;
    public ProgressBar progressBar;
    public ArrayList arrayList;
    private boolean stuUpdate;
    private AlertDialog.Builder listAction;
    private HashMap updateHm;
    private SimpleAdapter simpleAdapter;
    private ImageButton imgbtn;
    private Animation refnim;
    private listAdapter listA;
    private HashMap<String, Object> hm;
    private ArrayList as;
    private AlertDialog dlg;

    public static DataManagerFragment newInstance() {
        return new DataManagerFragment();
    }

    public DataManagerFragment() {
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
                loadData();
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
        if (!InternetConnection.isNetworkAvailable(rootView.getContext())) {
            aBuilder.show();
        } else {
            if (dbms.existUser()) {
                loadData();
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
                                    uploadFile.setVisibility(View.VISIBLE);
                                    Animation animation = AnimationUtils.loadAnimation(rootView.getContext(), R.anim.showing);
                                    uploadFile.startAnimation(animation);
                                }
                            } else if (fir > las) {
                                if (uploadFile.isShown()) {
                                    uploadFile.setVisibility(View.INVISIBLE);
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

                        data_list_progress.setVisibility(View.VISIBLE);
                        LoadFilesNameReq req = new LoadFilesNameReq();
                        req.userID = dbms.getLoginId();
                        req.tableName = updateHm.get("table_name").toString();
                        new LoadFilesName(req, new ModelLoader.DataLoadingListener() {
                            @Override
                            public void onLoaded(final String data) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        data_list_progress.setVisibility(View.INVISIBLE);
                                        try {
                                            JSONArray js = new JSONArray(data);
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
                                });
                            }

                            @Override
                            public void onFailed(String message) {

                            }
                        });
                    }
                });

            } else if (data.trim().equals("false")) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(rootView.getContext(), "Don't have file uploaded", Toast.LENGTH_LONG).show();
                try {
                    arrayList.clear();
                    simpleAdapter.notifyDataSetChanged();
                } catch (NullPointerException e) {

                }
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

                            DeleteTableReq req = new DeleteTableReq();
                            req.fileName = hm.get("file_name").toString();
                            req.idUpload = hm.get("id_upload").toString();
                            req.uploadTable = updateHm.get("table_name").toString();
                            req.userid = new DatabaseManager(rootView.getContext()).getLoginId();
                            new DeleteTableLoader(req, new ModelLoader.DataLoadingListener() {
                                @Override
                                public void onLoaded(final String data) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                JSONObject jsonObject = new JSONObject(data);
                                                if (jsonObject.getInt("status") == 1) {
                                                    as.remove(position);
                                                    listA.notifyDataSetChanged();
                                                    loadData();
                                                    if (jsonObject.getInt("drop") == 1) {
                                                        dlg.dismiss();
                                                    }
                                                } else {
                                                    showDialog(getString(R.string.missing), getString(R.string.del_failed), getString(R.string.closeBtn));
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onFailed(String message) {

                                }
                            });
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

    public void loadData() {
        progressBar.setVisibility(View.VISIBLE);
        imgbtn.startAnimation(refnim);
        TableLoaderReq req = new TableLoaderReq();
        req.userId = new DatabaseManager(rootView.getContext()).getLoginId();
        new TablesLoader(req, new ModelLoader.DataLoadingListener() {
            @Override
            public void onLoaded(final String data) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.INVISIBLE);
                        loadListView(data);
                    }
                });
            }

            @Override
            public void onFailed(String message) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.INVISIBLE);
                        simpleDialog(getString(R.string.alert), getString(R.string.update_failed), ContextCompat.getDrawable(getContext(), android.R.drawable.ic_dialog_alert));
                    }
                });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            switch (requestCode) {
                case FILE_SELECT_CODE:
                    if (resultCode == Activity.RESULT_OK) {
                        Uri uri = data.getData();
                        File file = new File(getRealPathFromURI(uri));
                        String[] ftype = file.getName().split("\\.");
                        String typ = ftype[ftype.length - 1];

                        if (typ.equals("csv") || typ.equals("arff")) {
                            if (stuUpdate) {
                                updateTable(file);
                            } else {
                                uploadNewFile(file);
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
            simpleDialog(getString(R.string.alert), getString(R.string.upload_failed) + "\n กรุณาเลือกไหล์ด้วยด้วยแอพจัดการไฟล์", ContextCompat.getDrawable(getActivity(), android.R.drawable.ic_dialog_alert));
            Log.e("Error upload File", e.toString());
        }
    }

    public void updateTable(File file) {
        new UpdateDataLoader(file, dbms.getLoginId(), updateHm.get("table_name").toString(), new ModelLoader.DataLoadingListener() {
            @Override
            public void onLoaded(String data) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            loadData();
                        } catch (Exception e) {
                            simpleDialog(getString(R.string.alert), getString(R.string.update_failed), getResources().getDrawable(android.R.drawable.ic_dialog_alert));
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void onFailed(String message) {

            }
        });
    }

    public void uploadNewFile(File file) {
        new AddDataLoader(file, dbms.getLoginId(), new ModelLoader.DataLoadingListener() {
            @Override
            public void onLoaded(final String data) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jso = new JSONObject(data);
                            if (jso.getInt("StatusID") == 1) {
                                loadData();
                            } else {
                                loadData();
                                simpleDialog(getString(R.string.alert), getString(R.string.upload_failed), ContextCompat.getDrawable(getActivity(), android.R.drawable.ic_dialog_alert));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            simpleDialog(getString(R.string.alert), getString(R.string.upload_failed), ContextCompat.getDrawable(getActivity(), android.R.drawable.ic_dialog_alert));
                        }
                    }
                });
            }

            @Override
            public void onFailed(String message) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        simpleDialog(getString(R.string.alert), getString(R.string.upload_failed), ContextCompat.getDrawable(getActivity(), android.R.drawable.ic_dialog_alert));
                    }
                });
            }
        });
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getActivity().getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
            result = cursor.getString(idx);
        }
        cursor.close();
        return result;
    }
}
