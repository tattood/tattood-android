package com.tattood.tattood;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

public class UserActivity extends AppCompatActivity {

    private ArrayList<Fragment> mFragments;
    private final String[] mTitles = {"Public", "Liked"};
    private ViewPager mViewPager;
    String username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Bundle extras = getIntent().getExtras();
        username = extras.getString("username");
        String other_user_photo = extras.getString("other_user_photo");
        initFragments();
        initViewPager();
        TextView tv = (TextView) findViewById(R.id.owner_name);
        tv.setText(username);
        final SimpleDraweeView  img = (SimpleDraweeView ) findViewById(R.id.user_image);
        img.setImageURI(other_user_photo);

        ViewPager pager = (ViewPager) findViewById(R.id.viewpager);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), mFragments, mTitles));
        TabLayout layout = (TabLayout) findViewById(R.id.tabs);
        layout.setupWithViewPager(pager);
    }

    private void initFragments() {
        mFragments = new ArrayList<>();
        for (String title : mTitles) {
            mFragments.add(ProfileFragment.getInstance(title, username));
        }
    }

    private void initViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), mFragments, mTitles));
    }
}
