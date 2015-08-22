package com.example.hackme.emining;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

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

public class Apriori_model_view extends Activity implements ActionBar.TabListener {

    private ActionBar myActionBar;
    private SectionsPagerAdapter sectionsPagerAdapter;
    private ViewPager pager;
    private JSONArray jsModel;
    private String val="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apriori_model_view);
        myActionBar = getActionBar();
        myActionBar.setDisplayHomeAsUpEnabled(true);
        myActionBar.setNavigationMode(myActionBar.NAVIGATION_MODE_TABS);
        myActionBar.setStackedBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_color));
        Bundle bundle=getIntent().getExtras();
        if(bundle.getString("valueModel").equals("")){
            sectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
            pager = (ViewPager) findViewById(R.id.aprioripager);
            pager.setAdapter(sectionsPagerAdapter);
            pager.setOffscreenPageLimit(5);

            pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    myActionBar.setSelectedNavigationItem(position);
                }
            });

            for (int i = 0; i < sectionsPagerAdapter.getCount(); i++) {
                myActionBar.addTab(myActionBar.newTab().setText(sectionsPagerAdapter.getPageTitle(i)).setTabListener(this));
            }
        }else{
         //localfile
        }
    }

    public void loadAprioriContent() {
        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... params) {
                try {
                    StringBuilder builder = new StringBuilder();
                    HttpClient client = new DefaultHttpClient();
                    HttpPost post = new HttpPost(new webServiceConfig().getHost("getAprioryModel.php"));
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

        }.execute("full_data", new database_manager(getBaseContext()).getLoginId());
    }

    public void loadAprioriContentforSave() {
        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... params) {
               return loadData(params);
            }

            @Override
            protected void onPostExecute(String s) {

                try {
                    jsModel = new JSONArray(s);
                    for (int i = 0; i < jsModel.length()-1; i++) {
                        val += jsModel.getString(i);
                    }
                    new saveModelFile(Apriori_model_view.this,val).setFileName(2);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        }.execute("full_data",new database_manager(getBaseContext()).getLoginId());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_apriori_model_view, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private String loadData(String...params){
        try {
            StringBuilder builder = new StringBuilder();
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(new webServiceConfig().getHost("getAprioryModel.php"));
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
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        } else if (id == R.id.action_saveModel) {
            loadAprioriContent();
        }else if (id == R.id.action_saveModelFile) {
            loadAprioriContentforSave();
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
                case 0: {
                    return apriori_summary_frag.newInstance();
                }
                case 1: {
                    return apriori_total_summary.newInstance();
                }
                case 2: {
                    return apriori_body_frag.newInstance();
                }
                default:
                    return apriori_body_frag.newInstance();
            }
        }

        @Override
        public int getCount() {
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
                    return "กฎที่ดีที่สุด";
                default:
                    return "";
            }
        }
    }

}
