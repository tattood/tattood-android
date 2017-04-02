package com.tattood.tattod;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Response;

import org.json.JSONObject;

public class UserActivity extends AppCompatActivity {

    private User user;
    private String token;
    private String username;
    private RecyclerView user_liked;
    private RecyclerView user_public;

    private OnListFragmentInteractionListener liked_listener;
    private OnListFragmentInteractionListener public_listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Bundle extras = getIntent().getExtras();
        token = extras.getString("token");
        username = extras.getString("username");
//        String user_id = extras.getString("uid");
        user = new User(this, token, username);
        TextView tv = (TextView) findViewById(R.id.tv_username);
        tv.setText(username);
        user_liked = (RecyclerView) findViewById(R.id.list_liked);
//        liked_listener = new OnListFragmentInteractionListener();
        liked_listener = null;
        user_liked.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        user_liked.setAdapter(new TattooRecyclerViewAdapter(liked_listener, this, user_liked, 25));
        user.getLiked(
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ((TattooRecyclerViewAdapter) user_liked.getAdapter()).set_data(token, response);
                    }
                });

        user_public = (RecyclerView) findViewById(R.id.list_uploaded);
//        public_listener = new OnListFragmentInteractionListener();
        public_listener = null;
        user_public.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        user_public.setAdapter(new TattooRecyclerViewAdapter(public_listener, this, user_public, 25));
        user.getPublic(
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ((TattooRecyclerViewAdapter) user_public.getAdapter()).set_data(token, response);
                    }
                });
    }

}
