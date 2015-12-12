package com.example.hackme.emining.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.astuetz.PagerSlidingTabStrip;
import com.example.hackme.emining.R;
import com.example.hackme.emining.entities.GetClusterModelReq;
import com.example.hackme.emining.model.GetClusterModelLoader;
import com.example.hackme.emining.model.ModelLoader;
import com.example.hackme.emining.model.SaveModelFile;
import com.example.hackme.emining.ui.fragments.ClusterBodyFragment;
import com.example.hackme.emining.model.DatabaseManager;
import com.example.hackme.emining.ui.fragments.HeadClusterFragment;
import com.example.hackme.emining.ui.fragments.SummaryFragment;

import org.json.JSONArray;

public class ClusterModelView extends AppCompatActivity {

    private ViewPager pager;
    private ActionBar actionBar;
    private int cluster_class_count;
    private JSONArray jsModel;
    private String val = "";
    private PagerSlidingTabStrip tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cluster_model_view);
        actionBar = getSupportActionBar();
        actionBar.setStackedBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.actionbar_color));
        actionBar.setDisplayHomeAsUpEnabled(true);
        Bundle bundle = getIntent().getExtras();
        cluster_class_count = bundle.getInt("class_count");
        if (bundle.getString("valueModel").equals("")) {
            tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
            tabs.setDividerColorResource(R.color.baseColorPlus);
            tabs.setBackgroundResource(R.color.baseColor);
            tabs.setTextColorResource(R.color.text_color);
            ClusterPagerAdapter clusterPagerAdapter = new ClusterPagerAdapter(getSupportFragmentManager());
            pager = (ViewPager) findViewById(R.id.cluster_pager);
            pager.setAdapter(clusterPagerAdapter);
            pager.setOffscreenPageLimit(5);
            tabs.setViewPager(pager);
        }
    }

    public void loadClusterContent(ModelLoader.DataLoadingListener listener) {
        GetClusterModelReq req = new GetClusterModelReq();
        req.param = "full_data";
        req.userId = new DatabaseManager(getBaseContext()).getLoginId();
        new GetClusterModelLoader(req, listener);
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
            loadClusterContent(new ModelLoader.DataLoadingListener() {
                @Override
                public void onLoaded(String data) {
                    try {
                        jsModel = new JSONArray(data);
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

                @Override
                public void onFailed(String message) {

                }
            });
            return true;
        } else if (id == R.id.action_saveModelFile) {
            loadClusterContent(new ModelLoader.DataLoadingListener() {
                @Override
                public void onLoaded(String data) {
                    try {
                        jsModel = new JSONArray(data);
                        for (int i = 0; i < jsModel.length() - 1; i++) {
                            val += jsModel.getString(i);
                        }
                        new SaveModelFile(ClusterModelView.this, val).setFileName(0);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public void onFailed(String message) {

                }
            });
        } else if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    public class ClusterPagerAdapter extends FragmentPagerAdapter {

        public ClusterPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

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
