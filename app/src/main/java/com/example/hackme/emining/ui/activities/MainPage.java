package com.example.hackme.emining.ui.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.renderscript.Element;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.example.hackme.emining.R;
import com.example.hackme.emining.ui.fragments.ViewModelFragment;
import com.example.hackme.emining.ui.fragments.DataManagerFragment;
import com.example.hackme.emining.model.DatabaseManager;
import com.example.hackme.emining.ui.fragments.SettingFragment;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainPage extends AppCompatActivity {

    public static ProgressDialog progressDialog;
    public DatabaseManager dbms;
    public int lastSelectPage = 0;
    private DatabaseManager dbm;
    private AlertDialog.Builder al;
    private long back_pressed;
    private PagerSlidingTabStrip tabs;
    private SectionsPagerAdapter sectionsPagerAdapter;
    private ViewPager pager;
    public final Integer tempIconId[] = {R.drawable.ic_file_file_upload, R.drawable.ic_action_trending_up, R.drawable.ic_action_perm_identity};
    public final Integer tempIconDarkId[] = {R.drawable.ic_action_file_file_upload, R.drawable.ic_action_action_trending_up, R.drawable.ic_action_social_person_outline};
    public List<Integer> iconId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbms = new DatabaseManager(getBaseContext());
        dbms.getWritableDatabase();
        if (!dbms.existUser()) {
            Intent intent = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(intent);
        }
        setContentView(R.layout.activity_main_page);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setStackedBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.actionbar_color));
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setTextColorResource(R.color.text_color);
        tabs.setBackgroundResource(R.color.baseColor);
        tabs.setIndicatorColorResource(R.color.baseColor);
        iconId = new ArrayList<>(Arrays.asList(tempIconId));
        iconId.set(0, tempIconDarkId[0]);
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(sectionsPagerAdapter);
        pager.setOffscreenPageLimit(5);
        pager.setCurrentItem(0);
        tabs.setViewPager(pager);
        getSupportActionBar().setTitle(sectionsPagerAdapter.getPageTitle(0));
        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                getSupportActionBar().setTitle(sectionsPagerAdapter.getPageTitle(position));
                onTabSelected(position);
                tabs.notifyDataSetChanged();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        dbm = new DatabaseManager(this);
        al = new AlertDialog.Builder(this);
        forceTabs();
    }

    public void forceTabs() {
        try {
            final ActionBar actionBar = getSupportActionBar();
            final Method setHasEmbeddedTabsMethod = actionBar.getClass()
                    .getDeclaredMethod("setHasEmbeddedTabs", boolean.class);
            setHasEmbeddedTabsMethod.setAccessible(true);
            setHasEmbeddedTabsMethod.invoke(actionBar, false);
        } catch (final Exception e) {
            Log.e("Handle issues", "Handle issues");
        }
    }

    @Override
    public Dialog onCreateDialog(int id) {
        switch (id) {
            case 0:
                progressDialog = new ProgressDialog(this);
                progressDialog.setTitle(getString(R.string.upload));
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setMessage(getString(R.string.updating));
                progressDialog.setIcon(android.R.drawable.stat_sys_upload);
                progressDialog.setCancelable(false);
                progressDialog.show();
                return progressDialog;
        }
        return null;
    }

    @Override
    protected void onRestart() {
        if (!dbms.existUser()) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        super.onRestart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_page, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis()) super.onBackPressed();
        else Toast.makeText(getBaseContext(), "กดอีกครั้งเพื่อปิด", Toast.LENGTH_SHORT).show();
        back_pressed = System.currentTimeMillis();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            al.setTitle(getString(R.string.confirm));
            al.setMessage(getString(R.string.conf_logO_text));
            al.setNegativeButton(getString(R.string.okBtn), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dbm.logout_action();
                }
            });
            al.setPositiveButton(getString(R.string.cancelBtn), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //cancel
                }
            });
            al.setCancelable(true);
            Dialog d = al.create();
            d.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void onTabSelected(int position) {
        pager.setCurrentItem(position);
        if (position == 1 && lastSelectPage < 1) {
            Animation animation = AnimationUtils.loadAnimation(getBaseContext(), R.anim.showing);
            ImageButton imageButton = (ImageButton) findViewById(R.id.imageButton2);
            imageButton.startAnimation(animation);
        }
        iconId.set(position, tempIconDarkId[position]);
        iconId.set(lastSelectPage, tempIconId[lastSelectPage]);
        lastSelectPage = position;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter implements PagerSlidingTabStrip.IconTabProvider {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return DataManagerFragment.newInstance();
                case 1:
                    return ViewModelFragment.newInstance();
                case 2:
                    return SettingFragment.newInstance();
                default:
                    return null;
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
                    return getString(R.string.title_section1);
                case 1:
                    return getString(R.string.title_section2);
                case 2:
                    return getString(R.string.title_section3);
                default:
                    return getString(R.string.title_section3);
            }
        }

        @Override
        public int getPageIconResId(int position) {
            return iconId.get(position);
        }
    }
}
