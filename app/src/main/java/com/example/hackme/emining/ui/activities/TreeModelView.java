package com.example.hackme.emining.ui.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.hackme.emining.R;
import com.example.hackme.emining.Helpers.WebServiceConfig;
import com.example.hackme.emining.model.DatabaseManager;
import com.example.hackme.emining.ui.fragments.TreeBodyFragment;
import com.example.hackme.emining.ui.fragments.TreeSummaryFragment;
import com.example.hackme.emining.ui.fragments.TreeTotalSummary;

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


public class TreeModelView extends Activity implements ActionBar.TabListener {

    private ActionBar myActionBar;
    private SectionsPagerAdapter sectionsPagerAdapter;
    private ViewPager pager;
    private JSONArray jsModel;
    private String val = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tree_model_view);

        myActionBar = getActionBar();
        myActionBar.setDisplayHomeAsUpEnabled(true);
        myActionBar.setNavigationMode(myActionBar.NAVIGATION_MODE_TABS);
        myActionBar.setStackedBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_color));

        Bundle bundle = getIntent().getExtras();
        if (bundle.getString("valueModel").equals("")) {

            sectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
            pager = (ViewPager) findViewById(R.id.pager);
            pager.setAdapter(sectionsPagerAdapter);
            pager.setOffscreenPageLimit(3);

            pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    myActionBar.setSelectedNavigationItem(position);
                }
            });

            for (int i = 0; i < sectionsPagerAdapter.getCount(); i++) {
                myActionBar.addTab(myActionBar.newTab().setText(sectionsPagerAdapter.getPageTitle(i)).setTabListener(this));
            }

        } else {
            //localfile
        }
    }

    public void loadTreeContent() {
        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... params) {
                return loadData(params);
            }

            @Override
            protected void onPostExecute(String s) {

                try {
                    jsModel = new JSONArray(s);
                    String val = "";
                    for (int i = 0; i < jsModel.length() - 1; i++) {
                        val += jsModel.getString(i);
                    }
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, val);
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.execute("full_data", new DatabaseManager(getBaseContext()).getLoginId());
    }

    public void loadTreeContentforSave() {
        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... params) {
                return loadData(params);
            }

            @Override
            protected void onPostExecute(String s) {

                try {
                    jsModel = new JSONArray(s);
                    for (int i = 0; i < jsModel.length() - 1; i++) {
                        val += jsModel.getString(i);
                    }
                    new SaveModelFile(TreeModelView.this, val).setFileName(1);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        }.execute("full_data", new DatabaseManager(getBaseContext()).getLoginId());
    }

    private void saveTree() {
        new SaveModelFile(TreeModelView.this, TreeBodyFragment.line, "html").setFileName(1);
    }

    private String loadData(String... params) {
        try {
            StringBuilder builder = new StringBuilder();
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(new WebServiceConfig().getHost("getTreeModel.php"));
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("param", params[0]));
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
            String ret = builder.toString();
            return ret;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tree_model_view, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        } else if (id == R.id.action_saveTreeModel) {
            loadTreeContent();
        } else if (id == R.id.action_saveFileTreeModel) {
            AlertDialog.Builder als = new AlertDialog.Builder(this);
            als.setTitle(getString(R.string.alert));
            als.setMessage("เลือกรูปแบบการบันทึก");
            als.setNegativeButton("บันทึกโมเดล", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    loadTreeContentforSave();
                }
            });
            als.setNeutralButton("บันทึกกฎ", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    saveTree();
                }
            });
            als.setPositiveButton(getString(R.string.cancelBtn), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    removeDialog(0);
                }
            });
            als.setCancelable(true);
            als.show();
        }
        return true;
    }


    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        pager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return TreeSummaryFragment.newInstance();
                case 1:
                    return TreeTotalSummary.newInstance();
                default:
                    return TreeBodyFragment.newInstance();
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "รายละเอียด";
                case 1:
                    return "ข้อมูลสรุป";
                case 2:
                    return "กฎทั้งหมด";
                default:
                    return "";
            }
        }
    }

}
