package com.tattood.tattood;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import cn.hugeterry.coordinatortablayout.CoordinatorTabLayout;

public class ProfileActivity2 extends AppCompatActivity {

    private CoordinatorTabLayout layout;
//    private int[] mImageArray, mColorArray;
    private ArrayList<Fragment> mFragments;
    private final String[] mTitles = {"Public", "Private", "Liked"};
    private ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile2);
        initFragments();
        initViewPager();

        ViewPager pager = (ViewPager) findViewById(R.id.viewpager);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), mFragments, mTitles));
        TabLayout layout = (TabLayout) findViewById(R.id.tabs);
        layout.setupWithViewPager(pager);
//        layout.setTitle(User.getInstance().username);
//        layout.setupWithViewPager(mViewPager);
//        LayoutInflater inflator = (LayoutInflater) this .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View v = inflator.inflate(R.layout.layout_actionbar, null);
//        layout.getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        layout.getActionBar().setCustomView(v);
//        int[] mColorArray = new int[]{
//                android.R.color.holo_blue_light,
//                android.R.color.holo_red_light,
//                android.R.color.holo_green_light};
//        int[] imageArray = new int[] {
//                R.mipmap.ic_launcher,
//                R.mipmap.ic_launcher,
//                R.mipmap.ic_launcher
//        };
//        layout.setImageArray(imageArray, mColorArray);
//        layout.get
//        int[] mColorArray = new int[]{
//                android.R.color.holo_blue_light,
//                android.R.color.holo_red_light,
//                android.R.color.holo_orange_light,
//                android.R.color.holo_green_light};
//        layout.setImageArray(mColorArray);
    }

    private void initFragments() {
        mFragments = new ArrayList<>();
        for (String title : mTitles) {
            mFragments.add(ProfileFragment.getInstance(title));
        }
    }

    private void initViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), mFragments, mTitles));
    }
}
