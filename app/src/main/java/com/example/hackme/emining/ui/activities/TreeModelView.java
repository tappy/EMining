package com.example.hackme.emining.ui.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;
import com.example.hackme.emining.R;
import com.example.hackme.emining.entities.TreeModelReq;
import com.example.hackme.emining.model.DatabaseManager;
import com.example.hackme.emining.model.GetTreeModelLoader;
import com.example.hackme.emining.model.ModelLoader;
import com.example.hackme.emining.ui.fragments.TreeBodyFragment;
import com.example.hackme.emining.ui.fragments.TreeSummaryFragment;
import com.example.hackme.emining.ui.fragments.TreeTotalSummary;

import org.json.JSONArray;


public class TreeModelView extends AppCompatActivity {

    private ActionBar myActionBar;
    private SectionsPagerAdapter sectionsPagerAdapter;
    private ViewPager pager;
    private PagerSlidingTabStrip tabs;
    private JSONArray jsModel;
    private String val = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tree_model_view);

        myActionBar = getSupportActionBar();
        myActionBar.setDisplayHomeAsUpEnabled(true);
        myActionBar.setNavigationMode(myActionBar.NAVIGATION_MODE_TABS);
        myActionBar.setStackedBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_color));

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

    public void loadTreeContent() {
        TreeModelReq req = new TreeModelReq();
        req.param = "full_data";
        req.userid = new DatabaseManager(getBaseContext()).getLoginId();
        loadData(req, new ModelLoader.DataLoadingListener() {
            @Override
            public void onLoaded(String data) {
                try {
                    jsModel = new JSONArray(data);
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

            @Override
            public void onFailed(String message) {

            }
        });
    }

    public void loadTreeContentforSave() {

        TreeModelReq req = new TreeModelReq();
        req.param = "full_data";
        req.userid = new DatabaseManager(getBaseContext()).getLoginId();
        loadData(req, new ModelLoader.DataLoadingListener() {
            @Override
            public void onLoaded(String data) {
                try {
                    jsModel = new JSONArray(data);
                    for (int i = 0; i < jsModel.length() - 1; i++) {
                        val += jsModel.getString(i);
                    }
                    new SaveModelFile(TreeModelView.this, val).setFileName(1);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailed(String message) {

            }
        });
    }

    private void saveTree() {
        new SaveModelFile(TreeModelView.this, TreeBodyFragment.line, "html").setFileName(1);
    }

    private void loadData(TreeModelReq req, ModelLoader.DataLoadingListener listener) {
        new GetTreeModelLoader(req, listener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tree_model_view, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
