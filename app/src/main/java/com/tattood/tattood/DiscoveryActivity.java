package com.tattood.tattood;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;

import com.android.volley.Response;

import org.json.JSONObject;

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
        User.setInstance(token, username);
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
                Intent myIntent = new Intent(DiscoveryActivity.this, ProfileActivity.class);
                myIntent.putExtra("token", token);
                myIntent.putExtra("username", username);
                ActivityOptions options =
                        ActivityOptions.makeCustomAnimation(DiscoveryActivity.this,
                                android.R.anim.slide_in_left,
                                android.R.anim.slide_out_right);
                startActivity(myIntent, options.toBundle());
            }
        });

        SearchView search = (SearchView) findViewById(R.id.search_view);
        search.setOnSearchClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.d("Search", "OnSearch");
            }
        });
        search.setOnCloseListener(new SearchView.OnCloseListener(){
            @Override
            public boolean onClose() {
                return false;
            }
        });
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent myIntent = new Intent(DiscoveryActivity.this, SearchActivity.class);
                myIntent.putExtra("query", query);
                myIntent.putExtra("token", token);
                startActivity(myIntent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
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
