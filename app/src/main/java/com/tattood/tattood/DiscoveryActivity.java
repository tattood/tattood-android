package com.tattood.tattood;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.android.volley.Response;

import org.json.JSONObject;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class DiscoveryActivity extends AppCompatActivity implements View.OnClickListener {

    private OnListFragmentInteractionListener mListener;
    private String token;
    private String username;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discovery);
        token = getIntent().getExtras().getString("token");
        username = getIntent().getExtras().getString("username");

        final RecyclerView popular_view = (RecyclerView) findViewById(R.id.popular_list);
        popular_view.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        popular_view.setAdapter(new TattooRecyclerViewAdapter(mListener, this, popular_view, token));
        Server.getPopular(this,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ((TattooRecyclerViewAdapter) popular_view.getAdapter()).set_data(token, response);
                    }
                });

        final RecyclerView recent_view = (RecyclerView) findViewById(R.id.recent_list);
        recent_view.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recent_view.setAdapter(new TattooRecyclerViewAdapter(mListener, this, recent_view, token));
        Server.getRecent(this,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ((TattooRecyclerViewAdapter) recent_view.getAdapter()).set_data(token, response);
                    }
                });

        Button see_more = (Button) findViewById(R.id.see_more_recent);
        see_more.setOnClickListener(this);
        see_more = (Button) findViewById(R.id.see_more_popular);
        see_more.setOnClickListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_profile);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DiscoveryActivity.this, ProfileActivity.class);
                i.putExtra("token", token);
                i.putExtra("username", username);
                startActivity(i);
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent myIntent = new Intent(this, SeeMore.class);
        if (v.getId() == R.id.see_more_recent) {
            myIntent.putExtra("TAG", "RECENT");
        } else if (v.getId() == R.id.see_more_popular) {
            myIntent.putExtra("TAG", "POPULAR");
        }
        myIntent.putExtra("token", token);
        startActivity(myIntent);
    }
}
