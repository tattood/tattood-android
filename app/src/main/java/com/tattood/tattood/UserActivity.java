package com.tattood.tattood;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

public class UserActivity extends AppCompatActivity {

    private ArrayList<Fragment> mFragments;
    private final String[] mTitles = {"Public", "Liked"};
    private TabLayout layout;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        layout = (TabLayout) findViewById(R.id.tabs);
        Bundle extras = getIntent().getExtras();
        username = extras.getString("username");
        String other_user_photo = extras.getString("other_user_photo");
        initViewPager();
        final SimpleDraweeView  img = (SimpleDraweeView ) findViewById(R.id.user_image);
        img.setImageURI(other_user_photo);
        TextView tv_user = (TextView) findViewById(R.id.owner_name);
        tv_user.setText(username);
        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(username);
                    isShow = true;
                } else if(isShow) {
                    collapsingToolbarLayout.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    private void initFragments() {
        mFragments = new ArrayList<>();
        for (String title : mTitles) {
            mFragments.add(ProfileFragment.getInstance(title, username));
        }
    }

    private void initViewPager() {
        initFragments();
        ViewPager pager = (ViewPager) findViewById(R.id.viewpager);
        pager.setOffscreenPageLimit(4);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), mFragments, mTitles));
        layout.setupWithViewPager(pager);
    }
}
