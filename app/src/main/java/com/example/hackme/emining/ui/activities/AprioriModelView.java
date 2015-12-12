package com.example.hackme.emining.ui.activities;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;
import com.example.hackme.emining.R;
import com.example.hackme.emining.entities.GetApioriModelReq;
import com.example.hackme.emining.model.GetApioriModelLoader;
import com.example.hackme.emining.model.ModelLoader;
import com.example.hackme.emining.ui.fragments.AprioriSummaryFragment;
import com.example.hackme.emining.ui.fragments.AprioriTotalSummaryFragment;
import com.example.hackme.emining.model.DatabaseManager;
import com.example.hackme.emining.ui.fragments.AprioriBodyFragment;

import org.json.JSONArray;

public class AprioriModelView extends AppCompatActivity {

    private ActionBar myActionBar;
    private SectionsPagerAdapter sectionsPagerAdapter;
    private ViewPager pager;
    private PagerSlidingTabStrip tabs;
    private JSONArray jsModel;
    private String val = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apriori_model_view);
        myActionBar = getSupportActionBar();
        myActionBar.setDisplayHomeAsUpEnabled(true);
        myActionBar.setStackedBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.actionbar_color));
        Bundle bundle = getIntent().getExtras();
        if (bundle.getString("valueModel").equals("")) {

            tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
            sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
            pager = (ViewPager) findViewById(R.id.aprioripager);
            pager.setAdapter(sectionsPagerAdapter);
            pager.setOffscreenPageLimit(5);
            tabs.setViewPager(pager);
            tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {

                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

        } else {
            //localfile
        }
    }

    public void loadAprioriContent() {
        loadData(new ModelLoader.DataLoadingListener() {
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
    }

    public void loadAprioriContentforSave() {
        loadData(new ModelLoader.DataLoadingListener() {
            @Override
            public void onLoaded(String data) {
                try {
                    try {
                        jsModel = new JSONArray(data);
                        for (int i = 0; i < jsModel.length() - 1; i++) {
                            val += jsModel.getString(i);
                        }
                        new SaveModelFile(AprioriModelView.this, val).setFileName(2);
                    } catch (Exception ex) {
                        ex.printStackTrace();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_apriori_model_view, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void loadData(ModelLoader.DataLoadingListener listener) {
        GetApioriModelReq req = new GetApioriModelReq();
        req.param = "full_data";
        req.userId = new DatabaseManager(getBaseContext()).getLoginId();
        new GetApioriModelLoader(req, listener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        } else if (id == R.id.action_saveModel) {
            loadAprioriContent();
        } else if (id == R.id.action_saveModelFile) {
            loadAprioriContentforSave();
        }
        return true;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0: {
                    return AprioriSummaryFragment.newInstance();
                }
                case 1: {
                    return AprioriTotalSummaryFragment.newInstance();
                }
                case 2: {
                    return AprioriBodyFragment.newInstance();
                }
                default:
                    return AprioriBodyFragment.newInstance();
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
