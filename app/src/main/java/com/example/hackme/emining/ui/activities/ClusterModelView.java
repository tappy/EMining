package com.example.hackme.emining.ui.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.hackme.emining.R;
import com.example.hackme.emining.ui.fragments.ClusterBodyFragment;
import com.example.hackme.emining.model.DatabaseManager;
import com.example.hackme.emining.ui.fragments.HeadClusterFragment;
import com.example.hackme.emining.ui.fragments.SummaryFragment;
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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ClusterModelView extends Activity implements ActionBar.TabListener {

    private ClusterPagerAdapter pageAdapter;
    private ViewPager mViewPager;
    private ActionBar actionBar;
    private ActionBar.TabListener tabListener;
    private int cluster_class_count;
    private JSONArray jsModel;
    private String val="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cluster_model_view);
        actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setStackedBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_color));
        actionBar.setDisplayHomeAsUpEnabled(true);
        tabListener = this;
        Bundle bundle = getIntent().getExtras();
        cluster_class_count = bundle.getInt("class_count");
        if(bundle.getString("valueModel").equals("")){
            cleateTab();
            forceTabs();
        }else{
         //localfile
        }
    }

    public void forceTabs() {
        try {
            final ActionBar actionBar = getActionBar();
            final Method setHasEmbeddedTabsMethod = actionBar.getClass()
                    .getDeclaredMethod("setHasEmbeddedTabs", boolean.class);
            setHasEmbeddedTabsMethod.setAccessible(true);
            setHasEmbeddedTabsMethod.invoke(actionBar, false);
        }
        catch(final Exception e) {
            Log.e("Handle issues","Handle issues");
        }
    }

    public void cleateTab() {

        pageAdapter = new ClusterPagerAdapter(getFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.cluster_pager);
        mViewPager.setAdapter(pageAdapter);
        mViewPager.setOffscreenPageLimit(cluster_class_count + 5);

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });


        for (int i = 0; i < pageAdapter.getCount(); i++) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(pageAdapter.getPageTitle(i))
                                    //.setIcon(pageAdapter.getIcon(i))
                            .setTabListener(tabListener));
        }

    }

    public void loadClusterContent() {
        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... params) {
                try {
                    StringBuilder builder = new StringBuilder();
                    HttpClient client = new DefaultHttpClient();
                    HttpPost post = new HttpPost(new WebServiceConfig().getHost("getClusterModel.php"));
                    List<NameValuePair> list = new ArrayList<NameValuePair>();
                    list.add(new BasicNameValuePair("getParam",params[0]));
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
            protected void onPostExecute(String s) {

                try {
                        jsModel = new JSONArray(s);
                        for (int i = 0; i < jsModel.length()-1; i++) {
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

    public void loadClusterContentforSave() {
        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... params) {
                try {
                    StringBuilder builder = new StringBuilder();
                    HttpClient client = new DefaultHttpClient();
                    HttpPost post = new HttpPost(new WebServiceConfig().getHost("getClusterModel.php"));
                    List<NameValuePair> list = new ArrayList<NameValuePair>();
                    list.add(new BasicNameValuePair("getParam",params[0]));
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
            protected void onPostExecute(String s) {

                try {
                    jsModel = new JSONArray(s);
                    for (int i = 0; i < jsModel.length()-1; i++) {
                        val += jsModel.getString(i);
                    }
                    new SaveModelFile(ClusterModelView.this, val).setFileName(0);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        }.execute("full_data", new DatabaseManager(getBaseContext()).getLoginId());
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cluster_model_view, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_saveModel) {
            loadClusterContent();
            return true;
        } else if (id == R.id.action_saveModelFile) {
            loadClusterContentforSave();
        }else if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    public class ClusterPagerAdapter extends android.support.v13.app.FragmentPagerAdapter {

        public ClusterPagerAdapter(android.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.app.Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return HeadClusterFragment.newInstance();
                case 1:
                    return SummaryFragment.newInstance();
                case 2:
                    return ClusterBodyFragment.newInstance("body", 1);
                default:
                    return ClusterBodyFragment.newInstance("body", position - 1);
            }
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return cluster_class_count + 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "รายละเอียด";
                case 1:
                    return "ข้อมูลสรุป";
                case 2:
                    return "ทั้งหมด";
                default:
                    return "กลุ่ม " + (position - 3);
            }
        }
    }

}
