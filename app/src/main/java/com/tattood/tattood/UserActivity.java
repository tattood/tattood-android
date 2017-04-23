package com.tattood.tattood;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.net.URI;

public class UserActivity extends AppCompatActivity {

    private RecyclerView user_liked;
    private RecyclerView user_public;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Bundle extras = getIntent().getExtras();
        String username = extras.getString("username");
        URI url = URI.create(extras.getString("photo-uri"));
        User user = User.getInstance();
        TextView tv = (TextView) findViewById(R.id.tv_username);
        tv.setText(username);
        final ImageView img = (ImageView) findViewById(R.id.user_image);
        BasicImageDownloader dl = new BasicImageDownloader(img);
        Log.d("PROFILE1", String.valueOf(url));
        dl.execute(String.valueOf(url));
        user_liked = (RecyclerView) findViewById(R.id.list_liked);
        user_liked.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        user_liked.setAdapter(new TattooRecyclerViewAdapter(null, this, user_liked));
        user.setLikedView(this, user_liked);

        user_public = (RecyclerView) findViewById(R.id.list_uploaded);
        user_public.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        user_public.setAdapter(new TattooRecyclerViewAdapter(null, this, user_public));
        user.setPublicView(this, user_public);
    }

}
