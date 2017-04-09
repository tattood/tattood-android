package com.tattood.tattod;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.android.volley.Response;

import org.json.JSONObject;

public class UserActivity extends AppCompatActivity {

    private String token;
    private RecyclerView user_liked;
    private RecyclerView user_public;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Bundle extras = getIntent().getExtras();
        token = extras.getString("token");
        String username = extras.getString("username");
        User user = new User(this, token, username);
        TextView tv = (TextView) findViewById(R.id.tv_username);
        tv.setText(username);
        user_liked = (RecyclerView) findViewById(R.id.list_liked);
        user_liked.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        user_liked.setAdapter(new TattooRecyclerViewAdapter(null, this, user_liked, token));
        user.getLiked(
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ((TattooRecyclerViewAdapter) user_liked.getAdapter()).set_data(token, response);
                    }
                });

        user_public = (RecyclerView) findViewById(R.id.list_uploaded);
        user_public.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        user_public.setAdapter(new TattooRecyclerViewAdapter(null, this, user_public, token));
        user.getPublic(
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ((TattooRecyclerViewAdapter) user_public.getAdapter()).set_data(token, response);
                    }
                });
    }

}
