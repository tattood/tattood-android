package com.tattood.tattood;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

public class ProfileActivity2 extends AppCompatActivity {

//    private int[] mImageArray, mColorArray;
    private ArrayList<Fragment> mFragments;
    private final String[] mTitles = {"Public", "Private", "Liked"};
    TabLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile2);
        layout = (TabLayout) findViewById(R.id.tabs);
        initViewPager();
        final User user = User.getInstance();
        String url = String.valueOf(user.photo);
        final SimpleDraweeView img = (SimpleDraweeView) findViewById(R.id.user_image);
        img.setImageURI(url);

        TextView tv_user = (TextView) findViewById(R.id.owner_name);
        tv_user.setText(user.username);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
                    collapsingToolbarLayout.setTitle(user.username);
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
            mFragments.add(ProfileFragment.getInstance(title, null));
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
